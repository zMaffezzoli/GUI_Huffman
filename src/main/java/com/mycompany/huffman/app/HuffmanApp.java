package com.mycompany.huffman.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HuffmanApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Carrega o FXML
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main_screen.fxml"));
        Scene scene = new Scene(root);

        // ADICIONA O CSS (Atualizado para a pasta styles)
        String css = getClass().getResource("/styles/application.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Huffman Studio Pro");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}