/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.base.bd;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author usuario
 */

@SuppressWarnings("serial")
public class SorteioRealizado implements Serializable {

	private final Modalidade    modalidade;
    private final Integer       numConcurso;
    private final Date        	data;
    private final List<Integer> numerosSorteados;
    
    public SorteioRealizado(Modalidade modalidade, Integer numConcurso, Date data, List<Integer> numerosSorteados) {
		this.modalidade = modalidade;
		this.numConcurso = numConcurso;
		this.data = data;
		this.numerosSorteados = numerosSorteados;
	}
    
    // Obtém o último sorteio realizado

	public static SorteioRealizado obterUltimoSorteioRealizado(Modalidade modalidade) {
		
		SorteioRealizado sorteioRealizado = null;
		
	   	try (
    		PreparedStatement stNumUltimoSorteio = GerenciadorConexaoBD.getConexao().prepareStatement(InstrucaoSQL.getInstrucao("sorteios_realizados.select.ultimo_sorteio"));
    	) {
			
	   		ResultSet rs = stNumUltimoSorteio.executeQuery();
	   		
	   		if (rs.next()) {
	   			List<String> numerosStr = Arrays.asList(rs.getString("numeros_sorteados"));
	   			
	   			List<Integer> numerosSorteados = numerosStr.stream().map(Integer::parseInt).collect(Collectors.toList());
	   			
	   			sorteioRealizado = new SorteioRealizado(
	   					modalidade, 
	   					rs.getInt("num_concurso"), 
	   					new SimpleDateFormat("dd/MM/yyyy").parse(rs.getString("data")), 
	   					numerosSorteados
	   			);
			}
    		
		} 
    	catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		
		return sorteioRealizado;
	}
    
    // Obtém os sorteios realizados armazenado no banco de dados
    
    public static List<SorteioRealizado> obterSorteiosRealizados(Modalidade modalidade) {
        
        ArrayList<SorteioRealizado> sorteios = new ArrayList<>();
        
	   	try (
    		PreparedStatement stTodosSorteios = GerenciadorConexaoBD.getConexao().prepareStatement(InstrucaoSQL.getInstrucao("sorteios_realizados.select.todos"));
    	) {
			
	   		stTodosSorteios.setInt(1, modalidade.getId());
	   		
	   		ResultSet rs = stTodosSorteios.executeQuery();
	   		
	   		while (rs.next()) {
	   			List<String> numerosStr = Arrays.asList(rs.getString("numeros_sorteados"));
	   			
	   			List<Integer> numerosSorteados = numerosStr.stream().map(Integer::parseInt).collect(Collectors.toList());
	   			
	   			SorteioRealizado sorteioRealizado = new SorteioRealizado(
	   					modalidade, 
	   					rs.getInt("num_concurso"), 
	   					new SimpleDateFormat("dd/MM/yyyy").parse(rs.getString("data")), 
	   					numerosSorteados
	   			);
	   			
	   			sorteios.add(sorteioRealizado);
			}
    		
		} 
    	catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
        
        return sorteios;
    }
    
    public Modalidade getModalidade() {
		return modalidade;
	}
    
    public Integer getNumConcurso() {
    	return numConcurso;
    }
    
    public Date getData() {
		return data;
	}
    
    public List<Integer> getNumerosSorteados() {
    	return numerosSorteados;
    }
    
    @Override
    public String toString() {
    	return String.format(
    			"Concurso: %d, Data: %s, Números: %s", 
    			this.numConcurso, 
    			new SimpleDateFormat("dd/MM/yyyy").format(this.data), 
    			this.numerosSorteados
    	);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numConcurso == null) ? 0 : numConcurso.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SorteioRealizado other = (SorteioRealizado) obj;
		if (numConcurso == null) {
			if (other.numConcurso != null)
				return false;
		} else if (!numConcurso.equals(other.numConcurso))
			return false;
		return true;
	}
}
