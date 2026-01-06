package com.vanessaduldier.xiaoshuguan.ui;

import com.vanessaduldier.xiaoshuguan.dao.BookDao;
import com.vanessaduldier.xiaoshuguan.model.Author;
import com.vanessaduldier.xiaoshuguan.model.Book;
import com.vanessaduldier.xiaoshuguan.service.GoodreadsService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookDetailsDialog extends Dialog<Void> {
    private final GoodreadsService goodreadsService = new GoodreadsService();
    private final BookDao bookDao = new BookDao();
    private Image icon = new Image(getClass().getResourceAsStream("/ui/icon/icon.png"));

    public BookDetailsDialog(Book book) {
        Book original = book.copy();

        // add stylesheet
        getDialogPane().getStylesheets().add(
                getClass().getResource("/styles/cherry-blossom.css").toExternalForm()
        );

        // Add Icon to Bookdetails
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(icon);

        setTitle("Buchdetails: " + book.getTitle());

        // Content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPrefWidth(400);

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

        Label descriptionLabel = new Label("Beschreibung:");
        grid.add(descriptionLabel, 0, 3);

        TextArea descriptionArea = new TextArea(book.getDescription());
        descriptionArea.setWrapText(true);
        descriptionArea.setEditable(false);
        descriptionArea.setPrefRowCount(6);

        grid.add(descriptionArea, 0, 4);
        GridPane.setColumnSpan(descriptionArea, 2);
        GridPane.setHgrow(descriptionArea, Priority.ALWAYS);

        // Buttons
        ButtonType saveButton = new ButtonType("Speichern", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType linkButton = new ButtonType("Goodreads Link Hinzufügen");

        getDialogPane().getButtonTypes().addAll(linkButton, saveButton, cancelButton);

        getDialogPane().setContent(grid);

        Node goodreadsButton = getDialogPane().lookupButton(linkButton);
        goodreadsButton.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            openGoodreadsDialog(book, genresField, grid);
        });


        // Set result converter to handle save button
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                // TITLE
                book.setTitle(titleField.getText());
                // AUTHORS
                List<Author> updatedAuthors = List.of(authorsField.getText().split(","))
                        .stream()
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(name -> new Author(null, name))
                        .collect(Collectors.toList());
                book.setAuthors(updatedAuthors);
                // GENRES
                List<String> updatedGenres = List.of(genresField.getText().split(","))
                        .stream()
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                book.setGenres(updatedGenres);
                bookDao.update(book);
            } else {
                // X (cancel)
                book.restore(original);
            }
            return null;
        });
    }

    private void openGoodreadsDialog(Book book, TextField genresField, GridPane grid) {
        TextInputDialog linkDialog = new TextInputDialog("");
        // Add Stylesheet to Link Entry
        linkDialog.getDialogPane().getStylesheets().add("/styles/cherry-blossom.css");
        linkDialog.setTitle("Goodreads Link hinzufügen");
        linkDialog.setHeaderText("Goodreads Link");
        linkDialog.setContentText("Trage den Link ein:"); // translate to german or remove

        // Add Icon to link input window
        Stage stage = (Stage) linkDialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(icon);

        linkDialog.showAndWait().ifPresent(link -> {
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

                // Add Icon to confirmation popup
                Stage confirmationStage = (Stage) confirmation.getDialogPane().getScene().getWindow();
                confirmationStage.getIcons().add(icon);

                confirmation.setTitle("Genres geladen");
                // Add Stylesheet to confirmation
                confirmation.getDialogPane().getStylesheets().add(
                        getClass().getResource("/styles/cherry-blossom.css").toExternalForm()
                );

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
    }
}
