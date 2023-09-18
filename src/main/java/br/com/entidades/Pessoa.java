package br.com.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
//import javax.faces.bean.ManagedBean;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.br.CPF;
import org.hibernate.validator.constraints.br.TituloEleitoral;

@Entity
public class Pessoa implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/* ----- Class attributes with notatations/validations ----- */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	//@NotEmpty (message="Name required")
	@Size(min=1, max=50, message="Nome deve ter entre 1 e 50 letras")
	private String nome;
	
	//@NotNull (message="Surname required")
	private String Sobrenome;


	@Temporal(TemporalType.DATE)
	private Date dataNascimento;

	private String sexo;

	private String[] frameworks;

	private Boolean ativo;
	
	private String login;

	private String senha;

	private String perfiUser;

	private String nivelProgramador;

	private Integer[] linguagens;

	
	
	 @ManyToOne 
	 private Cidades cidades;	 
	 
	 @Transient //só uma variavel de auxilio, nao fica persistente, hibernate não cria tabela no banco 
	 private Estados estados;
	 
	 @Column(columnDefinition = "text")  /*tipo Text grava arquivos em base 64*/
	 private String fotoIconBase64;
	 
	 private String extensao; /* extensão da foto*/
	 
	 @Basic(fetch= FetchType.LAZY) /* fetch LAZY: carrega somente quando for chamado*/
	 private byte[] fotoIconBase64Original; /*Grava arquivo no banco de dados */
	 
	 /* ----- end of class attributes -----  */
	 
	 
	/* Classe Construct */
	public Pessoa() {

	}
	/* ----------*/

	
	/* --- geters and setters method, hash and code --- */
	 public Cidades getCidades() { return cidades; }
	 
	 public void setCidades(Cidades cidades) { this.cidades = cidades; }

	 public Estados getEstados() { return estados; }
	 
	 public void setEstados(Estados estados) { this.estados = estados; }	 

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSobrenome() {
		return Sobrenome;
	}

	public void setSobrenome(String sobrenome) {
		Sobrenome = sobrenome;
	}

	

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getSexo() {
		return sexo;
	}

	public String[] getFrameworks() {
		return frameworks;
	}

	public void setFrameworks(String[] frameworks) {
		this.frameworks = frameworks;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getPerfiUser() {
		return perfiUser;
	}

	public void setPerfiUser(String perfiUser) {
		this.perfiUser = perfiUser;
	}

	public String getNivelProgramador() {
		return nivelProgramador;
	}

	public void setNivelProgramador(String nivelProgramador) {
		this.nivelProgramador = nivelProgramador;
	}

	public Integer[] getLinguagens() {
		return linguagens;
	}

	public void setLinguagens(Integer[] linguagens) {
		this.linguagens = linguagens;
	}

	
	 
	 public String getFotoIconBase64() {
		return fotoIconBase64;
	}

	public void setFotoIconBase64(String fotoIconBase64) {
		this.fotoIconBase64 = fotoIconBase64;
	}

	public String getExtensao() {
		return extensao;
	}

	public void setExtensao(String extensao) {
		this.extensao = extensao;
	}

	public byte[] getFotoIconBase64Original() {
		return fotoIconBase64Original;
	}

	public void setFotoIconBase64Original(byte[] fotoIconBase64Original) {
		this.fotoIconBase64Original = fotoIconBase64Original;
	}
	

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pessoa other = (Pessoa) obj;
		return Objects.equals(id, other.id);
	}
	/* ---------------------------*/

}
