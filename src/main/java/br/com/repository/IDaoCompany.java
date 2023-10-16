package br.com.repository;

import java.io.Serializable;

public interface IDaoCompany extends Serializable {
	
	boolean noRepeatCompany(String name);
	
	

}
