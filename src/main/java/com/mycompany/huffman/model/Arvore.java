package com.mycompany.huffman.model;

import java.util.Map;
import java.util.PriorityQueue;

public class Arvore {
    private No raiz;

    public No getRaiz() {
        return raiz;
    }

    public Arvore(TabelaFrequencia tabela) {
        construirArvore(tabela);
    }

    private void construirArvore(TabelaFrequencia tabela) {
        // Cria uma fila dos nós que serão adicionados na árvore (ainda sem filhos)
        PriorityQueue<No> fila = new PriorityQueue<>();

        // Adicionando cada caracter na fila (ainda sem filhos)
        for (Map.Entry<Character, Integer> linha : tabela.getTabela().entrySet()) {
             fila.add(new No(linha.getKey().toString(), (int) linha.getValue()));
        }

        // Pega os dois nós de menor frequencia (retirando-os da fila),
        // Cria um nó com esses dois nós menores
        // Realiza essa recursividade até que a arvore esteja completamente realizada
        // Restando apenas o nó raiz, contendo TODOS os outros nós (já na frequencia correta)
        while (fila.size() > 1) {
            No filhoEsquerdo = fila.poll();   // menor frequência da lista
            No filhoDireito = fila.poll();  // segunda menor frequencia (nesse nó, sendo a maior frequencia)

            No pai = new No(filhoEsquerdo=filhoEsquerdo, filhoDireito=filhoDireito);
            fila.add(pai);
        }

        this.raiz = fila.poll();

    }
}
