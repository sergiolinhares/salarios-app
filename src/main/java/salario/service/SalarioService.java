package salario.service;

import java.io.Serializable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import salario.model.CargoVencimento;
import salario.model.Pessoa;
import salario.model.PessoaSalarioConsolidado;
import salario.model.TipoVencimento;
import salario.util.Transacional;

public class SalarioService implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public SalarioService() {
		
	}
	
	public SalarioService(EntityManager manager) {
		this.em = manager;
	}
	
	@Inject                    
    private EntityManager em;
	

    @Transacional
    public List<PessoaSalarioConsolidado> recalcularSalariosAsync() {
    	// 1. Limpeza inicial
        em.createQuery("DELETE FROM PessoaSalarioConsolidado").executeUpdate();

        // 2. Consulta todas as pessoas
        List<Pessoa> pessoas = em.createQuery("SELECT p FROM Pessoa p", Pessoa.class).getResultList();

        // 3. PRÉ-CARREGAMENTO Todos vencimentos de uma vez 
        Map<Long, List<CargoVencimento>> vencimentosPorCargo = em.createQuery(
                "SELECT cv FROM CargoVencimento cv", CargoVencimento.class)
            .getResultList()
            .stream()
            .collect(Collectors.groupingBy(
                cv -> cv.getCargo().getId(),
                Collectors.toList()
            ));

        // 4. Configurar thread pool (tamanho baseado em núcleos disponíveis)
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(corePoolSize);

        // 5. Lista para armazenar resultados antes da persistência
        List<PessoaSalarioConsolidado> resultados = Collections.synchronizedList(new ArrayList<>());

        // 6. Processar cada pessoa assíncronamente
        List<CompletableFuture<Void>> futures = pessoas.stream()
            .map(pessoa -> CompletableFuture.runAsync(() -> {
                try {
                    PessoaSalarioConsolidado consol = processarPessoa(pessoa, vencimentosPorCargo);
                    if (consol != null) {
                        resultados.add(consol);
                    }
                } catch (Exception e) {
                    // Log adequado deveria ser usado aqui
                    System.err.println("Erro ao processar pessoa " + pessoa.getId() + ": " + e.getMessage());
                }
            }, executor))
            .collect(Collectors.toList());

        // 7. Esperar conclusão de todas as tarefas
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        // 8. Encerrar executor
        executor.shutdown();
        
        // 9. Persistir resultados em batch
        persistirEmLote(resultados);
        
        // 10. Retornar resultados consolidados
        return em.createQuery("SELECT pc FROM PessoaSalarioConsolidado pc", PessoaSalarioConsolidado.class)
                 .getResultList();
    }
    
    private PessoaSalarioConsolidado processarPessoa(Pessoa pessoa, Map<Long, List<CargoVencimento>> vencimentosPorCargo) {
        if (pessoa.getCargo() == null) return null;
        
        List<CargoVencimento> listaCV = vencimentosPorCargo.get(pessoa.getCargo().getId());
        if (listaCV == null) listaCV = Collections.emptyList();
        
        BigDecimal salarioCalculado = listaCV.stream()
            .map(cv -> {
                BigDecimal valor = cv.getVencimento().getValor();
                return cv.getVencimento().getTipo() == TipoVencimento.DEBITO ? 
                       valor.negate() : valor;
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        PessoaSalarioConsolidado consol = new PessoaSalarioConsolidado();
        consol.setPessoaId(pessoa.getId());
        consol.setNomePessoa(pessoa.getNome());
        consol.setNomeCargo(pessoa.getCargo().getNome());
        consol.setSalario(salarioCalculado);
        
        return consol;
    }

    // Persistência eficiente em lotes
    private void persistirEmLote(List<PessoaSalarioConsolidado> resultados) {
        for (int i = 0; i < resultados.size(); i++) {
            em.persist(resultados.get(i));
            if (i % 100 == 0) { 
                em.flush();
                em.clear();
            }
        }
        em.flush();
        em.clear();
    }

    public List<PessoaSalarioConsolidado> listarTodos() {
        return em.createQuery("from PessoaSalarioConsolidado", PessoaSalarioConsolidado.class).getResultList();
    }

}
