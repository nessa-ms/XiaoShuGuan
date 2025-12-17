package com.vanessaduldier.xiaoshuguan;

import com.vanessaduldier.xiaoshuguan.dao.BookDao;
import com.vanessaduldier.xiaoshuguan.model.Book;
import com.vanessaduldier.xiaoshuguan.service.BookImportService;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class XiaoShuGuan extends Application {

    private BookImportService importService;
    private BookDao bookDao;
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        // Datenbank wird automatisch beim ersten Aufruf von DatabaseService initialisiert

        // Services initialisieren
        importService = new BookImportService();
        bookDao = new BookDao();

        // GUI aufbauen
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label titleLabel = new Label("XiaoShuGuan - Persönliche Bücherei");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button importButton = new Button("EPUB importieren");
        importButton.setOnAction(e -> importEpubFile(primaryStage));

        Button listButton = new Button("Bücher anzeigen");
        listButton.setOnAction(e -> showBooks());

        statusLabel = new Label("Bereit");

        root.getChildren().addAll(titleLabel, importButton, listButton, statusLabel);

        Scene scene = new Scene(root, 400, 300);

        primaryStage.setTitle("XiaoShuGuan");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void importEpubFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("EPUB-Datei auswählen");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("EPUB Dateien", "*.epub")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                statusLabel.setText("Importiere: " + selectedFile.getName());
                Book book = importService.importEpub(selectedFile);
                statusLabel.setText("Import erfolgreich: " + book.getTitle());
            } catch (BookImportService.ImportException e) {
                statusLabel.setText("Import fehlgeschlagen: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showBooks() {
        List<Book> books = bookDao.findAll();
        StringBuilder sb = new StringBuilder("Gespeicherte Bücher:\n");

        if (books.isEmpty()) {
            sb.append("Keine Bücher vorhanden.");
        } else {
            for (Book book : books) {
                sb.append("- ").append(book.getTitle())
                        .append(" von ").append(book.getAuthorName())
                        .append("\n");
            }
        }

        statusLabel.setText(sb.toString());
    }

    public static void main(String[] args) {
        launch(args);
    }
}