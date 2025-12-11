package com.mycompany.huffman.model;

public class LinhaTabela {
    private String caracter;
    private int frequencia;
    private String codigo;

    public String getCaracter() { return caracter; }
    public int getFrequencia() { return frequencia; }
    public String getCodigo() { return codigo; }

    public LinhaTabela(String simbolo, int frequencia, String codigo) {
        this.caracter = simbolo;
        this.frequencia = frequencia;
        this.codigo = codigo;
    }
}