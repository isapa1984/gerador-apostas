package br.com.geradorapostas.util.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GerenciadorConexaoBD {
	
	private static Connection conexao;
	
	private GerenciadorConexaoBD() {}
	
	public static Connection getConexao() throws ClassNotFoundException, SQLException {
		
		if (conexao == null) {

			// Carrega o driver JDBC 
			Class.forName("org.sqlite.JDBC");
			
			// Cria a conexao
			conexao = DriverManager.getConnection("jdbc:sqlite:bd/gerados_apostas.db");
		}
		
		return conexao;
	}
	
	public static void fecharConexao() {
		try {
			if ((conexao != null) && (!conexao.isClosed()))  {
				conexao.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
}
