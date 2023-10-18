package br.com.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.dao.DaoGeneric;
import br.com.model.Company;
import br.com.model.Lancamento;
import br.com.model.Pessoa;
import br.com.repository.IDaoCompany;
import br.com.repository.IDaoPessoa;

@ViewScoped
@Named(value="relUsuario")
public class RelUsuario implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	
	private Date dataIni;
	
	private Date dataFim;
	
	private String nome;
	private	List<Company> companies = new ArrayList<Company>();
	

	@Inject
	private IDaoPessoa iDaoPessoa;
	
	@Inject
	private IDaoCompany iDaoCompany;
	
	@Inject
	private DaoGeneric<Pessoa> daoGeneric;

	@Inject
	private DaoGeneric<Company> daoGenericCompany;
	

	
	
	public List<Pessoa> getPessoas() {
		return pessoas;
	}

	public void setPessoas(List<Pessoa> pessoas) {
		this.pessoas = pessoas;
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

	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public List<Company> getCompanies() {
		return companies;
	}
	
	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}
	

	public void relPessoa() {
		pessoas = daoGeneric.getListEntity(Pessoa.class);		
	}

	public void reportCompanies() {
		companies = daoGenericCompany.getListEntity(Company.class);	
	}

	
	

	/*
	public void relPessoa() {
		if (dataIni == null && dataFim == null && nome == null) {
			pessoas = daoGeneric.getListEntity(Pessoa.class);
		}else {
			pessoas = iDaoPessoa.relatorioPessoa(nome, dataIni, dataFim);
		}
	}
    */
	
	
	
	
}
