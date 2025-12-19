package com.vanessaduldier.xiaoshuguan.dao;

import com.vanessaduldier.xiaoshuguan.model.Author;
import com.vanessaduldier.xiaoshuguan.service.DatabaseService;

import java.sql.*;

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
     * @param author Author
     * @return existingId Long if author already in database, else null
     */
    public Long insert(Author author) {
        Long existingId = findAuthorId(author.getName());
        if (existingId != null) {
            return existingId;
        }

        return DatabaseService.executeTransactionWithResult(connection -> {
            String sql = "INSERT INTO author (name) VALUES (?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, author.getName());
                ps.executeUpdate();

                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
            return null;
        });
    }

    public void delete(Author author) {

    }

    public void update(Author author) {

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

        try (Connection connection = DatabaseService.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Author(
                            rs.getLong("id"),
                            rs.getString("name")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden des Authors", e);
        }
        return null;
    }
}

