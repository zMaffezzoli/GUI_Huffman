package com.mycompany.huffman.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HuffmanApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Carrega o arquivo FXML principal da pasta resources/fxml/
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main_screen.fxml"));

        Scene scene = new Scene(root);

        stage.setTitle("Huffman Studio Pro");
        stage.setScene(scene);
        stage.setMaximized(true); // Abre em tela cheia
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}