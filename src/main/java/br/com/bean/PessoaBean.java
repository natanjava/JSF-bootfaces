package br.com.bean;

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
import br.com.jpautil.JPAUtil;
import br.com.model.Lancamento;
import br.com.model.Pessoa;
import br.com.repository.IDaoLancamento;
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
	
	private List<Lancamento> launchesReview = new ArrayList<Lancamento>();
	
	@Inject
	private DaoGeneric<Pessoa> daoGeneric;

	@Inject
	private IDaoPessoa iDaoPessoa;
	
	@Inject
	private IDaoLancamento daoLancamento;
	
	@Inject
	private JPAUtil jpaUtil;
	
	private List<SelectItem> estados; 
	
	private List<SelectItem> cidades;
	
	private Part arquivoFoto;
	
	
	/*  get and set --- BEGIN*/	
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
	
	public List<Lancamento> getLaunchesReview() {
		return launchesReview;
	}

	public void setLaunchesReview(List<Lancamento> launchesReview) {
		this.launchesReview = launchesReview;
	}
	/*  get and set --- END*/	
	
	
	/* method activated whenever the page is loaded*/
	@PostConstruct
	public void carregarPessoas () {
		pessoas = daoGeneric.getListEntityLimit5(Pessoa.class);
		
		// get the session e after the name of user, in order to use it in the UserPage (primeirapagina): "Welcome #user !"
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa usuarioLogado = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		String nameLoggedUser = (usuarioLogado != null) ? usuarioLogado.getNome() : "User not logged";
		String roleLoggedUser = (usuarioLogado != null) ? usuarioLogado.getPerfiUser() : "User withou role";
		loggedUser.setNome(nameLoggedUser);
		loggedUser.setPerfiUser(roleLoggedUser);
		launchesReview = daoLancamento.underAprovalLaunchs("under review"); 
		if (launchesReview.size() > 0 && (loggedUser.getPerfiUser().equalsIgnoreCase("ADMINISTRATOR") 
				                           || loggedUser.getPerfiUser().equalsIgnoreCase("MANAGER")  )) {
			mostrarMsg("There is one or more  Orders to be approved. Look at Pending Approvals Page");
		}
		
	}
	
	/*method that permit log in*/
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
				FacesContext.getCurrentInstance().addMessage("msg-erro", new FacesMessage("User/Password not found."));	
				return "index.xhtml";
			}
		} catch (NoResultException e) {
			FacesContext.getCurrentInstance().addMessage("msg-erro", new FacesMessage("User/Password not found"));
			return "index.xhtml";
		}
		catch (IndexOutOfBoundsException e)	{
			FacesContext.getCurrentInstance().addMessage("msg-erro", new FacesMessage("User/Password not found"));
			return "index.xhtml";
		}
	}
	
	/* save new user; handle upload of image*/
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
		
		// these IF condition below avoid the ADMIN to be deleted
		if (pessoa.getId() == null || pessoa.getId() != 1) {
			
			/*Process the image*/
			byte[] imagemByte = null;
			if (arquivoFoto != null) {
				imagemByte = getByte(arquivoFoto.getInputStream());
			}
			
			if (imagemByte != null && imagemByte.length > 0) { 
				pessoa.setFotoIconBase64Original(imagemByte); /*atribuicao ao objeto, salva a imagem original*/
				
				/*transfor in buffer */
				BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagemByte));
				
				/*find out the typ of image*/
				int type = bufferedImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : bufferedImage.getType();
				
				/*set up with and height to miniature*/
				int largura = 200;
				int altura = 200;
				
				/*create the miniature*/
				BufferedImage resizedImage = new BufferedImage(altura, largura, type);
				Graphics2D g = resizedImage.createGraphics(); // retorna a parte grafica
				g.drawImage(bufferedImage, 0, 0, largura, altura, null);
				g.dispose();   // finally record the image
				
				/*write the image again with a smaller size*/
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				String extensao = arquivoFoto.getContentType().split("\\/")[1]; // retorna como padrão a string "imagem/png"; posição[0]: image; posicao[1]: extensao
				// now with the extension, write the imageon the output buffer => baos
				ImageIO.write(resizedImage, extensao, baos);
				
				
				/* process the miniature
				 * for PDF: instead data/image that would be aplication/pdf */
				String miniImagem = "data:"+arquivoFoto.getContentType() + ";base64,"+DatatypeConverter.printBase64Binary(baos.toByteArray());
				
				/*mini image and extension assignments*/
				pessoa.setFotoIconBase64(miniImagem);
				pessoa.setExtensao(extensao);		
			}
			
			
			//System.out.println(arquivoFoto);  // used to debug
			pessoa = daoGeneric.merge(pessoa);
			
			pessoa = new Pessoa();
			carregarPessoas(); // there s changes on Data Base, then load the list again
			mostrarMsg("User saved successfully."); // show successful message
		}
		else if (pessoa.getId() != null && pessoa.getId()== 1) {
			mostrarMsg("Standard Admin can´t be updated.");
		}
		
		 
		return "";
	}
	
	/* generic method: show some message to the user*/
	private void mostrarMsg(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);  // parameter 'null' could be a specific component on the page
	}
	
	/* clean formular*/
	public String limpar () {
		pessoa = new Pessoa();
		return "";
	} 
	
	/* remove user*/
	public String remove() {
		if (pessoa.getId() != 1) {
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			Pessoa usuarioLogado = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
			
			Long idLoggedUser = usuarioLogado.getId();
			
			if (pessoa.getId() == idLoggedUser) {
				
				//daoGeneric.deletarPorId(pessoa);
				iDaoPessoa.deleteUserById(pessoa.getId());
				externalContext.getSessionMap().remove("usuarioLogado");
				HttpServletRequest httpServletRequest = (HttpServletRequest) context.getCurrentInstance().getExternalContext().getRequest();
				httpServletRequest.getSession().invalidate();
				mostrarMsg("You were delete. Please log in with another User.");
				return "index.jsf";
			}
			else {
				//daoGeneric.deletarPorId(pessoa);
				iDaoPessoa.deleteUserById(pessoa.getId());
				pessoa = new Pessoa(); 
				carregarPessoas(); 
				mostrarMsg("Removed successfully");
			}
			
		}
		else {
			mostrarMsg("User 'Standard Admin' can´t be removed");
		}
		return "";
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
	
	/* download image saved at the moment of registration*/
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
	
	/*Log off*/
	public String deslogar() {
		FacesContext context = FacesContext.getCurrentInstance();  // recupera qualquer informação do ambiente, em JSF			
		ExternalContext externalContext = context.getExternalContext();
		externalContext.getSessionMap().remove("usuarioLogado");
		
		HttpServletRequest httpServletRequest = (HttpServletRequest) context.getCurrentInstance().getExternalContext().getRequest();
		httpServletRequest.getSession().invalidate();
		
		return "index.jsf?faces-redirect=true";
	}
	
	/* used to control parts of the website that can be accessed by certain profiles */
	public boolean permiteAcesso(String role) { 
		
		FacesContext context = FacesContext.getCurrentInstance();  			
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");	
		
		return pessoaUser.getPerfiUser().equals(role);
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
	/*
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
	 * */
	

	
	
	
	
	


	
	
	
	
	
	
}
