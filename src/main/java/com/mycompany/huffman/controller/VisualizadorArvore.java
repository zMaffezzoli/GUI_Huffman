package com.mycompany.huffman.controller;

import com.mycompany.huffman.model.No;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class VisualizadorArvore {
    private final Pane treePane;
    private final StackPane treeViewport;

    // Controle do painel
    private double mouseAnchorX, mouseAnchorY;
    private double translateAnchorX, translateAnchorY;

    public VisualizadorArvore(Pane treePane, StackPane treeViewport) {
        this.treePane = treePane;
        this.treeViewport = treeViewport;
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

        treePane.setOnMouseReleased(event -> treeViewport.setCursor(javafx.scene.Cursor.DEFAULT));
    }

    public void drawTree(No root) {
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

        // Desenha as linhas para os filhos
        if (node.getFilhoEsquerdo() != null) {
            drawConnection(x, y, xPositions.get(node.getFilhoEsquerdo()), y + 100);
            drawTreeRecursive(node.getFilhoEsquerdo(), y + 100, xPositions);
        }
        if (node.getFilhoDireito() != null) {
            drawConnection(x, y, xPositions.get(node.getFilhoDireito()), y + 100);
            drawTreeRecursive(node.getFilhoDireito(), y + 100, xPositions);
        }

        drawNodeContent(node, x, y);
    }

    private void drawConnection(double x1, double y1, double x2, double y2) {
        // Desenha as linhas que unem pais e filhos
        Line line = new Line(x1, y1 + 26, x2, y2 - 26);
        line.setStroke(Color.GRAY);
        line.setStrokeWidth(2);
        line.toBack();
        treePane.getChildren().add(line);
    }

    private void drawNodeContent(No node, double x, double y) {
        // Desenha o texto na tela
        String rawChar = node.getCaracter() == null ? "" : node.getCaracter();

        // Tratamento dos caracteres especiais
        String charText = rawChar.replace("\n", "\\n").replace("\r", "\\r")
                .replace("\t", "\\t").replace(" ", "space");
        String fullLabel = charText + "\n" + node.getFrequencia();

        Text text = new Text(fullLabel);
        text.setBoundsType(TextBoundsType.VISUAL);

        // Verifica se e espaco para estilizacao personalizada
        if (node.isFolha() && charText.equals("space")) {
            text.setFont(Font.font("System", FontWeight.BOLD, FontPosture.ITALIC, 13));
        } else {
            text.setFont(Font.font("System", FontWeight.BOLD, 13));
        }

        text.setFill(Color.WHITE);
        text.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // Formato da borda
        double fixedHeight = 52;
        double textWidth = text.getLayoutBounds().getWidth();
        double capsuleWidth = Math.max(fixedHeight, textWidth + 30);

        // Desenha a borda
        Rectangle capsule = new Rectangle();
        capsule.setWidth(capsuleWidth);
        capsule.setHeight(fixedHeight);
        capsule.setArcWidth(fixedHeight);
        capsule.setArcHeight(fixedHeight);
        capsule.setX(x - (capsuleWidth / 2));
        capsule.setY(y - (fixedHeight / 2));

        capsule.setFill(node.isFolha() ? Color.web("#e74c3c") : Color.web("#34495e"));
        capsule.setStroke(Color.BLACK);
        capsule.setStrokeWidth(2);

        text.setX(x - (textWidth / 2));
        text.setY(y + text.getLayoutBounds().getHeight() / 10);

        treePane.getChildren().addAll(capsule, text);
    }

    private void calculatePositions(No node, Map<No, Double> xPositions, AtomicInteger leafIndex, double spacing) {
        // Calcula a distancia entre os nos para que a visualizacao fique correta
        // sem sobreposicao de nos

        if (node == null) return;

        if (node.isFolha()) {
            double x = leafIndex.getAndIncrement() * spacing;
            xPositions.put(node, x);
        } else {
            // Recursividade para os nós filhos
            calculatePositions(node.getFilhoEsquerdo(), xPositions, leafIndex, spacing);
            calculatePositions(node.getFilhoDireito(), xPositions, leafIndex, spacing);

            double leftX = xPositions.getOrDefault(node.getFilhoEsquerdo(), 0.0);
            double rightX = xPositions.containsKey(node.getFilhoDireito()) ?
                    xPositions.get(node.getFilhoDireito()) : leftX;

            xPositions.put(node, (leftX + rightX) / 2.0);
        }
    }
}