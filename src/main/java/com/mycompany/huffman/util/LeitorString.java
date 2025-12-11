package com.mycompany.huffman.util;

public class LeitorString extends Leitor {
    private final String conteudo;

    public LeitorString(String conteudo) {
        this.conteudo = conteudo;
    }

    @Override
    public String lerConteudo() {
        return conteudo;
    }
}