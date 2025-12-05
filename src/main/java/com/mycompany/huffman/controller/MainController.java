package com.mycompany.huffman.controller;

import com.mycompany.huffman.model.HuffmanRow;
import com.mycompany.huffman.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.FileChooser;

import java.io.File;

public class MainController {

    @FXML private TabPane inputTabPane;
    @FXML private Tab tabText;
    @FXML private TextArea inputArea;
    @FXML private Label fileLabel;

    @FXML private TableView<HuffmanRow> freqTable;
    @FXML private TableColumn<HuffmanRow, String> colSymbolFreq;
    @FXML private TableColumn<HuffmanRow, Integer> colFreq;

    @FXML private TableView<HuffmanRow> codeTable;
    @FXML private TableColumn<HuffmanRow, String> colSymbolCode;
    @FXML private TableColumn<HuffmanRow, String> colCode;

    @FXML private TextArea binaryOutput;

    // ÁREA DA ÁRVORE (MODIFICADO)
    @FXML private StackPane treeViewport; // A janela fixa
    @FXML private Pane treePane;          // O conteúdo que se move

    private File selectedFile;

    // Variáveis para controle de arrastar (Pan)
    private double mouseAnchorX;
    private double mouseAnchorY;
    private double translateAnchorX;
    private double translateAnchorY;

    @FXML
    public void initialize() {
        colSymbolFreq.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        colFreq.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        colSymbolCode.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));

        freqTable.setSelectionModel(null);
        codeTable.setSelectionModel(null);

        // --- CONFIGURAÇÃO DE ZOOM E PAN ---
        setupZoomAndPan();
    }

    private void setupZoomAndPan() {
        // 1. Clipping: Garante que a árvore não desenhe fora da caixa cinza
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(treeViewport.widthProperty());
        clip.heightProperty().bind(treeViewport.heightProperty());
        treeViewport.setClip(clip);

        // 2. Lógica de Scroll (Zoom)
        treeViewport.setOnScroll(event -> {
            // VERIFICAÇÃO DO CTRL: Só executa se o Ctrl estiver segurado
            if (event.isControlDown()) {
                double zoomFactor = 1.05;
                double deltaY = event.getDeltaY();

                if (deltaY < 0) {
                    zoomFactor = 1 / zoomFactor; // Zoom Out
                }

                // Aplica escala
                treePane.setScaleX(treePane.getScaleX() * zoomFactor);
                treePane.setScaleY(treePane.getScaleY() * zoomFactor);

                event.consume(); // Marca como resolvido para não propagar
            }
        });

        // 3. Lógica de Clique (Início do Arrastar)
        treePane.setOnMousePressed(event -> {
            mouseAnchorX = event.getSceneX();
            mouseAnchorY = event.getSceneY();
            translateAnchorX = treePane.getTranslateX();
            translateAnchorY = treePane.getTranslateY();
            treeViewport.setCursor(javafx.scene.Cursor.CLOSED_HAND); // Muda cursor
        });

        // 4. Lógica de Arrastar (Pan)
        treePane.setOnMouseDragged(event -> {
            treePane.setTranslateX(translateAnchorX + event.getSceneX() - mouseAnchorX);
            treePane.setTranslateY(translateAnchorY + event.getSceneY() - mouseAnchorY);
        });

        // 5. Soltar o Mouse
        treePane.setOnMouseReleased(event -> {
            treeViewport.setCursor(javafx.scene.Cursor.DEFAULT);
        });
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
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto", "*.txt"));
        selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null) fileLabel.setText(selectedFile.getName());
    }

    @FXML
    private void handleProcess() {
        try {
            String text = "";
            if (inputTabPane.getSelectionModel().getSelectedItem() == tabText) {
                text = inputArea.getText();
            } else if (selectedFile != null) {
                TextReader reader = new FileTextReader(selectedFile);
                text = reader.readContent();
            }

            if (text == null || text.isEmpty()) {
                binaryOutput.setText("Por favor, insira um texto ou selecione um arquivo.");
                return;
            }

            binaryOutput.setText("10010101110 (Simulação para: " + text + ")");

            ObservableList<HuffmanRow> data = FXCollections.observableArrayList(
                    new HuffmanRow("B", 1, "100"),
                    new HuffmanRow("A", 3, "0"),
                    new HuffmanRow("N", 2, "101")
            );
            freqTable.setItems(data);
            codeTable.setItems(data);

            // Simula Árvore
            SimulatedNode nB = new SimulatedNode("B", 1, null, null);
            SimulatedNode nN = new SimulatedNode("N", 2, null, null);
            SimulatedNode nBN = new SimulatedNode(null, 3, nB, nN);
            SimulatedNode nA = new SimulatedNode("A", 3, null, null);
            SimulatedNode root = new SimulatedNode(null, 6, nA, nBN);

            drawTree(root);

        } catch (Exception e) {
            e.printStackTrace();
            binaryOutput.setText("Erro: " + e.getMessage());
        }
    }

    private void drawTree(SimulatedNode root) {
        treePane.getChildren().clear();
        // Reseta posição e zoom ao gerar nova árvore
        treePane.setTranslateX(0);
        treePane.setTranslateY(0);
        treePane.setScaleX(1);
        treePane.setScaleY(1);

        if (root != null) drawTreeRecursive(root, 0, 50, 200); // Começa no 0,0 do pane
    }

    private void drawTreeRecursive(SimulatedNode node, double x, double y, double hGap) {
        if (node.left != null) {
            double childX = x - hGap;
            double childY = y + 100;
            Line line = new Line(x, y, childX, childY);
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(2);
            treePane.getChildren().add(line);
            drawTreeRecursive(node.left, childX, childY, hGap * 0.6);
        }

        if (node.right != null) {
            double childX = x + hGap;
            double childY = y + 100;
            Line line = new Line(x, y, childX, childY);
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(2);
            treePane.getChildren().add(line);
            drawTreeRecursive(node.right, childX, childY, hGap * 0.6);
        }

        Circle circle = new Circle(x, y, 25);
        circle.setFill(node.character == null ? Color.web("#34495e") : Color.web("#e74c3c"));
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);

        String label = (node.character == null ? "" : node.character + "\n") + node.frequency;
        Text text = new Text(label);
        text.setBoundsType(TextBoundsType.VISUAL);
        text.setX(x - 5);
        text.setY(y + 5);
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        if (node.character != null) text.setY(y);

        treePane.getChildren().addAll(circle, text);
    }

    public static class SimulatedNode {
        String character; int frequency; SimulatedNode left; SimulatedNode right;
        public SimulatedNode(String c, int f, SimulatedNode l, SimulatedNode r) {
            this.character = c; this.frequency = f; this.left = l; this.right = r;
        }
    }
}