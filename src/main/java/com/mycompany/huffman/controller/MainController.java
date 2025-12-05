package com.mycompany.huffman.controller;

import com.mycompany.huffman.model.HuffmanRow;
import com.mycompany.huffman.util.*; // Importa seus TextReaders
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.FileChooser;

import java.io.File;

public class MainController {

    // Inputs
    @FXML private TabPane inputTabPane;
    @FXML private Tab tabText;
    @FXML private TextArea inputArea;
    @FXML private Label fileLabel;

    // Tabelas
    @FXML private TableView<HuffmanRow> freqTable;
    @FXML private TableColumn<HuffmanRow, String> colSymbolFreq;
    @FXML private TableColumn<HuffmanRow, Integer> colFreq;

    @FXML private TableView<HuffmanRow> codeTable;
    @FXML private TableColumn<HuffmanRow, String> colSymbolCode;
    @FXML private TableColumn<HuffmanRow, String> colCode;

    // Output Binário
    @FXML private TextArea binaryOutput;

    // Desenho
    @FXML private Pane treePane;

    private File selectedFile;

    @FXML
    public void initialize() {
        // Vincula as colunas com a classe HuffmanRow
        colSymbolFreq.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        colFreq.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        colSymbolCode.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
    }

    @FXML
    private void handleSelectFile() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto", "*.txt"));
        selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null) fileLabel.setText(selectedFile.getName());
    }

    @FXML
    private void handleProcess() {
        try {
            // 1. LEITURA (Usando suas classes utilitárias)
            String text = "";
            if (inputTabPane.getSelectionModel().getSelectedItem() == tabText) {
                text = inputArea.getText();
            } else if (selectedFile != null) {
                // Polimorfismo aqui!
                TextReader reader = new FileTextReader(selectedFile);
                text = reader.readContent();
            }

            if (text == null || text.isEmpty()) {
                binaryOutput.setText("Por favor, insira um texto ou selecione um arquivo.");
                return;
            }

            // ======================================================
            // 2. SIMULAÇÃO DE DADOS (Aqui você plugará seu backend Huffman depois)
            // ======================================================

            // Simula resultado binário
            binaryOutput.setText("10101000... (Simulação para: " + text + ")");

            // Simula dados das tabelas
            ObservableList<HuffmanRow> data = FXCollections.observableArrayList(
                    new HuffmanRow("A", 15, "0"),
                    new HuffmanRow("B", 7, "101"),
                    new HuffmanRow("C", 6, "110"),
                    new HuffmanRow("D", 5, "111")
            );
            freqTable.setItems(data);
            codeTable.setItems(data);

            // Simula Árvore para Desenho
            // Estrutura: Raiz(33) -> Esq: A(15) | Dir: Interno(18)
            SimulatedNode nC = new SimulatedNode("C", 6, null, null);
            SimulatedNode nD = new SimulatedNode("D", 5, null, null);
            SimulatedNode nCD = new SimulatedNode(null, 11, nC, nD); // Soma C+D

            SimulatedNode nB = new SimulatedNode("B", 7, null, null);
            SimulatedNode nRight = new SimulatedNode(null, 18, nB, nCD); // Soma B + (C+D)

            SimulatedNode nA = new SimulatedNode("A", 15, null, null);
            SimulatedNode root = new SimulatedNode(null, 33, nA, nRight); // Raiz

            // 3. DESENHAR
            drawTree(root);

        } catch (Exception e) {
            e.printStackTrace();
            binaryOutput.setText("Erro: " + e.getMessage());
        }
    }

    private void drawTree(SimulatedNode root) {
        treePane.getChildren().clear();
        // Começa a desenhar no meio da tela (x=750, y=50) com espaçamento inicial de 300
        if (root != null) {
            drawTreeRecursive(root, 750, 50, 200);
        }
    }

    private void drawTreeRecursive(SimulatedNode node, double x, double y, double hGap) {
        // Se tiver filho esquerdo
        if (node.left != null) {
            double childX = x - hGap;
            double childY = y + 100;

            Line line = new Line(x, y, childX, childY);
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(2);
            treePane.getChildren().add(line);

            drawTreeRecursive(node.left, childX, childY, hGap * 0.6);
        }

        // Se tiver filho direito
        if (node.right != null) {
            double childX = x + hGap;
            double childY = y + 100;

            Line line = new Line(x, y, childX, childY);
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(2);
            treePane.getChildren().add(line);

            drawTreeRecursive(node.right, childX, childY, hGap * 0.6);
        }

        // Desenha a "Bola" do nó
        Circle circle = new Circle(x, y, 25);
        // Azul escuro para nós internos, Vermelho para folhas
        circle.setFill(node.character == null ? Color.web("#34495e") : Color.web("#e74c3c"));
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);

        // Texto dentro da bola (Caractere e Frequência)
        String label = (node.character == null ? "" : node.character + "\n") + node.frequency;
        Text text = new Text(label);
        text.setBoundsType(TextBoundsType.VISUAL);
        text.setX(x - 5);
        text.setY(y + 5);
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        if (node.character != null) text.setY(y); // Ajuste fino se tiver texto

        // Importante: Adiciona o círculo e o texto POR ÚLTIMO para ficarem em cima das linhas
        treePane.getChildren().addAll(circle, text);
    }

    // Classe interna estática para simular os nós (Use sua HuffmanNode aqui no futuro)
    public static class SimulatedNode {
        String character;
        int frequency;
        SimulatedNode left;
        SimulatedNode right;

        public SimulatedNode(String c, int f, SimulatedNode l, SimulatedNode r) {
            this.character = c;
            this.frequency = f;
            this.left = l;
            this.right = r;
        }
    }
}