package salario.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;




@Entity
@Table(name = "cargo_vencimentos")
public class CargoVencimento implements Serializable{
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /* ------------ Relações ------------ */

    /** Muitos registros da tabela associativa pertencem a um Cargo */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARGO_ID", nullable = false)
    private Cargo cargo;

    /** Muitos registros da tabela associativa pertencem a um Vencimento */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VENCIMENTO_ID", nullable = false)
    private Vencimento vencimento;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cargo getCargo() {
		return cargo;
	}

	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}

	public Vencimento getVencimento() {
		return vencimento;
	}

	public void setVencimento(Vencimento vencimento) {
		this.vencimento = vencimento;
	}
    
    
}
