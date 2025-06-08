package salario.controller;

import salario.model.PessoaSalarioConsolidado;
import salario.service.SalarioService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Named
@ViewScoped
public class PessoaSalarioConsolidadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private SalarioService salarioService;

    private List<PessoaSalarioConsolidado> listaConsolidada;

    @PostConstruct
    public void init() {
        listar();
    }

    public void listar() {
        this.listaConsolidada = salarioService.listarTodos();
    }
    
    public void calcular() {
        try {
            this.listaConsolidada = salarioService.recalcularSalariosAsync();
            FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Salários recalculados com sucesso.")
            );
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao recalcular salários: " + e.getMessage())
            );
        }
    }

    public List<PessoaSalarioConsolidado> getListaConsolidada() {
        return listaConsolidada;
    }

    public void setListaConsolidada(List<PessoaSalarioConsolidado> listaConsolidada) {
        this.listaConsolidada = listaConsolidada;
    }
    public void exportarPDF() {
        try {
            // 1. Carrega o template do relatório
            InputStream template = getClass().getResourceAsStream("/jasper/salarios_consolidados.jrxml");
            
            // 2. Compila o relatório
            JasperReport report = JasperCompileManager.compileReport(template);
            
            // 3. Prepara os dados
            JRDataSource dataSource = new JRBeanCollectionDataSource(listaConsolidada);
            // 4. Preenche o relatório
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                report,
                new HashMap<>(),
                dataSource
            );
            
            // 5. Configura a resposta HTTP
            HttpServletResponse response = (HttpServletResponse) 
                FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=salarios_consolidados.pdf");
            
            // 6. Exporta para PDF
            JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
            
            FacesContext.getCurrentInstance().responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
            // Tratar erro adequadamente
        }
    }
}