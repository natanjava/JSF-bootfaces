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
	
	@Size(min=1, max=50, message="Nome deve ter entre 1 e 50 letras")
	private String nome;
	
	//@NotEmpty (message="Sobrenome deve ser informado")
	//@NotNull (message="Sobrenome deve ser informado")
	private String Sobrenome;

	@DecimalMax(value="70", message = "Idade maxima 70 anos")
	@DecimalMin(value="10", message = "Idade minima 10 anos")
	private Integer idade;

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

	private String cep;

	private String logradouro;

	private String complemento;

	private String bairro;

	private String localidade;

	private String uf;

	private String unidade;

	private String ibge;

	private String gia;
	
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

	public Integer getIdade() {
		return idade;
	}

	public void setIdade(Integer idade) {
		this.idade = idade;
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

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	
	 public String getGia() { return gia; }
	 
	 public void setGia(String gia) { this.gia = gia; }


	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getLocalidade() {
		return localidade;
	}

	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	
	 public String getUnidade() { return unidade; }
	 
	 
	 public void setUnidade(String unidade) { this.unidade = unidade; }
	 
	 
	 public String getIbge() { return ibge; }
	 
	 
	 public void setIbge(String ibge) { this.ibge = ibge; }
	 
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
