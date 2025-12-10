package com.mycompany.huffman.model;

import org.junit.jupiter.api.Test;

public class VisualizacaoArvore {

    public void printArvore(No node) {
        printArvore(node, "", true);
    }

    public void printArvore(No no, String prefix, boolean isLeft) {
        if (no == null) return;

        System.out.println(prefix + (isLeft ? "└── " : "├── ")
                + (no.isFolha() ? "'" + no.getCaracter() + "'" : "")
                + " (" + no.getFrequencia() + ")");

        // prefixo dos filhos
        String childPrefix = prefix + (isLeft ? "    " : "│   ");

        printArvore(no.getFilhoEsquerdo(), childPrefix, false);
        printArvore(no.getFilhoDireito(), childPrefix, true);
    }

    @Test
    public void visualizarArvore() {
        TabelaFrequencia tabela = new TabelaFrequencia("Pindamonhangaba");
        Arvore arvore = new Arvore(tabela);

        System.out.println("\nTexto original " + tabela.getTexto());
        System.out.println("\nÁrvore de Huffman:");

        printArvore(arvore.getRaiz());

        System.out.println("\n");
    }
}
