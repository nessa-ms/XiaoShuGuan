package com.vanessaduldier.xiaoshuguan.ui;

import com.vanessaduldier.xiaoshuguan.model.Author;
import com.vanessaduldier.xiaoshuguan.model.Book;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;
import java.util.stream.Collectors;

public class BookDetailsDialog extends Dialog<Void> {

    public BookDetailsDialog(Book book) {
        setTitle("Buchdetails: " + book.getTitle());
        setHeaderText("Details anzeigen und bearbeiten");

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
        ButtonType linkButton = new ButtonType("Add Goodreads Link", ButtonBar.ButtonData.OTHER);

        getDialogPane().getButtonTypes().addAll(saveButton, cancelButton);
        getDialogPane().setContent(grid);

        Button addLinkButton = (Button) getDialogPane().lookupButton(linkButton);
        addLinkButton.setOnAction(e -> {
            TextInputDialog linkDialog = new TextInputDialog("");
            linkDialog.setTitle("Enter Goodreads Link to Book");
            linkDialog.setHeaderText("Goodreads Link");
            linkDialog.setContentText("Please enter the Goodreads link:");

            Optional<String> result = linkDialog.showAndWait();
            result.ifPresent(link -> {
                book.setGoodreadsLink(link);  // save link to book
                System.out.println("Goodreads link added: " + link);

                // Show confirmation
                Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
                confirmation.setTitle("Link Added");
                confirmation.setHeaderText(null);
                confirmation.setContentText("Goodreads link has been added: " + link);
                confirmation.showAndWait();
            });
        });

        // Set result converter to handle save button
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                // Update the book with new values
                book.setTitle(titleField.getText());
                // Note: You would need more logic to properly update authors and genres
                System.out.println("Book saved with new title: " + book.getTitle());
            }
            return null;
        });
    }
}
