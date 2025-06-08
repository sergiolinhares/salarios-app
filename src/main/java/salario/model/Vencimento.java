package salario.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "vencimentos")
public class Vencimento implements Serializable{
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "Descricao", nullable = false, length = 120)
    private String descricao;

    @Column(name = "Valor", nullable = false, scale = 2, precision = 15)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "Tipo", nullable = false, length = 10)
    private TipoVencimento tipo;

    /* ------------ Relações ------------ */

    /** 1 Vencimento → N associações CargoVencimento */
    @OneToMany(mappedBy = "vencimento", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CargoVencimento> cargos;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public TipoVencimento getTipo() {
		return tipo;
	}

	public void setTipo(TipoVencimento tipo) {
		this.tipo = tipo;
	}

	public Set<CargoVencimento> getCargos() {
		return cargos;
	}

	public void setCargos(Set<CargoVencimento> cargos) {
		this.cargos = cargos;
	}
    
    
}
