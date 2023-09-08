package br.com.repository;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.entidades.Estados;
import br.com.entidades.Lancamento;
import br.com.entidades.Pessoa;

@Named
public class IDaoPessoImpl implements IDaoPessoa, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Inject
	private EntityManager entityManager;

	@Override
	public Pessoa consultarUsuario(String login, String senha) {
		Pessoa pessoa = null;
		
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		
		pessoa = (Pessoa) entityManager.createQuery("select p from Pessoa p where p.login = '" + login + "' and p.senha = '" + senha +"'").
				getSingleResult();		
		
		entityTransaction.commit();		
		
		return pessoa;
	}


	@Override
	public List<SelectItem> listaEstados() {
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		
		List<Estados> estados = entityManager.createQuery("from Estados").getResultList();
		
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		for (Estados estado : estados) {
			selectItems.add(new SelectItem(estado.getId(), estado.getNome()));
		}
		
		return selectItems;
	}


	@Override
	public List<Pessoa> relatorioPessoa(String nome, Date dataIni, Date dataFim) {
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select l from Pessoa l ");
		
		if (dataIni == null && dataFim == null && nome != null && !nome.isEmpty()) {    // data null mas numerdif de null
			sql.append(" where upper(l.nome) like '%").append(nome.trim().toUpperCase()).append("%'");   // nome is a String , nome.trim() tira o espaco
		}
		else if (nome == null || (nome != null && nome.isEmpty())   // nota null ou nao mas data fim é null
				&& dataIni != null && dataFim == null) {
			
			String dataIniString = new SimpleDateFormat("yyyy-MM-dd").format(dataIni);
			sql.append(" where l.dataNascimento >= '").append(dataIniString).append("'");
		}
		else if (nome == null || (nome != null && nome.isEmpty())  // nota null ou nao mas data ini é null
				&& dataIni == null && dataFim != null) {
			
			String datafimString = new SimpleDateFormat("yyyy-MM-dd").format(dataFim);
			sql.append(" where l.dataNascimento <= '").append(datafimString).append("'");
		} 
		else if (nome == null || (nome != null && nome.isEmpty())    // data null e as duas datas informadas not ntull
				&& dataIni != null && dataFim != null) {
			
			String dataIniString = new SimpleDateFormat("yyyy-MM-dd").format(dataIni);
			String datafimString = new SimpleDateFormat("yyyy-MM-dd").format(dataFim);
			
			sql.append(" where l.dataNascimento >= '").append(dataIniString).append("' ");
			sql.append(" and l.dataNascimento <= '").append(datafimString).append("' ");
		}
		else if (nome != null && !nome.isEmpty()        // nada é null
				&& dataIni != null && dataFim != null) {
			
			String dataIniString = new SimpleDateFormat("yyyy-MM-dd").format(dataIni);
			String datafimString = new SimpleDateFormat("yyyy-MM-dd").format(dataFim);
			
			sql.append(" where l.dataNascimento >= '").append(dataIniString).append("' ");
			sql.append(" and l.dataNascimento <= '").append(datafimString).append("' ");
			sql.append(" and upper(l.nome) like '%").append(nome.trim().toUpperCase()).append("%'");
		}
		
		
		
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		pessoas = entityManager.createQuery(sql.toString()).getResultList();
		
		transaction.commit(); 
		
		return pessoas;
	}

	
	
}
