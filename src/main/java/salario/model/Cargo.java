package salario.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;



@Entity
@Table(name = "cargo")
public class Cargo implements Serializable{
	private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "Nome", nullable = false, length = 120)
    private String nome;

    /* ------------ Relações ------------ */

    /** 1 Cargo → N Pessoas */
    @OneToMany(mappedBy = "cargo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Pessoa> pessoas;

    /** 1 Cargo → N associações CargoVencimento (Many-to-Many explícita) */
    @OneToMany(mappedBy = "cargo", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CargoVencimento> vencimentos;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Set<Pessoa> getPessoas() {
		return pessoas;
	}

	public void setPessoas(Set<Pessoa> pessoas) {
		this.pessoas = pessoas;
	}

	public Set<CargoVencimento> getVencimentos() {
		return vencimentos;
	}

	public void setVencimentos(Set<CargoVencimento> vencimentos) {
		this.vencimentos = vencimentos;
	}
    
    
}
