package com.project.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;

import com.project.db.DatabaseConnection;

@WebServlet("/getAnnouncements")
public class Announcement extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection connection = DatabaseConnection.getConnection();
        String announcements = getAnnouncements(connection);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(announcements);
    }

    public String getAnnouncements(Connection connection) {
        StringBuilder announcements = new StringBuilder();
        announcements.append("[");

        try {
            Statement statement = connection.createStatement();
            statement.execute("USE AnnouncementPortal");
            String query = "SELECT title, content, created_at, institute_id FROM Announcements";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                if (announcements.length() > 1) {
                    announcements.append(",");
                }
                announcements.append("{")
                    .append("\"id\":").append(rs.getInt("institute_id")).append(",")
                    .append("\"title\":\"").append(rs.getString("title")).append("\",")
                    .append("\"content\":\"").append(rs.getString("content")).append("\",")
                    .append("\"date\":\"").append(rs.getDate("created_at")).append("\"")
                    .append("}");
            }

            announcements.append("]");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return announcements.toString();
    }
}
