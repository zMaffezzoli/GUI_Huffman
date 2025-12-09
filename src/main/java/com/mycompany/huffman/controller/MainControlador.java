package com.mycompany.huffman.controller;

import com.mycompany.huffman.model.Compressor;
import com.mycompany.huffman.model.LinhaTabela;

import com.mycompany.huffman.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MainControlador {
    @FXML private TabPane inputTabPane;
    @FXML private Tab tabText;
    @FXML private TextArea inputArea;
    @FXML private Label fileLabel;

    @FXML private TableView<LinhaTabela> freqTable;
    @FXML private TableColumn<LinhaTabela, String> colSymbolFreq;
    @FXML private TableColumn<LinhaTabela, Integer> colFreq;

    @FXML private TableView<LinhaTabela> codeTable;
    @FXML private TableColumn<LinhaTabela, String> colSymbolCode;
    @FXML private TableColumn<LinhaTabela, String> colCode;

    @FXML private TextArea binaryOutput;

    @FXML private StackPane treeViewport;
    @FXML private Pane treePane;

    private File selectedFile;
    private VisualizadorArvore treeVisualizer;

    @FXML
    public void initialize() {
        setupTables();
        // Inicializa o visualizador passando os componentes da GUI necessários
        this.treeVisualizer = new VisualizadorArvore(treePane, treeViewport);
    }

    private void setupTables() {
        // Tabela frequencia
        colSymbolFreq.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        colFreq.setCellValueFactory(new PropertyValueFactory<>("frequency"));

        // Tabela codigo gerado
        colSymbolCode.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));

        // Usa a classe para estilizar e organizar as células da linhas das tabelas
        FormatacaoTabela.configurarColunasSimbolo(colSymbolFreq, colSymbolCode);

        freqTable.setSelectionModel(null);
        codeTable.setSelectionModel(null);
    }

    @FXML
    private void handleClearText() {
        inputArea.clear();
        inputArea.requestFocus();
    }

    @FXML
    private void handlePasteExample() {
        inputArea.setText("BANANA");
    }

    @FXML
    private void handleSelectFile() {
        // Escreve o nome do arquivo do arquivo selecionado
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto", "*.txt"));
        selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null) fileLabel.setText(selectedFile.getName());
    }

    @FXML
    private void handleProcess() {
        // Classe principal para executar tudo no front (receb o onAction)

        // Trata possiveis erros de leitura do arquivo
        try {
            String text = obterTextoEntrada();

            if (text == null || text.isEmpty()) {
                binaryOutput.setText("Por favor, insira um texto ou selecione um arquivo que não esteja vazio.");
                return;
            }

            // Caso o arquivo não seja vazio, instancia as classes do algoritmo
            Compressor compressor = new Compressor(text);
            binaryOutput.setText(compressor.getBinario());

            atualizarTabelas(compressor);

            // Passamos a arvore gerada para a classe especializada para desenha-la
            if (compressor.getArvore() != null) {
                treeVisualizer.drawTree(compressor.getArvore().getRaiz());
            }

        } catch (Exception e) {
            e.printStackTrace();
            binaryOutput.setText("Erro: " + e.getMessage());
        }
    }

    // Retorna o texto recebido (seja ele do textarea ou do arquivo)
    private String obterTextoEntrada() throws IOException {
        String text = "";
        if (inputTabPane.getSelectionModel().getSelectedItem() == tabText) {
            // Para solidificar o código
            Leitor reader = new LeitorString(inputArea.getText());

            // Conteudo do textarea
            text = reader.readContent();

        } else if (selectedFile != null) {
            Leitor reader = new LeitorArquivo(selectedFile);

            // Conteudo do arquivo
            text = reader.readContent();
            if (text != null) text = text.replaceAll("[\\r\\n]+$", "");
        }
        return text;
    }

    private void atualizarTabelas(Compressor compressor) {
        ObservableList<LinhaTabela> tableData = FXCollections.observableArrayList();
        Map<Character, Integer> freqMap = compressor.getTabelaFrequencia().getTabela();
        Map<String, String> binaryMap = compressor.getTabelaBinaria().getTabela();

        // entry.getKey é comumm das duas tabelas (o caracter em si)
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            // Tratamento dos caracteres especiais na visualização das tabelas
            String symbol = String.valueOf(entry.getKey())
                    .replace("\n", "\\n").replace("\r", "\\r")
                    .replace("\t", "\\t").replace(" ", "space");

            tableData.add(new LinhaTabela(symbol, entry.getValue(), binaryMap.get(String.valueOf(entry.getKey()))));
        }

        // Ordena as tabelas pela frequencia dos caracteres
        tableData.sort((r1, r2) -> {
            int freqCompare = Integer.compare(r2.getFrequency(), r1.getFrequency());
            return (freqCompare != 0) ? freqCompare : r1.getSymbol().compareTo(r2.getSymbol());
        });

        freqTable.setItems(tableData);
        codeTable.setItems(tableData);
    }
}