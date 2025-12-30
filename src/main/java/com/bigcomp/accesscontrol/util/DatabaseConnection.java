package com.bigcomp.accesscontrol.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database connection utility class.
 * Manages database connections using singleton pattern.
 * Each call to getConnection() returns a new Connection instance to avoid sharing issues.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class DatabaseConnection {
    /** Singleton instance */
    private static DatabaseConnection instance;
    
    /** Database connection URL */
    private String url;
    
    /** Database username */
    private String username;
    
    /** Database password */
    private String password;

    /**
     * Private constructor to enforce singleton pattern
     */
    private DatabaseConnection() {
        loadDatabaseConfig();
    }

    /**
     * Gets the singleton instance of DatabaseConnection
     * @return the DatabaseConnection instance
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private void loadDatabaseConfig() {
        try {
            Properties props = new Properties();
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("config/database.properties");
            
            if (inputStream == null) {
                // Use default configuration
                url = "jdbc:mysql://localhost:3306/bigcomp_access?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
                username = "root";
                password = "";  // Default password is empty
            } else {
                props.load(inputStream);
                url = props.getProperty("db.url", 
                    "jdbc:mysql://localhost:3306/bigcomp_access?useSSL=false&serverTimezone=UTC&characterEncoding=utf8");
                username = props.getProperty("db.username", "root");
                password = props.getProperty("db.password", "");  // Default password is empty
                inputStream.close();
            }
        } catch (Exception e) {
            System.err.println("Failed to load database configuration, using default: " + e.getMessage());
            url = "jdbc:mysql://localhost:3306/bigcomp_access?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
            username = "root";
            password = "";  // Default password is empty
        }
    }

    /**
     * Gets a new database connection.
     * Each call returns a new Connection instance to avoid connection sharing issues.
     * @return a new Connection object
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Return a new connection each time to avoid sharing connection issues
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
}

