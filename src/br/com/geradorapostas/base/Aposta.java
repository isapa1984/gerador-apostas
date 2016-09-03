/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.base;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author usuario
 */
public class Aposta {

    private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    private static final String SECURE_RANDOM_PROVIDER 	= "SUN";

    private final List<Integer> numeros;

    public Aposta(List<Integer> numeros) {
        Collections.sort(numeros);
        this.numeros = numeros;
    }

    public static Aposta obterAposta(Integer valorMin, Integer valorMax, Integer qtdeNumeros) throws IllegalArgumentException {

        Aposta aposta = null;
        List<Integer> listaNumeros = new ArrayList<>();

        try {

            if (qtdeNumeros == 0) {
                throw new IllegalArgumentException("Quantidade números não pode ser 0");
            }

            if (valorMax < valorMin) {
                throw new IllegalArgumentException("'max' não pode ser menor que 'min' ");
            }

            SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM, SECURE_RANDOM_PROVIDER);

            while (listaNumeros.size() < qtdeNumeros) {
                Integer numero = secureRandom.nextInt((valorMax - valorMin) + 1) + valorMin;

                if (!listaNumeros.contains(numero)) {
                    listaNumeros.add(numero);
                }
            }

            aposta = new Aposta(listaNumeros);
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            System.err.println(ex);
        }

        return aposta;
    }

    public List<Integer> getNumeros() {
        return numeros;
    }
    
    public String getNumerosStr() {
        StringBuilder strApostas = new StringBuilder();

        for (int i = 0; i < numeros.size(); i++) {

            Integer numero = numeros.get(i);

            if (i < (numeros.size() - 1)) {
                strApostas.append(String.format("%02d ", numero));
            } else {
                strApostas.append(String.format("%02d", numero));
            }
        }

        return strApostas.toString();
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numeros == null) ? 0 : numeros.hashCode());
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
		Aposta other = (Aposta) obj;
		if (numeros == null) {
			if (other.numeros != null)
				return false;
		} else if (!numeros.equals(other.numeros))
			return false;
		return true;
	}
    
}
