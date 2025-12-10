package com.mycompany.huffman.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

public class TabelaBinariaTeste {

    @Test
    public void main() {
        String texto = "Pindamonhangaba";
        TabelaFrequencia tabelaFrequencia = new TabelaFrequencia(texto);
        Arvore arvore = new Arvore(tabelaFrequencia);
        TabelaBinaria tabelaCodificada = new TabelaBinaria(arvore);

        System.out.println("\n");
        System.out.format("Texto original %s\n\n", texto);

        for (Map.Entry<String, String> linha : tabelaCodificada.getTabela().entrySet()) {
            System.out.format("%s: %s\n", linha.getKey(), linha.getValue());
        }

        System.out.println("\n");
    }
}
