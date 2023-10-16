package br.com.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.entidades.Company;

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
		
		companiesWithSameName = entityManager.createQuery("from Company c where c.name = :name")
	            .setParameter("name", name)
	            .getResultList();
		
		transaction.commit();
		
		return companiesWithSameName.isEmpty();
	}

}
