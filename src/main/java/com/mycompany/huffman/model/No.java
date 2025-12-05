package com.mycompany.huffman.model;

public class No implements Comparable<No>{
    private String caracter;
    private Integer frequencia;
    private No filho_esquerdo;
    private No filho_direito;

    public String getCaracter() {
        return caracter;
    }

    public void setCaracter(String caracter) {
        this.caracter = caracter;
    }

    public No getFilho_direito() {
        return filho_direito;
    }

    public void setFilho_direito(No filho_direito) {
        this.filho_direito = filho_direito;
    }

    public No getFilho_esquerdo() {
        return filho_esquerdo;
    }

    public void setFilho_esquerdo(No filho_esquerdo) {
        this.filho_esquerdo = filho_esquerdo;
    }

    public Integer getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(Integer frequencia) {
        this.frequencia = frequencia;
    }

    public No(String caracter, int frequencia) {
        this.caracter = caracter;
        this.frequencia = frequencia;
    }

    public No(No filho_esquerdo, No filho_direito) {
        this.filho_esquerdo = filho_esquerdo;
        this.filho_direito = filho_direito;
        this.frequencia = filho_direito.frequencia +  filho_esquerdo.frequencia;
        this.caracter = filho_esquerdo.caracter + " + " + filho_direito.caracter;
    }

    // Checa se é uma folha (Metodo somente para visualização teste)
    public boolean isFolha() {
        return this.filho_direito == null && this.filho_esquerdo == null;
    }

    @Override
    public int compareTo(No other) {
        return Integer.compare(this.frequencia, other.frequencia);
    }
}
