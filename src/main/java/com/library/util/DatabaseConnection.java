package com.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    // ✅ SOLUTION : Utiliser uniquement les variables d'environnement
    // Ne JAMAIS mettre de valeurs par défaut pour les credentials en production
    private static final String URL = getEnvOrThrow("DB_URL");
    private static final String USERNAME = getEnvOrThrow("DB_USERNAME");
    private static final String PASSWORD = getEnvOrThrow("DB_PASSWORD");

    // Connection for unit tests (mock)
    private static Connection testConnection = null;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            LOGGER.info("MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    /**
     * Récupère une variable d'environnement ou lance une exception si elle n'existe pas
     * @param envVar Nom de la variable d'environnement
     * @return Valeur de la variable
     * @throws IllegalStateException si la variable n'existe pas
     */
    private static String getEnvOrThrow(String envVar) {
        String value = System.getenv(envVar);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(
                    String.format("Missing required environment variable: %s. " +
                            "Please set it before starting the application.", envVar)
            );
        }
        return value;
    }

    /**
     * Version pour développement local avec valeurs par défaut
     * À utiliser UNIQUEMENT en développement local, JAMAIS en production
     */
    private static String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        if (value == null || value.trim().isEmpty()) {
            LOGGER.warning(String.format(
                    "Environment variable %s not set, using default value. " +
                            "This should ONLY happen in development!", envVar
            ));
            return defaultValue;
        }
        return value;
    }

    public static Connection getConnection() throws SQLException {
        if (testConnection != null) {
            return testConnection; // return mock connection for testing
        }

        // Log connection info (sans le mot de passe pour la sécurité)
        LOGGER.info("Connecting to database: " + URL + " with user: " + USERNAME);

        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static void setTestConnection(Connection conn) {
        testConnection = conn; // inject mock connection for unit tests
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                LOGGER.info("Database connection closed successfully");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Failed to close database connection", e);
            }
        }
    }
}