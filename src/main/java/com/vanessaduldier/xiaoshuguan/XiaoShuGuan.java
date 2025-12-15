package com.vanessaduldier.xiaoshuguan;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.vanessaduldier.xiaoshuguan.service.DatabaseManager;

/*
 * XiaoShuGuan
 * © 2025 Vanessa Duldier
 *
 * Licensed under CC BY-NC 4.0
 * Commercial use is not permitted.
 */
public class XiaoShuGuan extends Application {

    @Override
    public void start(Stage primaryStage) {

        DatabaseManager.initializeDatabase();

        Label label = new Label("XiaoShuGuan");
        label.setStyle("-fx-font-weight: bold; -fx-padding: 20px;");

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(label);

        Scene scene = new Scene(stackPane, 400, 300);

        primaryStage.setTitle("XiaoShuGuan");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
