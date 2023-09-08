package br.com.cursojsf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.dao.DaoGeneric;
import br.com.entidades.Lancamento;
import br.com.entidades.Pessoa;
import br.com.repository.IDaoLancamento;

@Named(value= "lancamentoBean")
@javax.faces.view.ViewScoped
public class LancamentoBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	private Lancamento lancamento = new Lancamento();
	private List<Lancamento> lancamentos = new ArrayList<Lancamento>();
	
	@Inject
	private DaoGeneric<Lancamento> daoGeneric;
	
	@Inject
	private IDaoLancamento daoLancamento;	
	
	
	
	/*  get and set --- BEGIN*/	
	public Lancamento getLancamento() {
		return lancamento;
	}

	public void setLancamento(Lancamento lancamento) {
		this.lancamento = lancamento;
	}

	public DaoGeneric<Lancamento> getDaoGEneric() {
		return daoGeneric;
	}

	public void setDaoGEneric(DaoGeneric<Lancamento> daoGEneric) {
		this.daoGeneric = daoGEneric;
	}

	public List<Lancamento> getLancamentos() {
		return lancamentos;
	}

	public void setLancamentos(List<Lancamento> lancamentos) {
		this.lancamentos = lancamentos;
	}	
	/*  get and set --- END*/
	
	
	/*  Methods --- BEGIN*/
	public String salvar () { 
		FacesContext context = FacesContext.getCurrentInstance();  			
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");  
		// recupera o usuario logado e adiciona um lancamento
		lancamento.setUsuario(pessoaUser);
		
		// daoGeneric.salvar(lancamento); antes era essa linha mas estava dando problema quando editava uma lancamento e salvava depois
		lancamento = daoGeneric.merge(lancamento);
		
		carregarLancamentos();
		FacesContext.getCurrentInstance().addMessage("msg-launch", new FacesMessage("Salvo com sucesso!"));
		
		return "";
	}
	
	@PostConstruct    // notação que faz metodo ser executado, sempre que página é carregada.
	private void carregarLancamentos() {
		FacesContext context = FacesContext.getCurrentInstance();  			
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		lancamentos = daoLancamento.consultarLimit5(pessoaUser.getId());
		System.out.println(lancamentos);
	}
	
	
	public String novo() {
		lancamento = new Lancamento();
		
		return "";
	}
	
	public String remover() {
		daoGeneric.deletarPorId(lancamento);
		lancamento = new Lancamento();
		carregarLancamentos();
		FacesContext.getCurrentInstance().addMessage("msg-launch", new FacesMessage("Excluidocom sucesso!"));
		return "";
	}
	/*  Methods --- END*/
	
	
	
	
} 
