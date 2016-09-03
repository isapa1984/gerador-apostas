/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author usuario
 */
public class ArquivoUtil {
    
    public static void escreverEmArquivoTexto(String nomeArquivo, String conteudo) throws IOException {
        File arquivoTexto = new File(nomeArquivo);
        
        if (arquivoTexto.exists()) {
            arquivoTexto.delete();
        }
        
        arquivoTexto.createNewFile();
        
        FileWriter fileWriter = new FileWriter(arquivoTexto);
        
        try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(conteudo);
        }
    }
}
