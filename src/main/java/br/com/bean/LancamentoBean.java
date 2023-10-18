package br.com.bean;

import java.io.Serializable;
import java.text.DecimalFormat;
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
import br.com.model.Company;
import br.com.model.Lancamento;
import br.com.model.Pessoa;
import br.com.repository.IDaoCompany;
import br.com.repository.IDaoLancamento;

@Named(value= "lancamentoBean")
@javax.faces.view.ViewScoped
public class LancamentoBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Lancamento lancamento = new Lancamento();
	
	private Company company = new Company();

	private List<Lancamento> lancamentos = new ArrayList<Lancamento>();
	private List<Lancamento> launchesReview = new ArrayList<Lancamento>();
	private List<Company> companies = new ArrayList<Company>();
	
	@Inject
	private DaoGeneric<Lancamento> daoGeneric;
	
	@Inject
	private DaoGeneric<Company> daoGenericCompany;
	
	@Inject
	private IDaoLancamento daoLancamento;	
	
	@Inject
	private IDaoCompany daoCompany;	
	
	
	
	
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
	
	public List<Company> getCompanies() {
		return companies;
	}
	
	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}
	
	public Company getCompany() {
		return company;
	}
	
	public void setCompany(Company company) {
		this.company = company;
	}
	/*  get and set --- END*/
	
	

	/*  Methods related to LAUNCH(lancamento) --- BEGIN*/
	@PostConstruct    // method activated whenever the page is loaded
	private void carregarLancamentos() {
		FacesContext context = FacesContext.getCurrentInstance();  			
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		lancamento.setDataIni(new Date());
		lancamentos = daoLancamento.findLaunches(); 
		companies = daoGenericCompany.getListEntity(Company.class);
		launchesReview = daoLancamento.underAprovalLaunchs("under review"); 
	}
	
	/* save launch*/
	public String salvar () { 
		FacesContext context = FacesContext.getCurrentInstance();  			
		ExternalContext externalContext = context.getExternalContext();
		// recover the logged in user and assign a user to the launch
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");  
		lancamento.setUsuario(pessoaUser);
		
		if (lancamento.getDataFim().before(lancamento.getDataIni())) {
			FacesContext.getCurrentInstance().addMessage("msg-launch", new FacesMessage
					("Delivery Date invalid. That must be later than current Date."));
			return "";
		}
		if (lancamento.getStatus() == null) {
			lancamento.setStatus("under review");
		}
				
		// daoGeneric.salvar(lancamento); Method 'merge' works better
		lancamento = daoGeneric.merge(lancamento);
		
		FacesContext.getCurrentInstance().addMessage("msg-launch", new FacesMessage("Successfully saved."));
		carregarLancamentos();
		lancamento = new Lancamento();
		lancamento.setDataIni(new Date());
		return "";
	}
	
	/* save new launch status */
	public String saveNewStatus() {
		if (lancamento.getId() == null) {
			FacesContext.getCurrentInstance().addMessage("msg-launch", new FacesMessage("Choose a Launch to update."));
		}
		else if (lancamento.getId() != null && lancamento.getStatus().equalsIgnoreCase("REJECTED")  && lancamento.getReason() == "") {
			lancamento.setStatus("under review");
			FacesContext.getCurrentInstance().addMessage("msg-launch", new FacesMessage("Please write a reson for your decison."));
		} else {
			lancamento = daoGeneric.merge(lancamento);
			FacesContext.getCurrentInstance().addMessage("msg-launch", new FacesMessage("New status sucessfully updated."));
			carregarLancamentos();
			lancamento = new Lancamento();
		}
		return "";
	}
	
	/* new launch => clean formular*/
	public String novo() {
		lancamento = new Lancamento();
		lancamento.setDataIni(new Date());
		return "";
	}

	/* remove launch*/
	public String remover() {
		daoGeneric.deletarPorId(lancamento);
		lancamento = new Lancamento();
		carregarLancamentos();
		FacesContext.getCurrentInstance().addMessage("msg-launch", new FacesMessage("Successfully removed."));
		return "";
	}
	/*  Methods related to LAUNCH(lancamento) --- END*/
	
	
	
	/*  Methods related to COMAPNY --- BEGIN*/
	public String saveCompany() {
		String nameCompany = company.getName();
		
		if (daoCompany.noRepeatCompany(nameCompany)) {
			company = daoGenericCompany.merge(company);
			FacesContext.getCurrentInstance().addMessage("msg-launch", new FacesMessage("Successfully saved."));
			companies = daoGenericCompany.getListEntity(Company.class);
			company = new Company();
		}
		else {
			FacesContext.getCurrentInstance().addMessage("msg-launch", new FacesMessage("There is already a partner company with this name."));
		}
		
		return "";
	}
	
	/* clean formular*/
	public String newCompany() {
		company = new Company();
		return "";
	}
	
	/* remove company*/
	public String removeCompany() {
		try {
			daoGenericCompany.deletarPorId(company);
			company = new Company();
			companies = daoGenericCompany.getListEntity(Company.class);
			FacesContext.getCurrentInstance().addMessage("msg-launch", new FacesMessage("Successfully removed."));
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage("msg-launch", new FacesMessage
					("This company cannot be excluded from the database because there is one or more Launches associated with it."));
		}
		return "";
	}
	/*  Methods related to COMPANY --- END*/
	
	
	
	
} 
