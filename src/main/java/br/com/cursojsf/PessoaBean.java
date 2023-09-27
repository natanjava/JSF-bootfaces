package br.com.cursojsf;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.DatatypeConverter;

import br.com.dao.DaoGeneric;
import br.com.entidades.Cidades;
import br.com.entidades.Estados;
import br.com.entidades.Pessoa;
import br.com.jpautil.JPAUtil;
import br.com.repository.IDaoPessoa;

@javax.faces.view.ViewScoped
@Named (value="pessoaBean")
public class PessoaBean implements Serializable {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Pessoa pessoa = new Pessoa();
	private Pessoa loggedUser = new Pessoa();
	private List<Pessoa> pessoas = new ArrayList<Pessoa> ();
	
	@Inject
	private DaoGeneric<Pessoa> daoGeneric;

	@Inject
	private IDaoPessoa iDaoPessoa;
	
	private List<SelectItem> estados; 
	
	private List<SelectItem> cidades;
	
	private Part arquivoFoto;
	
	@Inject
	private JPAUtil jpaUtil;
	

	

	
	
	public Pessoa getLoggedUser() {
		return loggedUser;
	}
	
	public void setLoggedUser(Pessoa loggedUser) {
		this.loggedUser = loggedUser;
	}
	
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public DaoGeneric<Pessoa> getDaoGeneric() {
		return daoGeneric;
	}

	public void setDaoGeneric(DaoGeneric<Pessoa> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}
	
	public List<Pessoa> getPessoas() {
		return pessoas;
	}
	
	public void setPessoas(List<Pessoa> pessoas) {
		this.pessoas = pessoas;
	}
	
	public List<SelectItem> getEstados() {
		estados = iDaoPessoa.listaEstados();
		return estados;
	}
	
	public List<SelectItem> getCidades() {
		return cidades;
	}
	
	public void setCidades(List<SelectItem> cidades) {
		this.cidades = cidades;
	}	
	
	public void setEstados(List<SelectItem> estados) {
		this.estados = estados;
	}
	
	public Part getarquivoFoto() {
		return arquivoFoto;
	}

	public void setarquivoFoto(Part arquivoFoto) {
		this.arquivoFoto = arquivoFoto;
	}
	

	public String salvar () throws IOException {		
		
		
		// avoid duplicated login for new user
		if (pessoa.getId() == null && !iDaoPessoa.verifyLogin(pessoa.getLogin())) {
			mostrarMsg("Login already exists, please choose another login");
			return "";
		}
		
		// avoid duplicated login for saved user when it has been updated
		if (pessoa.getId() != null && !iDaoPessoa.verifyLoginWithId(pessoa.getLogin(), pessoa.getId())) {
			mostrarMsg("Login already exists, please choose another login");
			return "";
		}
		
		// these IF condition avoid the ADMIN to be deleted
		if (pessoa.getId() == null || pessoa.getId() != 1) {
			/*Processar the image*/
			byte[] imagemByte = null;
			if (arquivoFoto != null) {
				imagemByte = getByte(arquivoFoto.getInputStream());
			}
			
			if (imagemByte != null && imagemByte.length > 0) { 
				pessoa.setFotoIconBase64Original(imagemByte); /*atribuicao ao objeto, salva a imagem original*/
				
				/*transformar em buffer image*/
				BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagemByte));
				
				/*descobrir  o tipo da imagem*/
				int type = bufferedImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : bufferedImage.getType();
				
				/*atribui largura e altura para miniatura*/
				int largura = 200;
				int altura = 200;
				
				/*criar a miniatura*/
				BufferedImage resizedImage = new BufferedImage(altura, largura, type);
				Graphics2D g = resizedImage.createGraphics(); // retorna a parte grafica
				g.drawImage(bufferedImage, 0, 0, largura, altura, null);
				g.dispose();   // finalmente grava a imagem
				
				/*escrever novamente a imagem no tamanho menor*/
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				String extensao = arquivoFoto.getContentType().split("\\/")[1]; // retorna como padrão a string "imagem/png"; posição[0]: image; posicao[1]: extensao
				// com a extensao agora escreve a miniatura no buffer de saida baos
				ImageIO.write(resizedImage, extensao, baos);
				
				
				/*processa a imagem miniatura
				 * para pdf seria aplication/pdf ao inves de data/image*/
				String miniImagem = "data:"+arquivoFoto.getContentType() + ";base64,"+DatatypeConverter.printBase64Binary(baos.toByteArray());
				
				/*atribuicoes da mini imagem e da extensao*/
				pessoa.setFotoIconBase64(miniImagem);
				pessoa.setExtensao(extensao);		
			}
			
			
			//System.out.println(arquivoFoto);  // used to debug
			pessoa = daoGeneric.merge(pessoa);
			
