package com.project.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/"; // No default database
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Connected to MySQL Server (no specific database).");
            } catch (SQLException e) {
                System.out.println("Connection Failed: " + e.getMessage());
            }
        }
        return connection;
    }

    public static void main(String[] args) {
        Connection connection = DatabaseConnection.getConnection();

        if (connection == null) {
            return;
        }

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS AnnouncementPortal");

            statement.executeUpdate("USE AnnouncementPortal");

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Users (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    role ENUM('INSTITUTE', 'JOB_SEEKER') NOT NULL
                )
            """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Announcements (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    title VARCHAR(100) NOT NULL,
                    content TEXT NOT NULL,
                    institute_id INT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (institute_id) REFERENCES Users(id)
                )
            """);

            statement.executeUpdate("""
                 CREATE TABLE IF NOT EXISTS JobSeekerProfiles (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        full_name VARCHAR(100) NOT NULL,
                        email VARCHAR(100) NOT NULL,
                        github_link VARCHAR(255),
                        linkedin_link VARCHAR(255),
                        resume LONGBLOB,
                        user_id INT,
                        post_id INT,
                        FOREIGN KEY (user_id) REFERENCES Users(id),
                        FOREIGN KEY (post_id) REFERENCES Users(id)
                    );


            """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS InterviewResults (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    jobseeker_id INT,
                    result VARCHAR(50),
                    comments TEXT,
                    FOREIGN KEY (jobseeker_id) REFERENCES Users(id)
                )
            """);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    
    }

}
