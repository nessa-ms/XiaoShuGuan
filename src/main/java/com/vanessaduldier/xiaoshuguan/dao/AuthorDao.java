package com.vanessaduldier.xiaoshuguan.dao;

import com.vanessaduldier.xiaoshuguan.model.Author;
import com.vanessaduldier.xiaoshuguan.service.DatabaseService;

import java.sql.*;
import java.util.List;

/**
 * Data access object author
 * @author Vanessa Duldier
 */
public class AuthorDao {

    public AuthorDao() {

    }

    /**
     * Insert author into database
     * If author already in database, return id, else add author to database
     * @param connection Connection to database
     * @param author Authors
     * @return existingId Long if author already in database, else null
     */
    public Long insert(Connection connection, Author author) throws SQLException {

        String insertSql = "INSERT OR IGNORE INTO author(name) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
            ps.setString(1, author.getName());
            ps.executeUpdate();
        }

        String selectSql = "SELECT id FROM author WHERE name = ?";
        try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
            ps.setString(1, author.getName());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Long id = rs.getLong("id");
                author.setId(id);
                return id;
            }
        }

        throw new SQLException("Author ID konnte nicht ermittelt werden: " + author.getName());
    }

    /**
     * Find Author's id based on name
     * @param name Author's name
     * @return id Long Author id with corresponding name
     */
    public Long findAuthorId(String name) {
        String sql = "SELECT id FROM author WHERE name = ?";

        try (Connection connection = DatabaseService.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Suchen des Autors", e);
        }
        return null;
    }

    /**
     * Search Author based on id
     * @param id Long Authors id
     * @return Author with corresponding id
     */
    public Author findAuthor(Long id) {
        String sql = "SELECT * FROM author WHERE id = ?";
        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Author(rs.getLong("id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}

