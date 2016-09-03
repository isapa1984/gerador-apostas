/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.base;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

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
        InputStreamReader reader = new InputStreamReader(Modalidade.class.getResourceAsStream("modalidades.json"));
        Type type = new TypeToken<List<Modalidade>>(){}.getType();
        Gson gson = new Gson();
        List<Modalidade> modalidades = gson.fromJson(reader, type);
        return modalidades;
    }
    
    public static Modalidade obterModalidadePorSigla(String sigla) {
        List<Modalidade> modalidades = Modalidade.obterModalidades();
        
        for (Modalidade modalidade : modalidades) {
            if (modalidade.getSigla().equals(sigla)) {
                return modalidade;
            }
        }
        
        return null;
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
}
