package com.vanessaduldier.xiaoshuguan.dao;

import com.vanessaduldier.xiaoshuguan.model.Book;
import com.vanessaduldier.xiaoshuguan.service.DatabaseService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreDao {

    public void saveGenresForBook(Long bookId, List<String> genres) {
        if (bookId == null || genres == null || genres.isEmpty()) return;
        DatabaseService.executeTransactionWithResult(connection -> {

            String insertGenre =
                    "INSERT OR IGNORE INTO genre(name) VALUES (?)";

            String selectGenreId =
                    "SELECT id FROM genre WHERE name = ?";

            String insertBookGenre =
                    "INSERT OR IGNORE INTO book_genre(book_id, genre_id) VALUES (?, ?)";

            for (String genre : genres) {
                Long genreId;

                try (PreparedStatement ps = connection.prepareStatement(insertGenre)) {
                    ps.setString(1, genre);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = connection.prepareStatement(selectGenreId)) {
                    ps.setString(1, genre);
                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    genreId = rs.getLong("id");
                }

                try (PreparedStatement ps = connection.prepareStatement(insertBookGenre)) {
                    ps.setLong(1, bookId);
                    ps.setLong(2, genreId);
                    ps.executeUpdate();
                }
            }
            return null;
        });
    }

    public List<String> findGenresForBook(Long bookId) {
        List<String> genres = new ArrayList<>();

        String sql = """
            SELECT g.name
            FROM genre g
            JOIN book_genre bg ON g.id = bg.genre_id
            WHERE bg.book_id = ?
            ORDER BY g.name
        """;

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, bookId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                genres.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return genres;
    }

    public void updateBookGenres(Book book, Connection connection) {
        if (book.getId() == null || book.getGenres() == null) return;

        try {
            // 1. Alte Beziehungen löschen
            String deleteBookGenres = "DELETE FROM book_genre WHERE book_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(deleteBookGenres)) {
                ps.setLong(1, book.getId());
                ps.executeUpdate();
            }

            // 2. Neue Genres speichern
            String insertGenre = "INSERT OR IGNORE INTO genre(name) VALUES (?)";
            String selectGenreId = "SELECT id FROM genre WHERE name = ?";
            String insertBookGenre = "INSERT INTO book_genre(book_id, genre_id) VALUES (?, ?)";

            for (String genre : book.getGenres()) {
                Long genreId;

                // Genre-Tabelle aktualisieren
                try (PreparedStatement ps = connection.prepareStatement(insertGenre)) {
                    ps.setString(1, genre);
                    ps.executeUpdate();
                }

                // Genre-ID abrufen
                try (PreparedStatement ps = connection.prepareStatement(selectGenreId)) {
                    ps.setString(1, genre);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        genreId = rs.getLong("id");
                    } else {
                        throw new SQLException("Genre ID konnte nicht gefunden werden: " + genre);
                    }
                }

                // Relation einfügen
                try (PreparedStatement ps = connection.prepareStatement(insertBookGenre)) {
                    ps.setLong(1, book.getId());
                    ps.setLong(2, genreId);
                    ps.executeUpdate();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Aktualisieren der Genres für Buch: " + book.getTitle(), e);
        }
    }
}

