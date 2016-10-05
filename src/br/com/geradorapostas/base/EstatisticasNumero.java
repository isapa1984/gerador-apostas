package br.com.geradorapostas.base;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.com.geradorapostas.util.bd.GerenciadorConexaoBD;
import br.com.geradorapostas.util.bd.InstrucaoSQL;

public class EstatisticasNumero {

	private final Integer numero;
	private final Integer qtde_repeticao;
	private final Integer qtde_atraso;

	public EstatisticasNumero(Integer numero, Integer qtde_repeticao, Integer qtde_atraso) {
		this.numero 		= numero;
		this.qtde_repeticao = qtde_repeticao;
		this.qtde_atraso 	= qtde_atraso;
	}

	public static EstatisticasNumero obterEstatisticas(Modalidade modalidade, Integer numero) {

		EstatisticasNumero estatisticasNumero = null;

		try {
			PreparedStatement stEstatsNumero = GerenciadorConexaoBD.getConexao().prepareStatement(InstrucaoSQL.getInstrucao("estatisticas_numeros.select.estats_numero"));
			
			stEstatsNumero.setInt(1, modalidade.getId());
			stEstatsNumero.setInt(2, numero);

			ResultSet rs = stEstatsNumero.executeQuery();
			
			if (rs.next()) {
				estatisticasNumero = new EstatisticasNumero(rs.getInt("numero"), rs.getInt("qtde_repeticao"), rs.getInt("qtde_atraso"));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return estatisticasNumero;
	}

	public Integer getNumero() {
		return numero;
	}

	public Integer getQtde_repeticao() {
		return qtde_repeticao;
	}

	public Integer getQtde_atraso() {
		return qtde_atraso;
	}

}
