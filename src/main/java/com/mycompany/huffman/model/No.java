package com.mycompany.huffman.model;

public class No implements Comparable<No>{
    private String caracter;
    private Integer frequencia;
    private No filhoEsquerdo;
    private No filhoDireito;

    public String getCaracter() {
        return caracter;
    }

    public void setCaracter(String caracter) {
        this.caracter = caracter;
    }

    public No getFilhoDireito() {
        return filhoDireito;
    }

    public void setFilhoDireito(No filhoDireito) {
        this.filhoDireito = filhoDireito;
    }

    public No getFilhoEsquerdo() {
        return filhoEsquerdo;
    }

    public void setFilhoEsquerdo(No filhoEsquerdo) {
        this.filhoEsquerdo = filhoEsquerdo;
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

    public No(No filhoEsquerdo, No filhoDireito) {
        this.filhoEsquerdo = filhoEsquerdo;
        this.filhoDireito = filhoDireito;
        this.frequencia = filhoDireito.frequencia +  filhoEsquerdo.frequencia;
        this.caracter = filhoEsquerdo.caracter + "+" + filhoDireito.caracter;
    }

    // Checa se é uma folha
    public boolean isFolha() {
        return this.filhoDireito == null && this.filhoEsquerdo == null;
    }

    @Override
    public int compareTo(No other) {
        return Integer.compare(this.frequencia, other.frequencia);
    }
}
