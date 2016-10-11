package br.com.geradorapostas.base.bd;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EstatisticasNumero {

	private final Modalidade modalidade;
	private final Integer numero;
	private Integer qtdeRepeticoes;
	private Integer qtdeAtrasos;

	public EstatisticasNumero(Modalidade modalidade, Integer numero, Integer qtdeRepeticoes, Integer qtdeAtrasos) {
		this.modalidade = modalidade;
		this.numero = numero;
		this.qtdeRepeticoes = qtdeRepeticoes;
		this.qtdeAtrasos = qtdeAtrasos;
	}

	public static EstatisticasNumero obterEstatisticas(Modalidade modalidade, Integer numero) {

		EstatisticasNumero estatisticasNumero = null;

		try (
			PreparedStatement stEstatsNumero = GerenciadorConexaoBD.getConexao().prepareStatement(InstrucaoSQL.getInstrucao("estatisticas_numeros.select.estats_numero"));
		) {
			stEstatsNumero.setInt(1, modalidade.getId());
			stEstatsNumero.setInt(2, numero);

			ResultSet rs = stEstatsNumero.executeQuery();
			
			if (rs.next()) {
				estatisticasNumero = new EstatisticasNumero(modalidade, rs.getInt("numero"), rs.getInt("qtde_repeticoes"), rs.getInt("qtde_atrasos"));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return estatisticasNumero;
	}
	
	public static void inserirEstatistica(EstatisticasNumero novaEstatistica) {
    	try (
    		PreparedStatement stInserirEstats = GerenciadorConexaoBD.getConexao().prepareStatement(InstrucaoSQL.getInstrucao("estatisticas_numeros.insert.nova_estatistica"));
        ) {
			stInserirEstats.setInt(1, novaEstatistica.getModalidade().getId());
			stInserirEstats.setInt(2, novaEstatistica.getNumero());
			stInserirEstats.setInt(3, novaEstatistica.getQtdeRepeticoes());
			stInserirEstats.setInt(4, novaEstatistica.getQtdeAtrasos());

			stInserirEstats.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void atualizarEstatistica(EstatisticasNumero novaEstatistica) {
		try (
			PreparedStatement stAtualizarEstats = GerenciadorConexaoBD.getConexao().prepareStatement(InstrucaoSQL.getInstrucao("estatisticas_numeros.update.estatistica"));
		) {
			stAtualizarEstats.setInt(1, novaEstatistica.getQtdeRepeticoes());
			stAtualizarEstats.setInt(2, novaEstatistica.getQtdeAtrasos());
			stAtualizarEstats.setInt(3, novaEstatistica.getModalidade().getId());
			stAtualizarEstats.setInt(4, novaEstatistica.getNumero());
			
			stAtualizarEstats.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Modalidade getModalidade() {
		return modalidade;
	}
	
	public Integer getNumero() {
		return numero;
	}

	public Integer getQtdeRepeticoes() {
		return qtdeRepeticoes;
	}

	public void setQtdeRepeticoes(Integer qtdeRepeticoes) {
		this.qtdeRepeticoes = qtdeRepeticoes;
	}

	public Integer getQtdeAtrasos() {
		return qtdeAtrasos;
	}

	public void setQtdeAtrasos(Integer qtdeAtrasos) {
		this.qtdeAtrasos = qtdeAtrasos;
	}

}
