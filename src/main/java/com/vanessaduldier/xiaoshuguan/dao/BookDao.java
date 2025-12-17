package com.vanessaduldier.xiaoshuguan.dao;

import com.vanessaduldier.xiaoshuguan.model.Book;
import com.vanessaduldier.xiaoshuguan.service.DatabaseService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object Book
 * @author Vanessa Duldier
 */
public class BookDao {

    public BookDao() {
        // Dao wird automatisch erstellt
    }

    /**
     * Insert a Book into Database
     */
    public Long insert(Book book) {
        DatabaseService.executeTransaction(connection -> {
            String sql = """
                INSERT INTO books (
                    title, author_id, cover_image, pages, isbn, 
                    published_date, publisher, description, file_path, 
                    file_hash, main_genre, subgenre1, subgenre2, subgenre3,
                    goodreads_id, goodreads_url, rating, status, notes
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, book.getTitle());
                ps.setLong(2, extractAuthorId(book)); // Author muss existieren
                ps.setString(3, book.getCoverImage());
                ps.setObject(4, book.getPageCount());
                ps.setString(5, book.getIsbn());
                ps.setString(6, book.getPublishedDate());
                ps.setString(7, book.getPublisher());
                ps.setString(8, book.getDescription());
                ps.setString(9, book.getFilePath());
                ps.setString(10, calculateFileHash(book.getFilePath()));

                // Genres aus Liste extrahieren
                List<String> genres = book.getGenres();
                ps.setString(11, genres.size() > 0 ? genres.get(0) : null);
                ps.setString(12, genres.size() > 1 ? genres.get(1) : null);
                ps.setString(13, genres.size() > 2 ? genres.get(2) : null);
                ps.setString(14, genres.size() > 3 ? genres.get(3) : null);

                // Goodreads (später)
                ps.setString(15, null);
                ps.setString(16, null);

                // User Daten
                ps.setObject(17, null); // rating
                ps.setString(18, "unread");
                ps.setString(19, null); // notes

                ps.executeUpdate();

                // Generierte ID holen
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        book.setId(rs.getLong(1));
                    }
                }
            }
        });

        return book.getId();
    }

    private Long extractAuthorId(Book book) {
        // Hier müsstest du zuerst den Author in die DB speichern
        // Für erste Version: erstelle immer neuen Author
        // TODO: AuthorDao verwenden
        return 1L; // Temporär
    }

    private String calculateFileHash(String filePath) {
        // Einfache Hash-Berechnung
        // TODO: Implementieren (z.B. MD5 oder SHA-256)
        return "hash_" + filePath.hashCode();
    }

    /**
     * Alle Bücher aus der Datenbank holen
     */
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();

        String sql = "SELECT * FROM books ORDER BY title";

        try (Connection conn = DatabaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Book book = mapResultSetToBook(rs);
                books.add(book);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden der Bücher", e);
        }

        return books;
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        // TODO: Vollständige Mapping-Logik implementieren
        Book book = new Book(
                rs.getLong("id"),
                rs.getString("title"),
                new ArrayList<>(), // Authors müssten separat geladen werden
                extractGenresFromResultSet(rs)
        );

        book.setDescription(rs.getString("description"));
        book.setPublisher(rs.getString("publisher"));
        book.setIsbn(rs.getString("isbn"));
        book.setFilePath(rs.getString("file_path"));

        return book;
    }

    private List<String> extractGenresFromResultSet(ResultSet rs) throws SQLException {
        List<String> genres = new ArrayList<>();
        addIfNotNull(genres, rs.getString("main_genre"));
        addIfNotNull(genres, rs.getString("subgenre1"));
        addIfNotNull(genres, rs.getString("subgenre2"));
        addIfNotNull(genres, rs.getString("subgenre3"));
        return genres;
    }

    private void addIfNotNull(List<String> list, String item) {
        if (item != null && !item.trim().isEmpty()) {
            list.add(item);
        }
    }
}
