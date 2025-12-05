package com.mycompany.huffman.model;

import org.junit.jupiter.api.Test;


public class TabelaCodificadaTeste {

    @Test
    public void main() {
        String texto = "Pindamonhangaba";
        TabelaFrequencia tabelaFrequencia = new TabelaFrequencia(texto);
        Arvore arvore = new Arvore(tabelaFrequencia);
        TabelaCodificada tabelaCodificada = new TabelaCodificada(arvore);
        System.out.println(tabelaCodificada.getTabela());
    }
}
