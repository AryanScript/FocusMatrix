package com.focusmatrix.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection (JDBC Connectivity & Singleton Pattern - Rubric: 8 Marks)
 * Manages singleton database connectivity to MySQL focusmatrix_db.
 */
public class DBConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/focusmatrix_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Haish@989";

    private static Connection connection = null;

    // Private constructor prevents external instantiation (Singleton)
    private DBConnection() {}

    /**
     * Retrieves or initializes the active singleton JDBC connection
     */
    public static synchronized Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                // Explicitly load MySQL 8.0 JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found in classpath!", e);
        }
        return connection;
    }

    /**
     * Safely closes the database connection on server shutdown
     */
    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing DB connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
