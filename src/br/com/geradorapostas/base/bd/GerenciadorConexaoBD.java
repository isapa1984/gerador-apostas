package br.com.geradorapostas.base.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GerenciadorConexaoBD {
	
	public static Connection getConexao()  {
		
		Connection conexao = null;

		try {
			// Carrega o driver JDBC 
			Class.forName("org.sqlite.JDBC");
			
			// Cria a conexao
			conexao = DriverManager.getConnection("jdbc:sqlite:bd/gerados_apostas.db");
			conexao.setAutoCommit(false);
		} 
		catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		return conexao;
	}
	
}
