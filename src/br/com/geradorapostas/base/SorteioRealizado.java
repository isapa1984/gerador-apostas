/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.base;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author usuario
 */

@SuppressWarnings("serial")
public class SorteioRealizado implements Serializable {
    
    private final Integer       numConcurso;
    private final Date        	data;
    private final List<Integer> numerosSorteados;

    public SorteioRealizado(Integer numConcurso, Date data, List<Integer> numerosSorteados) {
        this.numConcurso 		= numConcurso;
        this.data 				= data;
        this.numerosSorteados 	= numerosSorteados;
    }

    // Obtém os sorteios realizados para modalidade oriundos dos arquivos disponibilizados pela Caixa
    
    public static List<SorteioRealizado> obterSorteiosRealizados(Modalidade modalidade) {
        
        ArrayList<SorteioRealizado> sorteios = new ArrayList<>();
        
        return sorteios;
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
