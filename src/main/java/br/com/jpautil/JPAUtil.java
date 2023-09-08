package br.com.jpautil;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@ApplicationScoped
public class JPAUtil {
	
	// instancia statica e unica do EntityManager
	private EntityManagerFactory factory;
	
	
	
	public JPAUtil() {
		if (factory == null) {
		factory = Persistence.createEntityManagerFactory("JSF-intro");
		}
	}
	
	
	
	
	@Produces
	@RequestScoped
	public EntityManager getEntityManager() {
		return factory.createEntityManager(); 
	}
	
	
	// metodo pra resgatar o id do registro
	public Object getPrimaryKey (Object entity) {
		return factory.getPersistenceUnitUtil().getIdentifier(entity);
	}
	
}
