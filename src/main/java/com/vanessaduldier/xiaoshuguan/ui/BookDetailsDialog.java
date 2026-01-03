package com.vanessaduldier.xiaoshuguan.ui;

import com.vanessaduldier.xiaoshuguan.dao.BookDao;
import com.vanessaduldier.xiaoshuguan.model.Author;
import com.vanessaduldier.xiaoshuguan.model.Book;
import com.vanessaduldier.xiaoshuguan.service.GoodreadsService;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookDetailsDialog extends Dialog<Void> {
    private final GoodreadsService goodreadsService = new GoodreadsService();
    private final BookDao bookDao = new BookDao();

    public BookDetailsDialog(Book book) {
        Book original = book.copy();

        // add stylesheet
        getDialogPane().getStylesheets().add(
                getClass().getResource("/styles/cherry-blossom.css").toExternalForm()
        );

        setTitle("Buchdetails: " + book.getTitle());

        // Content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Titel:"), 0, 0);
        TextField titleField = new TextField(book.getTitle());
        grid.add(titleField, 1, 0);

        grid.add(new Label("Autoren:"), 0, 1);
        TextField authorsField = new TextField(
                book.getAuthors().stream()
                        .map(Author::getName)
                        .collect(Collectors.joining(", "))
        );
        grid.add(authorsField, 1, 1);

        grid.add(new Label("Genres:"), 0, 2);
        TextField genresField = new TextField(
                String.join(", ", book.getGenres())
        );
        grid.add(genresField, 1, 2);

        // Buttons
        ButtonType saveButton = new ButtonType("Speichern", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().addAll(saveButton, cancelButton);

        Button linkButton = new Button("Add Goodreads Link");
        grid.add(linkButton, 1, 3);

        getDialogPane().setContent(grid);


        linkButton.setOnAction(e -> {
            TextInputDialog linkDialog = new TextInputDialog("");
            linkDialog.setTitle("Enter Goodreads Link to Book");
            linkDialog.setHeaderText("Goodreads Link");
            linkDialog.setContentText("Please enter the Goodreads link:");

            Optional<String> result = linkDialog.showAndWait();
            result.ifPresent(link -> {
                book.setGoodreadsLink(link);

                ProgressIndicator progress = new ProgressIndicator();
                grid.add(progress, 1, 3);

                Task<List<String>> loadGenresTask = new Task<>() {
                    @Override
                    protected List<String> call() throws Exception {
                        return goodreadsService.fetchGenres(link);
                    }
                };

                loadGenresTask.setOnSucceeded(ev -> {
                    List<String> genres = loadGenresTask.getValue();
                    book.setGenres(genres);

                    // UI aktualisieren
                    genresField.setText(String.join(", ", genres));
                    grid.getChildren().remove(progress);

                    Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
                    confirmation.setTitle("Genres geladen");
                    confirmation.setHeaderText(null);
                    confirmation.setContentText(
                            "Genres von Goodreads übernommen:\n" +
                                    String.join(", ", genres)
                    );
                    confirmation.showAndWait();
                });

                loadGenresTask.setOnFailed(ev -> {
                    grid.getChildren().remove(progress);

                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Fehler");
                    error.setHeaderText("Goodreads konnte nicht geladen werden");
                    error.setContentText(loadGenresTask.getException().getMessage());
                    error.showAndWait();
                });

                new Thread(loadGenresTask).start();
            });
        });

        // Set result converter to handle save button
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                book.setTitle(titleField.getText());
                bookDao.update(book);
            } else {
                // X (cancel)
                book.restore(original);
            }
            return null;
        });
    }
}