			pessoa = new Pessoa();
			carregarPessoas(); // there s changes on Data Base, then load the list again
			mostrarMsg("User saved successfully.");
		}
		else if (pessoa.getId() != null && pessoa.getId()== 1) {
			mostrarMsg("Standard Admin can´t be updated.");
		}
		
		 
		return "";
	}
	
	
	private void mostrarMsg(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);  // parameter 'null' could be a specific componen
	}

	public String limpar () {
		pessoa = new Pessoa();
		return "";
	} 
	
	
	public String remove() {
		if (pessoa.getId() != 1) {
			daoGeneric.deletarPorId(pessoa);
			pessoa = new Pessoa(); 
			carregarPessoas(); 
			mostrarMsg("Removed successfully");
		}
		else {
			mostrarMsg("User 'Standard Admin' can´t be removed");
		}
		return "";
	} 
	
	@PostConstruct
	public void carregarPessoas () {
		pessoas = daoGeneric.getListEntityLimit5(Pessoa.class);
		
		// get the session e after the name of user, in order to use it in the UserPage (primeirapagina): "Welcome #user !"
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa usuarioLogado = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		String nameLoggedUser = (usuarioLogado != null) ? usuarioLogado.getNome() : "User not logged";
		loggedUser.setNome(nameLoggedUser);
		
	}
	
	public String logar() throws Exception {
		List<Pessoa> pessoaUser = iDaoPessoa.consultarUsuario(pessoa.getLogin(), pessoa.getSenha());
		try {
			
			if (pessoaUser != null ||  pessoaUser.size()!=0) { // user Ok
				FacesContext context = FacesContext.getCurrentInstance();  			
				ExternalContext externalContext = context.getExternalContext();
				/**/
				externalContext.getSessionMap().put("usuarioLogado", pessoaUser.get(0)); // pega o objeto ao inves de perfil ou login
				return "primeirapagina.xhtml?faces-redirect=true";
				/**/			
				/*
				HttpServletRequest req = (HttpServletRequest) externalContext.getRequest();
				HttpSession session = req.getSession();						
				session.setAttribute("usuarioLogado", pessoaUser);  // pega o objeto inteiro e guarda na sessão
				
				return "primeirapagina.xhtml";*/
				
			} else {
				FacesContext.getCurrentInstance().addMessage("msg-erro", new FacesMessage("User not found."));	
				return "index.xhtml";
			}
		} catch (NoResultException e) {
			FacesContext.getCurrentInstance().addMessage("msg-erro", new FacesMessage("User not found"));
			return "index.xhtml";
		}
		catch (IndexOutOfBoundsException e)	{
			FacesContext.getCurrentInstance().addMessage("msg-erro", new FacesMessage("User not found"));
			return "index.xhtml";
		}
		
		
	}
	
	/*Log off*/
	public String deslogar() {
		FacesContext context = FacesContext.getCurrentInstance();  // recupera qualquer informação do ambiente, em JSF			
		ExternalContext externalContext = context.getExternalContext();
		externalContext.getSessionMap().remove("usuarioLogado");
		
		HttpServletRequest httpServletRequest = (HttpServletRequest) context.getCurrentInstance().getExternalContext().getRequest();
		httpServletRequest.getSession().invalidate();
		
		return "index.jsf";
	}
	
	
	/* usado para controlar partes do site que podem ser vista por determiados perfis */
	public boolean permiteAcesso(String acesso) { 
		
		FacesContext context = FacesContext.getCurrentInstance();  			
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");	
		
		return pessoaUser.getPerfiUser().equals(acesso);
	}
	
	
	
	/*
	public void pesquisaCep(AjaxBehaviorEvent event) {
		//System.out.println("Method pesquisaCep(search ZipCOde) invoked: " + pessoa.getCep());
		
		try {
			URL url = new URL("https://viacep.com.br/ws/" +pessoa.getCep()+ "/json/");
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			
			String cep = "";
			StringBuilder jsonCep = new StringBuilder();
			while ((cep = br.readLine()) != null) {
				jsonCep.append(cep);
			}
			
			Pessoa gsonAux = new Gson().fromJson(jsonCep.toString(), Pessoa.class);
			//System.out.println(jsonCep);
			pessoa.setCep(gsonAux.getCep());
			pessoa.setLogradouro(gsonAux.getLogradouro());
			pessoa.setComplemento(gsonAux.getComplemento());
			pessoa.setBairro(gsonAux.getBairro());
			pessoa.setLocalidade(gsonAux.getLocalidade()); 
			pessoa.setUf(gsonAux.getUf());
			pessoa.setUnidade(gsonAux.getUnidade());
			pessoa.setIbge(gsonAux.getIbge());
			pessoa.setGia(pessoa.getGia());
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			mostrarMsg("Erro ao consultar o cep");
		}
		
	}
	 * */
	
	/* Everything which is called via Listener (from a JSF-tag), must to have one parameter 'AjaxBehaviorEvent'     	
	  * This method is no more been used, there was problem wit JSF-tag renderization*/	
	public void carregaCidades(AjaxBehaviorEvent event) {
		
		String codigoEstado = (String) event.getComponent().getAttributes().get("submittedValue");
		if (codigoEstado != null) {
			Estados estado = jpaUtil.getEntityManager().find(Estados.class, Long.parseLong(codigoEstado));
			if (estado != null) {
				
				pessoa.setEstados(estado);
				List<Cidades> cidades = jpaUtil.getEntityManager().
											createQuery("from Cidade where estados.id = "+codigoEstado+ "").
											getResultList();
				List<SelectItem> selectItemsCidade = new ArrayList<SelectItem>();
				
				for (Cidades cidade : cidades) {
					 selectItemsCidade.add(new SelectItem(cidade.getId(), cidade.getNome()));					 
				}
				setCidades(selectItemsCidade);
				
			}
		}		
	}
	
	/*This method convert InputStream to Array of Bytes*/
	private byte[] getByte (InputStream is) throws IOException {
		int len; //variavel de controle: lenght
		int size = 1024; // tamanho padrão usado para arquivo;
		byte[] buf = null;   // fluxo
		if (is instanceof ByteArrayInputStream) {    // talvez InputStream é uma instancia de ByteArrayInputStream
			size = is.available();
			buf = new byte[size];
			len = is.read(buf, 0, size);   // escreve InputStream o que esta no buffer, da posição 0 até seu tamanho.
		}
		else {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();     // saida de mida em forma de bytes
			buf = new byte[size];    // new byte de 1024;
			
			while( (len = is.read(buf, 0, size)) != -1 ) {   // varrer o is e escrever no arquivo de saida, ler e adicion ao mesmo tempo ate ser -1 (erro)
				bos.write(buf,0,len);    // escreve no arquivo de saida			
			}
			buf = bos.toByteArray();
		}	
		return buf;
		
	}
	
	
	public void download () throws IOException {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String fileDownloadId = params.get("fileDownloadId");
		if (fileDownloadId != null && fileDownloadId != "") {
			//System.out.println(fileDownloadId);
			Pessoa pessoa = daoGeneric.consultar(Pessoa.class, fileDownloadId);
			
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			response.addHeader("Content-Disposition", "attachment;filename=download."+pessoa.getExtensao());
			response.setContentType("application/octet-stream");
			response.setContentLength(pessoa.getFotoIconBase64Original().length);
			response.getOutputStream().write(pessoa.getFotoIconBase64Original());
			response.getOutputStream().flush();
			FacesContext.getCurrentInstance().responseComplete();
		}
	}
	
	
	
	
	


	
	
	
	
	
	
}
