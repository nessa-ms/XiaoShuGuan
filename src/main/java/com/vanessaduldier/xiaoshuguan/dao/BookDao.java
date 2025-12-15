package com.vanessaduldier.xiaoshuguan.dao;

import com.vanessaduldier.xiaoshuguan.model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Data access object Book
 * @author Vanessa Duldier
 */
public class BookDao {

    /**
     * Prepares sql statement for DatabaseManager executeTransaction
     * @param connection Database Connection is passed to BookDao by DatabaseManager
     * @param book
     * @throws SQLException
     */
    public void insert(Connection connection, Book book) throws SQLException {
        Long bookId = book.getId();
        String title = book.getTitle();
        String author = book.getAuthor().getName();
        String genres = book.getGenres().toString();

        String sql = "INSERT INTO books (id, title, author, genres) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, bookId);
            ps.setString(2, title);
            ps.setString(3, author);
            ps.setString(4, genres);
        }
    }
}
