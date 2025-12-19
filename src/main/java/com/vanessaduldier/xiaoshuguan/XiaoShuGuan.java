package com.vanessaduldier.xiaoshuguan;

import com.vanessaduldier.xiaoshuguan.dao.BookDao;
import com.vanessaduldier.xiaoshuguan.model.Book;
import com.vanessaduldier.xiaoshuguan.service.BookImportService;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * App
 * @author Vanessa Duldier
 */
public class XiaoShuGuan extends Application {

    private BookImportService importService;
    private BookDao bookDao;
    private Label statusLabel;
    private TableView<Book> bookTable;
    private TextField searchField;

    @Override
    public void start(Stage primaryStage) {
        // initialize services
        importService = new BookImportService();
        bookDao = new BookDao();

        // build GUI
        BorderPane mainPane = new BorderPane();
        mainPane.setPadding(new Insets(10));

        // TOP BOX title and search field
        VBox topBox = new VBox(10);

        Label titleLabel = new Label("XiaoShuGuan - Personal Library");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // search field
        HBox searchBox = new HBox(10);
        searchField = new TextField();
        searchField.setPromptText("Suche nach Titel, Autor oder Genre...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> searchBooks());

        Button clearButton = new Button("clear");
        clearButton.setOnAction(e -> {
            searchField.clear();
            refreshBooks();
        });

        searchBox.getChildren().addAll(
                new Label("Suche:"),
                searchField,
                clearButton
        );

        topBox.getChildren().addAll(titleLabel, searchBox);
        mainPane.setTop(topBox);

        // CENTRE table
        setupBookTable();
        mainPane.setCenter(bookTable);

        // BOTTOM state and buttons
        VBox bottomBox = new VBox(10);

        // buttons
        HBox buttonBox = new HBox(10);
        Button importButton = new Button("EPUB importieren");
        importButton.setOnAction(e -> importEpubFile(primaryStage));

        Button refreshButton = new Button("Aktualisieren");
        refreshButton.setOnAction(e -> refreshBooks());

        buttonBox.getChildren().addAll(importButton, refreshButton);

        statusLabel = new Label("Bereit");

        bottomBox.getChildren().addAll(buttonBox, statusLabel);
        mainPane.setBottom(bottomBox);


        // window setup
        Scene scene = new Scene(mainPane, 900, 600);
        primaryStage.setTitle("XiaoShuGuan");
        primaryStage.setScene(scene);
        primaryStage.show();

        // load initial books
        refreshBooks();
    }

    private void setupBookTable() {
        bookTable = new TableView<>();

        // Columns
        TableColumn<Book, String> titleCol = new TableColumn<>("Titel");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(300);

        TableColumn<Book, String> authorCol = new TableColumn<>("Autor");
        authorCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAuthorName()));
        authorCol.setPrefWidth(200);

        TableColumn<Book, String> genreCol = new TableColumn<>("Genres");
        genreCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.join(", ", cellData.getValue().getGenres())));
        genreCol.setPrefWidth(200);

        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getIsbn() != null ?
                        cellData.getValue().getIsbn() : ""));
        isbnCol.setPrefWidth(120);

        bookTable.getColumns().addAll(titleCol, authorCol, genreCol, isbnCol);

        // show book details on double click
        bookTable.setRowFactory(tv -> {
            TableRow<Book> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Book book = row.getItem();
                    showBookDetails(book);
                }
            });
            return row;
        });
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
                refreshBooks(); // Tabelle aktualisieren
            } catch (BookImportService.ImportException e) {
                statusLabel.setText("Import fehlgeschlagen: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void refreshBooks() {
        try {
            List<Book> books = bookDao.findAll();
            bookTable.setItems(FXCollections.observableArrayList(books));
            statusLabel.setText("Geladen: " + books.size() + " Bücher");
        } catch (Exception e) {
            statusLabel.setText("Fehler beim Laden: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void searchBooks() {
        String searchTerm = searchField.getText().toLowerCase().trim();

        if (searchTerm.isEmpty()) {
            refreshBooks();
            return;
        }

        try {
            List<Book> allBooks = bookDao.findAll();
            List<Book> filteredBooks = allBooks.stream()
                    .filter(book ->
                            book.getTitle().toLowerCase().contains(searchTerm) ||
                                    book.getAuthorName().toLowerCase().contains(searchTerm) ||
                                    book.getGenres().stream().anyMatch(genre ->
                                            genre.toLowerCase().contains(searchTerm)) ||
                                    (book.getIsbn() != null && book.getIsbn().contains(searchTerm)) ||
                                    (book.getPublisher() != null && book.getPublisher().toLowerCase().contains(searchTerm)))
                    .collect(Collectors.toList());

            bookTable.setItems(FXCollections.observableArrayList(filteredBooks));
            statusLabel.setText("Gefunden: " + filteredBooks.size() + " von " + allBooks.size() + " Büchern");
        } catch (Exception e) {
            statusLabel.setText("Fehler bei der Suche: " + e.getMessage());
        }
    }

    private void showBookDetails(Book book) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Buchdetails");
        alert.setHeaderText(book.getTitle());

        String content = "Autor: " + book.getAuthorName() + "\n" +
                "Genres: " + String.join(", ", book.getGenres()) + "\n" +
                (book.getIsbn() != null ? "ISBN: " + book.getIsbn() + "\n" : "") +
                (book.getPublisher() != null ? "Verlag: " + book.getPublisher() + "\n" : "") +
                (book.getDescription() != null && !book.getDescription().isEmpty() ?
                        "\nBeschreibung:\n" + book.getDescription() : "");

        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}