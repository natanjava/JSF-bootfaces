package br.com.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.entidades.Company;
import br.com.entidades.Lancamento;

@Named
public class IDaoCompanyImpl implements IDaoCompany, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager entityManager;
	
	@Override
	public boolean noRepeatCompany(String name) {
		List<Company> companiesWithSameName = null;
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		companiesWithSameName = entityManager.createQuery("from Company c where lower(c.name) = lower(:name)")
		        .setParameter("name", name.toLowerCase())
	            .getResultList();
		
		transaction.commit();
		
		return companiesWithSameName.isEmpty();
	}

	@Override
	public int getLaunchCompany(String empresaOrigem) {
		List<Lancamento> numberLaunches = null;
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		numberLaunches = entityManager.createQuery("from Lancamento l where l.empresaOrigem = :empresaOrigem")
					.setParameter("empresaOrigem", empresaOrigem)
					.getResultList();
		transaction.commit();
		
		return numberLaunches.size();
	}


}
