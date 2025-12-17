package com.vanessaduldier.xiaoshuguan.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;

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
     * Create Database based on schema.sql.
     */
    public static void initializeDatabase() {
        try (InputStream is = DatabaseService.class.getClassLoader().getResourceAsStream(
                "com/vanessaduldier/xiaoshuguan/database/schema.sql");
             Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {

            if (is == null) {
                throw new RuntimeException("schema.sql not found");
            }

            String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            stmt.execute(sql);

        } catch (SQLException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void executeTransaction(TransactionWork work) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try {
                work.execute(connection);
                connection.commit();  // only commit if the transaction succeeds
            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException("Transaction failed", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    public interface TransactionWork {
        void execute(Connection connection) throws Exception;
    }
}
