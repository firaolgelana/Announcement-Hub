package com.project.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class InitializeConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/AnnouncementPortal";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static Connection conn = null;

    public static Connection connection()throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
        }
        return conn;
    }

}
