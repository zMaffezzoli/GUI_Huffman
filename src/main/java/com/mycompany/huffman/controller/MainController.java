package com.mycompany.huffman.controller;

import com.mycompany.huffman.model.Compressor;
import com.mycompany.huffman.model.HuffmanRow;
import com.mycompany.huffman.model.No;
import com.mycompany.huffman.util.FileTextReader;
import com.mycompany.huffman.util.TextReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.FileChooser;
import javafx.util.Callback; // Import necessário para o CellFactory

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

    // ÁREA DA ÁRVORE
    @FXML private StackPane treeViewport;
    @FXML private Pane treePane;

    private File selectedFile;

    // Variáveis para controle de arrastar (Pan)
    private double mouseAnchorX;
    private double mouseAnchorY;
    private double translateAnchorX;
    private double translateAnchorY;

    @FXML
    public void initialize() {
        // Configura as colunas das tabelas
        colSymbolFreq.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        colFreq.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        colSymbolCode.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));

        // --- CUSTOMIZAÇÃO DAS CÉLULAS (ITÁLICO PARA 'space') ---
        // Criamos uma fábrica de células customizada que aplica estilo condicional
        Callback<TableColumn<HuffmanRow, String>, TableCell<HuffmanRow, String>> cellFactory = param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);

                    // Se o texto for "space", aplica Negrito + Itálico
                    if ("space".equals(item)) {
                        setFont(Font.font("System", FontWeight.BOLD, FontPosture.ITALIC, 13));
                    } else {
                        // Senão, aplica apenas Negrito (padrão do resto)
                        // É importante resetar para não herdar estilo errado na reciclagem da tabela
                        setFont(Font.font("System", FontWeight.BOLD, 13));
                    }
                }
            }
        };

        // Aplica essa fábrica nas colunas de símbolo
        colSymbolFreq.setCellFactory(cellFactory);
        colSymbolCode.setCellFactory(cellFactory);

        freqTable.setSelectionModel(null);
        codeTable.setSelectionModel(null);

        setupZoomAndPan();
    }

    private void setupZoomAndPan() {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(treeViewport.widthProperty());
        clip.heightProperty().bind(treeViewport.heightProperty());
        treeViewport.setClip(clip);

        treeViewport.setOnScroll(event -> {
            if (event.isControlDown()) {
                event.consume();
                double deltaY = event.getDeltaY();
                if (deltaY == 0) return;

                double zoomFactor = (deltaY > 0) ? 1.1 : 0.9;

                treePane.setScaleX(treePane.getScaleX() * zoomFactor);
                treePane.setScaleY(treePane.getScaleY() * zoomFactor);
            }
        });

        treePane.setOnMousePressed(event -> {
            mouseAnchorX = event.getSceneX();
            mouseAnchorY = event.getSceneY();
            translateAnchorX = treePane.getTranslateX();
            translateAnchorY = treePane.getTranslateY();
            treeViewport.setCursor(javafx.scene.Cursor.CLOSED_HAND);
        });

        treePane.setOnMouseDragged(event -> {
            treePane.setTranslateX(translateAnchorX + event.getSceneX() - mouseAnchorX);
            treePane.setTranslateY(translateAnchorY + event.getSceneY() - mouseAnchorY);
        });

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

                if (text != null) {
                    text = text.replaceAll("[\\r\\n]+$", "");
                }
            }

            if (text == null || text.isEmpty()) {
                binaryOutput.setText("Por favor, insira um texto ou selecione um arquivo.");
                return;
            }

            Compressor compressor = new Compressor(text);
            binaryOutput.setText(compressor.getBinario());

            ObservableList<HuffmanRow> tableData = FXCollections.observableArrayList();

            Map<Character, Integer> freqMap = compressor.getTabelaFrequencia().getTabela();
            Map<String, String> binaryMap = compressor.getTabelaBinaria().getTabela();

            for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
                String symbol = String.valueOf(entry.getKey())
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
                        .replace("\t", "\\t")
                        .replace(" ", "space");

                Integer frequency = entry.getValue();

                String originalKey = String.valueOf(entry.getKey());
                String code = binaryMap.get(originalKey);

                tableData.add(new HuffmanRow(symbol, frequency, code));
            }

            tableData.sort((r1, r2) -> {
                int freqCompare = Integer.compare(r2.getFrequency(), r1.getFrequency());
                if (freqCompare != 0) return freqCompare;
                return r1.getSymbol().compareTo(r2.getSymbol());
            });

            freqTable.setItems(tableData);
            codeTable.setItems(tableData);

            if (compressor.getArvore() != null) {
                drawTree(compressor.getArvore().getRaiz());
            }

        } catch (Exception e) {
            e.printStackTrace();
            binaryOutput.setText("Erro: " + e.getMessage());
        }
    }

    private void drawTree(No root) {
        treePane.getChildren().clear();
        treePane.setTranslateX(0);
        treePane.setTranslateY(0);
        treePane.setScaleX(1);
        treePane.setScaleY(1);

        if (root != null) {
            Map<No, Double> xPositions = new HashMap<>();
            AtomicInteger leafIndex = new AtomicInteger(0);
            double leafSpacing = 85.0;

            calculatePositions(root, xPositions, leafIndex, leafSpacing);

            double rootX = xPositions.get(root);
            treePane.setTranslateX(-rootX + 400);

            drawTreeRecursive(root, 50, xPositions);
        }
    }

    private void drawTreeRecursive(No node, double y, Map<No, Double> xPositions) {
        double x = xPositions.get(node);

        // --- 1. Linhas ---
        if (node.getFilho_esquerdo() != null) {
            double childX = xPositions.get(node.getFilho_esquerdo());
            double childY = y + 100;
            Line line = new Line(x, y + 26, childX, childY - 26);
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(2);
            line.toBack();
            treePane.getChildren().add(line);
            drawTreeRecursive(node.getFilho_esquerdo(), childY, xPositions);
        }

        if (node.getFilho_direito() != null) {
            double childX = xPositions.get(node.getFilho_direito());
            double childY = y + 100;
            Line line = new Line(x, y + 26, childX, childY - 26);
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(2);
            line.toBack();
            treePane.getChildren().add(line);
            drawTreeRecursive(node.getFilho_direito(), childY, xPositions);
        }

        // --- 2. Preparação do Texto ---
        String rawChar = node.getCaracter() == null ? "" : node.getCaracter();

        String charText = rawChar
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace(" ", "space");

        String freqText = String.valueOf(node.getFrequencia());
        String fullLabel = charText + "\n" + freqText;

        Text text = new Text(fullLabel);
        text.setBoundsType(TextBoundsType.VISUAL);

        // --- 3. Lógica de Fonte na Árvore ---
        if (node.isFolha() && charText.equals("space")) {
            text.setFont(Font.font("System", FontWeight.BOLD, FontPosture.ITALIC, 13));
        } else {
            text.setFont(Font.font("System", FontWeight.BOLD, 13));
        }

        text.setFill(Color.WHITE);
        text.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        text.setWrappingWidth(0);

        // --- 4. Cápsula ---
        double fixedHeight = 52;
        double textWidth = text.getLayoutBounds().getWidth();
        double capsuleWidth = Math.max(fixedHeight, textWidth + 30);

        Rectangle capsule = new Rectangle();
        capsule.setWidth(capsuleWidth);
        capsule.setHeight(fixedHeight);
        capsule.setArcWidth(fixedHeight);
        capsule.setArcHeight(fixedHeight);

        capsule.setX(x - (capsuleWidth / 2));
        capsule.setY(y - (fixedHeight / 2));

        boolean isLeaf = node.isFolha();
        capsule.setFill(isLeaf ? Color.web("#e74c3c") : Color.web("#34495e"));
        capsule.setStroke(Color.BLACK);
        capsule.setStrokeWidth(2);

        text.setX(x - (textWidth / 2));
        text.setY(y + text.getLayoutBounds().getHeight() / 10);

        treePane.getChildren().addAll(capsule, text);
    }

    private void calculatePositions(No node, Map<No, Double> xPositions, AtomicInteger leafIndex, double spacing) {
        if (node == null) return;

        if (node.isFolha()) {
            double x = leafIndex.getAndIncrement() * spacing;
            xPositions.put(node, x);
        } else {
            calculatePositions(node.getFilho_esquerdo(), xPositions, leafIndex, spacing);
            calculatePositions(node.getFilho_direito(), xPositions, leafIndex, spacing);

            double leftX = xPositions.containsKey(node.getFilho_esquerdo()) ?
                    xPositions.get(node.getFilho_esquerdo()) : 0;

            double rightX = xPositions.containsKey(node.getFilho_direito()) ?
                    xPositions.get(node.getFilho_direito()) : leftX;

            double myX = (leftX + rightX) / 2.0;
            xPositions.put(node, myX);
        }
    }
}