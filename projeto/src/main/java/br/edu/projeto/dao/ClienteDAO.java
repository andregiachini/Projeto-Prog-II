package br.edu.projeto.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.sql.DataSource;

import br.edu.projeto.model.Cliente;
import br.edu.projeto.util.DbUtil;

//Classe DAO responsável pelas regras de negócio envolvendo operações de persistência de dados
//Indica-se a acriação de um DAO específico para cada Modelo

//Anotação EJB que indica que Bean (objeto criado para a classe) será comum para toda a aplicação
//Isso faz com que recursos computacionais sejam otimizados e garante maior segurança nas transações com o banco
@Stateful
public class ClienteDAO implements Serializable{
	private static final long serialVersionUID = 1L;

	@Inject
	private DataSource ds;
    
    public List<Cliente> listAll() {
    	List<Cliente> clientes = new ArrayList<Cliente>();
    	Connection con = null;//Conexão com a base
    	PreparedStatement ps = null;//Instrução SQL
    	ResultSet rs = null;//Resposta do SGBD
    	try {
			con = this.ds.getConnection();//Pegar um conexão
			ps = con.prepareStatement("SELECT * FROM cliente");
			rs = ps.executeQuery();
			while (rs.next()) {//Pega próxima linha do retorno
				Cliente c = new Cliente();
				c.setCpf_cliente(rs.getString("cpf"));
				c.setNome_cliente(rs.getString("nome"));
				c.setNome_social_cliente(rs.getString("nome_social"));
				c.setAltura_cliente(rs.getDouble("altura"));
				c.setMassa_cliente(rs.getDouble("massa"));
				c.setGenero_cliente(rs.getString("genero"));
				c.setIdade_cliente(rs.getInt("idade"));
				c.setEmail_cliente(rs.getString("email"));
				c.setCelular_cliente(rs.getString("celular"));
				c.setEndereco(rs.getString("endereco"));
				clientes.add(c);
			}
		} catch (SQLException e) {e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closePreparedStatement(ps);
			DbUtil.closeConnection(con);
		}
        return clientes;
    }
    
       
    public Boolean insert(Cliente c) {
    	Boolean resultado = false;
    	Connection con = null;
    	PreparedStatement ps = null;
    	try {
	    	con = this.ds.getConnection();
	    	try {				
				ps = con.prepareStatement("INSERT INTO cliente (cpf, nome, nome_social, altura, massa, genero, idade, email, celular, endereco) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				ps.setString(1, c.getCpf_cliente());
				ps.setString(2, c.getNome_cliente());
				ps.setString(3, c.getNome_social_cliente());	
				ps.setDouble(4, c.getAltura_cliente());
				ps.setDouble(5, c.getMassa_cliente());
				ps.setString(6, c.getGenero_cliente());
				ps.setInt(7, c.getIdade_cliente());
				ps.setString(8, c.getEmail_cliente());
				ps.setString(9, c.getCelular_cliente());
				ps.setString(10, c.getEndereco());
				ps.execute();
				resultado = true;
			} catch (SQLException e) {e.printStackTrace();}
    	} catch (SQLException e) {e.printStackTrace();
    	} finally {
			DbUtil.closePreparedStatement(ps);
			DbUtil.closeConnection(con);
		}
    	return resultado;
    }
    
    public Boolean update(Cliente c) {
    	Boolean resultado = false;
    	Connection con = null;
    	PreparedStatement ps = null;
    	try {
	    	con = this.ds.getConnection();
	    	try {				
				ps = con.prepareStatement("UPDATE cliente SET nome_social = ?, altura = ?, massa = ?, genero = ?, idade = ?, email = ?, celular = ?, endereco = ? WHERE cpf = ?");
				ps.setString(1, c.getNome_social_cliente());	
				ps.setDouble(2, c.getAltura_cliente());
				ps.setDouble(3, c.getMassa_cliente());
				ps.setString(4, c.getGenero_cliente());
				ps.setInt(5, c.getIdade_cliente());
				ps.setString(6, c.getEmail_cliente());
				ps.setString(7, c.getCelular_cliente());
				ps.setString(8, c.getEndereco());
				ps.setString(9, c.getCpf_cliente());
				ps.execute();	
				resultado = true;
			} catch (SQLException e) {e.printStackTrace();}
    	} catch (SQLException e) {e.printStackTrace();
    	} finally {
			DbUtil.closePreparedStatement(ps);
			DbUtil.closeConnection(con);
		}
    	return resultado;
    }
    
    public Boolean delete(Cliente c) {
    	Boolean resultado = false;
    	Connection con = null;
    	PreparedStatement ps = null;
    	try {
	    	con = this.ds.getConnection();
	    	try {				
				ps = con.prepareStatement("DELETE FROM cliente WHERE cpf = ?");
				ps.setString(1, c.getCpf_cliente());
				ps.execute();
				resultado = true;
			} catch (SQLException e) {e.printStackTrace();}
    	} catch (SQLException e) {e.printStackTrace();
    	} finally {
			DbUtil.closePreparedStatement(ps);
			DbUtil.closeConnection(con);
		}
    	return resultado;
    }
}
