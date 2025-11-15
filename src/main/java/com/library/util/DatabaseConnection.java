package com.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/online_library?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "tarik";
    private static final String PASSWORD = "tarik123";

    // Connexion de test pour les tests unitaires
    private static Connection testConnection = null;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (testConnection != null) {
            return testConnection; // retourne la connexion mock pour les tests
        }
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static void setTestConnection(Connection conn) {
        testConnection = conn; // permet d’injecter une connexion mock
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
