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
     * Insert a Book into Database
     * Prepares sql statement for DatabaseManager executeTransaction to insert Book
     * @param connection Database Connection is passed to BookDao by DatabaseManager
     * @param book Book
     * @throws SQLException when transaction fails
     */
    public void insert(Connection connection, Book book) throws SQLException {
        Long bookId = book.getId();
        String title = book.getTitle();
        String author = book.getAuthors().toString();
        String genres = book.getGenres().toString();

        String sql = "INSERT INTO books (id, title, authors, genres) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, bookId);
            ps.setString(2, title);
            ps.setString(3, author);
            ps.setString(4, genres);
        }
    }


}
