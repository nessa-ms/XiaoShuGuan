package com.vanessaduldier.xiaoshuguan.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Cleanup database and file storage
 * @author Vanessa Duldier
 */
public class CleanupService {

    public void cleanupOrphans(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("""
            DELETE FROM author
            WHERE id NOT IN (SELECT DISTINCT author_id FROM book_author)
        """);

            stmt.executeUpdate("""
            DELETE FROM genre
            WHERE id NOT IN (SELECT DISTINCT genre_id FROM book_genre)
        """);
        }
    }
}
