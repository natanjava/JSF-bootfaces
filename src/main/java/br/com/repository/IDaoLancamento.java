package br.com.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.com.entidades.Lancamento;

public interface IDaoLancamento extends Serializable {
	
	List<Lancamento> findLaunchByUser(Long codUser);
	
	List<Lancamento> findLaunches();
	
	List<Lancamento> consultarLimit5(Long codUser);
	
	List<Lancamento> relatorioLancamento (String numNome, Date dataIni, Date dataFim);
	
	List<Lancamento> underAprovalLaunchs (String status);

}
