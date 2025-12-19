package com.vanessaduldier.xiaoshuguan.ui;

import com.vanessaduldier.xiaoshuguan.model.Author;
import com.vanessaduldier.xiaoshuguan.model.Book;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

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
        getDialogPane().getButtonTypes().addAll(saveButton, cancelButton);

        getDialogPane().setContent(grid);
    }
}
