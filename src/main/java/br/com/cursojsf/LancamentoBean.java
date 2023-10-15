package br.com.cursojsf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
	private List<Lancamento> launchesReview = new ArrayList<Lancamento>();
	
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
	
	public List<Lancamento> getLaunchesReview() {
		return launchesReview;
	}
	
	public void setLaunchesReview(List<Lancamento> launchesReview) {
		this.launchesReview = launchesReview;
	}
	
	
	/*  get and set --- END*/
	
	

	/*  Methods --- BEGIN*/
	public String salvar () { 
		FacesContext context = FacesContext.getCurrentInstance();  			
		ExternalContext externalContext = context.getExternalContext();
		// recover the logged in user and assign a user to the launch
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");  
		lancamento.setUsuario(pessoaUser);
		
		if (lancamento.getStatus() == null) {
			lancamento.setStatus("under review");
		}
		if (lancamento.getStatus().equalsIgnoreCase("REJECTED")  && lancamento.getReason() == "") {
			FacesContext.getCurrentInstance().addMessage("msg-launch", new FacesMessage("Please wirte a reson for your decison."));
			return "";
		}
				
		// daoGeneric.salvar(lancamento); antes era essa linha mas estava dando problema quando editava uma lancamento e salvava depois
		lancamento = daoGeneric.merge(lancamento);
		
		FacesContext.getCurrentInstance().addMessage("msg-launch", new FacesMessage("Successfully saved."));
		carregarLancamentos();
		lancamento = new Lancamento();
		return "";
	}
	

	
	
	@PostConstruct    // notação que faz metodo ser executado, sempre que página é carregada.
	private void carregarLancamentos() {
		FacesContext context = FacesContext.getCurrentInstance();  			
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		lancamento.setDataIni(new Date());
		lancamentos = daoLancamento.consultarLimit5(pessoaUser.getId());
		launchesReview = daoLancamento.underAprovalLaunchs("under review"); 
	//	System.out.println(lancamentos);
	}
	
	
	public String novo() {
		lancamento = new Lancamento();
		
		return "";
	}
	
	public String remover() {
		daoGeneric.deletarPorId(lancamento);
		lancamento = new Lancamento();
		carregarLancamentos();
		FacesContext.getCurrentInstance().addMessage("msg-launch", new FacesMessage("Successfully removed."));
		return "";
	}
	/*  Methods --- END*/
	
	
	
	
} 
