package com.project.servlet;

import com.project.db.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
// import java.lang.Thread.State;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet("/postAnnouncement")
public class AnnouncementServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String date = request.getParameter("date");

        if (title == null || description == null || date == null) {
            response.getWriter().write("Invalid input. Please try again.");
            return;
        }

        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");

        if (userEmail == null) {
            response.getWriter().write("User not logged in. Please login.");
            return;
        }

        Connection connection = DatabaseConnection.getConnection();

        if (connection == null) {
            response.getWriter().write("Database connection failed.");
            return;
        }

        int userId = getUserIdByEmail(connection, userEmail);
        if (userId == -1) {
            response.getWriter().write("User not found. Please login.");
            return;
        }
        try{
            Date sqlDate = Date.valueOf(date);  
            Statement  statement = connection.createStatement();
            statement.execute("USE AnnouncementPortal");
            String query = "INSERT INTO announcements (title, content, institute_id, created_at) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setInt(3, userId);
            stmt.setDate(4, sqlDate);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                response.getWriter().write("success");
            } else {
                response.getWriter().write("Failed to post the announcement. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("Internal server error. Please try again.");
        }
    }

    private int getUserIdByEmail(Connection connection, String email) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("USE AnnouncementPortal");
            String query = "SELECT id FROM Users WHERE email = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; 
    }
}
