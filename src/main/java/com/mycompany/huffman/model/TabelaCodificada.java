package com.mycompany.huffman.model;

import java.util.Map;

public class TabelaCodificada {
    private No raiz;
    private Map<String, String> tabela;

    public No getRaiz() {
        return raiz;
    }

    public void setRaiz(No raiz) {
        this.raiz = raiz;
    }

    public Map<String, String> getTabela() {
        return tabela;
    }

    public void setTabela(Map<String, String> tabela) {
        this.tabela = tabela;
    }

    public TabelaCodificada(No raiz) {
        this.raiz = raiz;
        this.createTable();
    }

    private Map<String, String> createTable(){
        System.out.println(raiz);
        return null;
    }
}
