package br.com.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.dao.DaoGeneric;
import br.com.model.Lancamento;
import br.com.repository.IDaoLancamento;

@ViewScoped
@Named(value="rellancamento")
public class Rellancamento implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	private Date dataIni;
	private Date dataFim;
	
	private String descricao;

	private List<Lancamento> lancamentos = new ArrayList<Lancamento>();
	
	@Inject
	private IDaoLancamento daoLancamento;
	
	@Inject
	private DaoGeneric<Lancamento> daoGeneric;
	
	
	
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
	

	public void setLancamentos(List<Lancamento> lancamentos) {
		this.lancamentos = lancamentos;
	}
	
	public List<Lancamento> getLancamentos() {
		return lancamentos;
	}
	
	
	public void bucarLancamento() {
		if (dataIni == null && dataFim == null && descricao == null) {
			lancamentos = daoGeneric.getListEntity(Lancamento.class);
		}else {
			lancamentos = daoLancamento.relatorioLancamento(descricao, dataIni, dataFim);
		}
		
	}

	
	
	

}
