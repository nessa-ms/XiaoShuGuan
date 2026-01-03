package com.vanessaduldier.xiaoshuguan;

import com.vanessaduldier.xiaoshuguan.dao.BookDao;
import com.vanessaduldier.xiaoshuguan.model.Book;
import com.vanessaduldier.xiaoshuguan.service.BookImportService;

import com.vanessaduldier.xiaoshuguan.ui.BookDetailsDialog;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
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

        // Load Icon
        primaryStage.getIcons().add(
                new Image(
                        getClass().getResourceAsStream("/ui/icon/icon.png")
                )
        );

        // build GUI
        BorderPane mainPane = new BorderPane();
        mainPane.setPadding(new Insets(10));

        // TOP BOX title and search field
        VBox topBox = new VBox(5);

        // search field
        HBox searchBox = new HBox(5);
        searchField = new TextField();
        searchField.setPromptText("Search Title, Author or Genre...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> searchBooks());

        searchBox.getChildren().addAll(
                searchField
        );

        topBox.getChildren().addAll(searchBox);
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

        topBox.setPadding(new Insets(20));
        searchBox.setPadding(new Insets(10));
        buttonBox.setPadding(new Insets(10));

        bookTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bookTable.setFixedCellSize(22);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(bookTable.widthProperty());
        clip.heightProperty().bind(bookTable.heightProperty());
        clip.setArcWidth(28);
        clip.setArcHeight(28);
        bookTable.setClip(clip);


        // window setup
        Scene scene = new Scene(mainPane, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/cherry-blossom.css").toExternalForm());
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
        BookDetailsDialog dialog = new BookDetailsDialog(book);
        dialog.showAndWait();
        refreshBooks();
    }

    public static void main(String[] args) {
        launch(args);
    }
}