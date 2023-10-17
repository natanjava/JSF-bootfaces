package br.com.repository;

import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import br.com.entidades.Pessoa;

public interface IDaoPessoa {
	
	List<Pessoa> consultarUsuario(String login, String senha); 
	
	List<SelectItem> listaEstados();

	List<Pessoa> relatorioPessoa(String nome, Date dataIni, Date dataFim);
	
	boolean verifyLogin(String login);
	
	boolean verifyLoginWithId(String login, Long id);
	
	void deleteUserById(Long id);

}
