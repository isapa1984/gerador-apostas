package br.com.geradorapostas.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import br.com.geradorapostas.util.bd.GerenciadorConexaoBD;
import br.com.geradorapostas.util.bd.InstrucaoSQL;

public class AtualizadorBD {
	
	public static void atualizarBancoDeDados(Modalidade modalidade, File arquivoSorteios) throws IOException, ParseException, ClassNotFoundException, SQLException {
		
		// Obtém os sorteios realizados do arquivo fornecido da caixa
		
		List<SorteioRealizado> sorteiosRealizadosArquivo = obterSorteiosRealizadosDoArquivo(modalidade, arquivoSorteios);
		
		PreparedStatement stNumUltimoSorteio = GerenciadorConexaoBD.getConexao().prepareStatement(InstrucaoSQL.getInstrucao("sorteios_realizados.select.num_ultimo_sorteio"));
		
		stNumUltimoSorteio.setInt(0, modalidade.getId());
		
		ResultSet rs = stNumUltimoSorteio.executeQuery();
		
		if (rs.next()) {
			
		}
		
	}
	
    private static List<SorteioRealizado> obterSorteiosRealizadosDoArquivo(Modalidade modalidade, File arquivo) 
            throws IOException, ParseException {
        
        ArrayList<SorteioRealizado> sorteios = new ArrayList<>();
        
        if (arquivo.exists()) {
            
        	Document htmlDoc = Jsoup.parse(arquivo, null);
        	
        	Elements trElements = htmlDoc.select("tr:has(td)");
        	
        	for (Element trElement : trElements) {
        		
        		Elements tdElements = trElement.children();
        		
        		if (tdElements.size() < 6) {
					continue;
				}
        		
        		Integer numConcurso 				= Integer.parseInt(tdElements.get(0).text());
        		Date dataRealizacao 				= new SimpleDateFormat("dd/MM/yyyy").parse(tdElements.get(1).text());
        		ArrayList<Integer> numerosSorteados = new ArrayList<>();
        		
        		for (int i = 1, j = 2; i <= modalidade.getQtdeMinMarcacao(); i++, j++) {
					numerosSorteados.add(Integer.parseInt(tdElements.get(j).text()));
				}
        		
        		sorteios.add(new SorteioRealizado(numConcurso, dataRealizacao, numerosSorteados));
			}

        }
        else {
            throw new FileNotFoundException(String.format("Arquivo \"%s\" não encontrado!", arquivo.getName()));
        }
        
        return sorteios;
    }
	
}
