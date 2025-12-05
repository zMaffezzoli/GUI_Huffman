package com.mycompany.huffman.model;

import java.util.HashMap;
import java.util.Map;

public class TabelaFrequencia {
    private String texto;
    private Map<Character, Integer> tabela = new HashMap<>();

    public Map<Character, Integer> getTabela() {
        return tabela;
    }

    public void setTabela(Map<Character, Integer> map) {
        this.tabela = map;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public TabelaFrequencia(String texto) {
        this.texto = texto;
        this.createTable();
    }

    private void createTable() {
        for  (char c : this.texto.toCharArray()) {
            tabela.put(c, tabela.getOrDefault(c, 0) + 1);
        }
    }
}
