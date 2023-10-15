package br.com.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

//import org.hibernate.annotations.ForeignKey;

@Entity
public class Lancamento implements Serializable {


	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	private Long id;
	
	private String descricao;
	
	@ManyToOne
	@org.hibernate.annotations.ForeignKey(name="company_fk")
	private Company empresaOrigem;
	
	private Double coast;
	
	private String status;
	
	private String reason;
	
	
	@ManyToOne(optional = false)
	@org.hibernate.annotations.ForeignKey(name="usuario_fk")
	private Pessoa usuario;

	@Temporal (TemporalType.DATE)
	private Date dataIni;
	
	@Temporal (TemporalType.DATE)
	private Date dataFim;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public Company getEmpresaOrigem() {
		return empresaOrigem;
	}

	public void setEmpresaOrigem(Company empresaOrigem) {
		this.empresaOrigem = empresaOrigem;
	}

	public Pessoa getUsuario() {
		return usuario;
	}

	public void setUsuario(Pessoa usuario) {
		this.usuario = usuario;
	}
	
	public Date getDataIni() {
		return dataIni;
	}

	public void setDataIni(Date dataIni) {
		this.dataIni = dataIni;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}


	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Double getCoast() {
		return coast;
	}

	public void setCoast(Double coast) {
		this.coast = coast;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Lancamento other = (Lancamento) obj;
		return Objects.equals(id, other.id);
	}
		
}
