package br.edu.projeto.controller;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.regex.*;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import br.edu.projeto.dao.ClienteDAO;
import br.edu.projeto.model.Cliente;

//Escopo do objeto da classe (Bean)
//ApplicationScoped é usado quando o objeto é único na aplicação (compartilhado entre usuários), permanece ativo enquanto a aplicação estiver ativa
//SessionScoped é usado quando o objeto é único por usuário, permanece ativo enquanto a sessão for ativa
//ViewScoped é usado quando o objeto permanece ativo enquanto não houver um redirect (acesso a nova página)
//RequestScoped é usado quando o objeto só existe naquela requisição específica
//Quanto maior o escopo, maior o uso de memória no lado do servidor (objeto permanece ativo por mais tempo)
//Escopos maiores que Request exigem que classes sejam serializáveis assim como todos os seus atributos (recurso de segurança)
@ViewScoped
//Torna classe disponível na camada de visão (html) - são chamados de Beans ou ManagedBeans (gerenciados pelo JSF/EJB)
@Named
public class CrudClienteController implements Serializable {
	private static final long serialVersionUID = 1L;

	//Anotação que marca atributo para ser gerenciado pelo CDI
	//O CDI criará uma instância do objeto automaticamente quando necessário
	@Inject
	private FacesContext facesContext;
	
	@Inject
    private ClienteDAO clienteDAO;
	
	private Cliente cliente;
	
	private List<Cliente> listaCliente;
	
	private Boolean rendNovoCadastro;
	
	//Anotação que força execução do método após o construtor da classe ser executado
    @PostConstruct
    public void init() {
    	//Inicializa elementos importantes
    	this.setListaCliente(clienteDAO.listAll());
    }
	
    //Chamado pelo botão novo
	public void novoCadastro() {
        this.setCliente(new Cliente());
        this.setRendNovoCadastro(true);
    }
	
	//Chamado pelo botão alterar
	public void alterarCadastro() {
        this.setRendNovoCadastro(false);
    }
	
	//Chamado pelo botão remover da tabela
	public void remover() {
		if (this.clienteDAO.delete(this.cliente)) {
			this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Cliente Removido", null));
			this.listaCliente.remove(this.cliente);
		} else 
			this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falha ao Remover Cliente", null));
		//Após excluir usuário é necessário recarregar lista que popula tabela com os novos dados
		this.listaCliente = clienteDAO.listAll();
        //Limpa seleção de usuário
		this.cliente = null;
        PrimeFaces.current().ajax().update("form:messages", "form:dt-clientes");
	}	
	
	//Chamado ao salvar cadastro de usuário (novo)
	public void salvarNovo() {
		boolean nomeValido = validarNome(this.cliente.getNome_cliente());
		boolean cpfValido = validarCPF(this.cliente.getCpf_cliente());
		boolean alturaValida = validarAltura(this.cliente.getAltura_cliente());
		boolean massaValida = validarMassa(this.cliente.getMassa_cliente());
		boolean idadeValida = validarIdade(this.cliente.getIdade_cliente());
		//boolean celularValido = validarCelular(this.cliente.getCelular_cliente());
		
		if(nomeValido && cpfValido && alturaValida && massaValida && idadeValida)
		{	
			if (this.clienteDAO.insert(this.cliente)) {
				this.getListaCliente().add(this.cliente);
				PrimeFaces.current().executeScript("PF('clienteDialog').hide()");
				PrimeFaces.current().ajax().update("form:dt-cliente");
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Cliente Criado", null));
			} else
        		this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falha ao Criar Cliente", null));  
		PrimeFaces.current().ajax().update("form:messages");
		}
	}
	//Chamado ao salvar cadastro de usuário (alteracao)
	public void salvarAlteracao() {
		boolean alturaValida = validarAltura(this.cliente.getAltura_cliente());
		boolean massaValida = validarMassa(this.cliente.getMassa_cliente());
		boolean idadeValida = validarIdade(this.cliente.getIdade_cliente());
		//boolean celularValido = validarCelular(this.cliente.getCelular_cliente());
		
		if(alturaValida && massaValida && idadeValida)
		{	
			if (this.clienteDAO.update(this.cliente)) {
				PrimeFaces.current().executeScript("PF('clienteDialog').hide()");
				PrimeFaces.current().ajax().update("form:dt-cliente");
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Cliente Atualizado", null));
			} else
        		this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falha ao Atualizar Cliente", null));
		this.setListaCliente(clienteDAO.listAll());
		PrimeFaces.current().ajax().update("form:messages");
				}
		}
	
	//GETs e SETs
	//GETs são necessários para elementos visíveis em tela
	//SETs são necessários para elementos que serão editados em tela
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<Cliente> getListaCliente() {
		return listaCliente;
	}

	public void setListaCliente(List<Cliente> listaCliente) {
		this.listaCliente = listaCliente;
	}

	public Boolean getRendNovoCadastro() {
		return rendNovoCadastro;
	}

	public void setRendNovoCadastro(Boolean rendNovoCadastro) {
		this.rendNovoCadastro = rendNovoCadastro;
	}
	
	public static boolean validarNome(String nome) {
        Pattern padrao = Pattern.compile("^[a-zA-Z]+$");
        Matcher matcher = padrao.matcher(nome);
        return matcher.matches();
    }
	public static boolean validarCPF(String cpf) {
        Pattern padrao = Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");
        Matcher matcher = padrao.matcher(cpf);
        return matcher.matches();
    }

	public static boolean validarAltura(double altura) {
		DecimalFormat df = new DecimalFormat("#.##");
        String alturaFormatada = df.format(altura);
        Pattern padrao = Pattern.compile("^\\d+(\\.\\d{1,2})?$");
        Matcher matcher = padrao.matcher(alturaFormatada);
        return matcher.matches();
    }

    public static boolean validarMassa(double massa) {
        return massa > 0;
    }

    public static boolean validarIdade(int idade) {
        return idade > 0;
    }

//    public static boolean validarCelular(String celular) {
//        return celular.matches("^\\(\\d{2}\\)\\d{5}-\\d{4}$");
//    }

}
