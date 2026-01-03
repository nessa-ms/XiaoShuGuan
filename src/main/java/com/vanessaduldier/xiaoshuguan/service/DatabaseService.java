package com.vanessaduldier.xiaoshuguan.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database Connection and Transaction service.
 * @author Vanessa Duldier
 */
public class DatabaseService {
    private static final String DB_URL = "jdbc:sqlite:xiaoshuguan.db";

    static {
        initializeDatabase();
    }

    private DatabaseService() {}

    /**
     * Create Database
     * contains schema of database
     */
    public static void initializeDatabase() {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {

            // Tabellen erstellen
            String sql = """
            CREATE TABLE IF NOT EXISTS author (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE
            );

            CREATE TABLE IF NOT EXISTS books (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                author_id INTEGER,
                file_path TEXT UNIQUE NOT NULL,
                description TEXT,
                publisher TEXT,
                isbn TEXT,
                added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (author_id) REFERENCES author(id)
            );
            
            CREATE TABLE IF NOT EXISTS genre (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT UNIQUE NOT NULL
            );

            CREATE TABLE IF NOT EXISTS book_author (
                book_id INTEGER,
                author_id INTEGER,
                PRIMARY KEY (book_id, author_id),
                FOREIGN KEY (book_id) REFERENCES books(id),
                FOREIGN KEY (author_id) REFERENCES author(id)
            );
            
            CREATE TABLE IF NOT EXISTS book_genre (
                book_id INTEGER,
                genre_id INTEGER,
                PRIMARY KEY (book_id, genre_id)
            );

            CREATE INDEX IF NOT EXISTS idx_books_title ON books(title);
            CREATE INDEX IF NOT EXISTS idx_book_author_book ON book_author(book_id);
            CREATE INDEX IF NOT EXISTS idx_book_author_author ON book_author(author_id);
            """;

            stmt.executeUpdate(sql);

        } catch (SQLException ex) {
            throw new RuntimeException("Fehler beim Initialisieren der Datenbank", ex);
        }
    }

    /**
     * @return Database Connection
     * @throws SQLException problem with database
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // TRANSACTIONS
    // TODO: Javadoc und Kommentare

    /**
     *
     * @param work TransactionWork
     * @return result
     * @param <T> T
     */
    public static <T> T executeTransactionWithResult(TransactionWorkWithResult<T> work) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try {
                T result = work.execute(connection);
                connection.commit();
                return result;
            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException("Transaction failed", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed", e);
        }
    }

    /**
     * for updates
     * @param work TransactionWork
     */
    public static void executeTransaction(TransactionWork work) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try {
                work.execute(connection);
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException("Transaction failed", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed", e);
        }
    }

    @FunctionalInterface
    public interface TransactionWork {
        void execute(Connection connection) throws Exception;
    }

    @FunctionalInterface
    public interface TransactionWorkWithResult<T> {
        T execute(Connection connection) throws Exception;
    }
}
