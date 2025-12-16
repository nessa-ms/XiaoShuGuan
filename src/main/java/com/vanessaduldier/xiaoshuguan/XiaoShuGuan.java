package com.vanessaduldier.xiaoshuguan;

import com.vanessaduldier.xiaoshuguan.dao.BookDao;
import com.vanessaduldier.xiaoshuguan.model.Book;
import com.vanessaduldier.xiaoshuguan.parser.EpubParser;
import com.vanessaduldier.xiaoshuguan.service.DatabaseService;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/*
 * XiaoShuGuan
 * © 2025 Vanessa Duldier
 *
 * Licensed under CC BY-NC 4.0
 * Commercial use is not permitted.
 */
public class XiaoShuGuan extends Application {
    private final EpubParser parser = new EpubParser();
    private final BookDao bookDao = new BookDao();

    @Override
    public void start(Stage primaryStage) {

        DatabaseService.initializeDatabase();
        // Book book = new Book(123, "Katabasis", new Author(),);
        // DatabaseService.executeTransaction(bookDao.insert(DatabaseService.getConnection(), book));

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
