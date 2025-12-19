package com.vanessaduldier.xiaoshuguan.dao;

import com.vanessaduldier.xiaoshuguan.model.Author;
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
    private final AuthorDao authorDao = new AuthorDao();

    public BookDao() {

    }

    /**
     * Insert a Book into Database
     */
    public Long insert(Book book) {
        return DatabaseService.executeTransactionWithResult(connection -> {
            // save authors and collect their ids
            List<Long> authorIds = new ArrayList<>();
            for (Author author : book.getAuthors()) {
                Long authorId = authorDao.insert(author);
                authorIds.add(authorId);
            }

            // save book with first author
            String sql = """
                    INSERT INTO books (
                        title, author_id, cover_image, pages, isbn, 
                        published_date, publisher, description, file_path, 
                        file_hash, main_genre, subgenre1, subgenre2, subgenre3,
                        goodreads_id, goodreads_url, rating, status, notes
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            Long bookId;

            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, book.getTitle());
                ps.setLong(2, authorIds.isEmpty() ? 1L : authorIds.get(0));
                ps.setString(3, book.getCoverImage());
                ps.setObject(4, book.getPageCount());
                ps.setString(5, book.getIsbn());
                ps.setString(6, book.getPublishedDate());
                ps.setString(7, book.getPublisher());
                ps.setString(8, book.getDescription());
                ps.setString(9, book.getFilePath());
                ps.setString(10, calculateFileHash(book.getFilePath()));

                // extract genres from list
                List<String> genres = book.getGenres();
                ps.setString(11, !genres.isEmpty() ? genres.get(0) : null);
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
                        bookId = rs.getLong(1);
                    } else {
                        throw new SQLException("Keine ID generiert");
                    }
                }
            }

            // save many to many relations in book_author
            String bookAuthorSql = "INSERT INTO book_author (book_id, author_id) VALUES (?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(bookAuthorSql)) {
                for (Long authorId : authorIds) {
                    ps.setLong(1, bookId);
                    ps.setLong(2, authorId);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            return bookId;
        });
    }

    private String calculateFileHash(String filePath) {
        // Einfache Hash-Berechnung
        // TODO: Implementieren (z.B. MD5 oder SHA-256)
        return "hash_" + filePath.hashCode();
    }

    /**
     * Get all Books from Database
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



    public Long findBookId(String title) {
        return null;
    }

    public Book findBook(Long id) {
        return null;
    }

    private List<Author> loadAuthors(Long bookId) {
        List<Author> authors = new ArrayList<>();
        String sql = """
                SELECT a.* FROM author a
                JOIN book_author ba ON a.id = ba.book_id
                WHERE ba.book_id = ?
                """;

        try (Connection connection = DatabaseService.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    authors.add(new Author(
                            rs.getLong("id"),
                            rs.getString("name")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden der Autoren", e);
        }
        return authors;
    }

    // helper functions
    private void addIfNotNull(List<String> list, String item) {
        if (item != null && !item.trim().isEmpty()) {
            list.add(item);
        }
    }
}
