package br.com.geradorapostas.base.bd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class InstrucaoSQL {
	public static String getInstrucao(String idInstrucao) {
		
		Properties sqlProp = new Properties();
		
		InputStream inputStream = InstrucaoSQL.class.getResourceAsStream("sql.properties");
		
		String instrucaoSql = null;
		
		try {
			sqlProp.load(inputStream);
			instrucaoSql = sqlProp.getProperty(idInstrucao);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return instrucaoSql;
	}
}
