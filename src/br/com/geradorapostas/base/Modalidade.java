/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.base;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import br.com.geradorapostas.util.bd.GerenciadorConexaoBD;
import br.com.geradorapostas.util.bd.InstrucaoSQL;

/**
 *
 * @author usuario
 */
@SuppressWarnings("serial")
public class Modalidade implements Serializable {
    private final String sigla;
    private final String descricao;
    private final Integer qtdeMinMarcacao;
    private final Integer qtdeMaxMarcacao;
    private final Integer minNumero;
    private final Integer maxNumero;
    private final List<Integer> acertosPremiacao;

    public Modalidade(String sigla, String descricao, Integer qtdeMinMarcacao, Integer qtdeMaxMarcacao, Integer minNumero, Integer maxNumero, List<Integer> acertosPremiacao) {
        this.sigla              = sigla;
        this.descricao          = descricao;
        this.qtdeMinMarcacao    = qtdeMinMarcacao;
        this.qtdeMaxMarcacao    = qtdeMaxMarcacao;
        this.minNumero          = minNumero;
        this.maxNumero          = maxNumero;
        this.acertosPremiacao   = acertosPremiacao;
    }
    
    public static List<Modalidade> obterModalidades() {
        
    	List<Modalidade> modalidades = new ArrayList<>();
    	
		try {
			Connection conexaoBD = GerenciadorConexaoBD.getConexao();
			PreparedStatement stModTodas = conexaoBD.prepareStatement(InstrucaoSQL.getInstrucao("modalidades.todas"));
			
			ResultSet rs = stModTodas.executeQuery();
			
			while (rs.next()) {
				
				List<String> acertosStr = Arrays.asList(rs.getString("acertos_premiacao").split("\\s*,\\s*"));
				
				List<Integer> acertosPremiacao = acertosStr.stream().map(Integer::parseInt).collect(Collectors.toList());
				
				Modalidade modalidade = new Modalidade(
						rs.getString("sigla"), 
						rs.getString("descricao"), 
						rs.getInt("qtde_min_marcacao"),
						rs.getInt("qtde_max_marcacao"),
						rs.getInt("min_numero"),
						rs.getInt("max_numero"),
						acertosPremiacao
				);
				
				modalidades.add(modalidade);
			}
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
        
        return modalidades;
    }
    
    public static Modalidade obterModalidadePorSigla(String sigla) {

    	Modalidade modalidade = null;
        
		try {
			Connection conexaoBD = GerenciadorConexaoBD.getConexao();
			PreparedStatement stModPorSigla = conexaoBD.prepareStatement(InstrucaoSQL.getInstrucao("modalidades.por_sigla"));
			
			stModPorSigla.setString(1, sigla);
			ResultSet rs = stModPorSigla.executeQuery();
			
			while (rs.next()) {
				
				List<String> acertosStr = Arrays.asList(rs.getString("acertos_premiacao").split("\\s*,\\s*"));
				
				List<Integer> acertosPremiacao = acertosStr.stream().map(Integer::parseInt).collect(Collectors.toList());
				
				modalidade = new Modalidade(
						rs.getString("sigla"), 
						rs.getString("descricao"), 
						rs.getInt("qtde_min_marcacao"),
						rs.getInt("qtde_max_marcacao"),
						rs.getInt("min_numero"),
						rs.getInt("max_numero"),
						acertosPremiacao
				);
			}
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
        
        return modalidade;
    }

    public String getSigla() {
        return sigla;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getQtdeMinMarcacao() {
        return qtdeMinMarcacao;
    }

    public Integer getQtdeMaxMarcacao() {
        return qtdeMaxMarcacao;
    }

    public Integer getMinNumero() {
        return minNumero;
    }

    public Integer getMaxNumero() {
        return maxNumero;
    }

    public List<Integer> getAcertosPremiacao() {
        return acertosPremiacao;
    }
    
    @Override
    public String toString() {
    	return this.descricao;
    }
}
