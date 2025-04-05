package com.project.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONObject;

import com.project.db.InitializeConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/getPostIdByEmail")
public class GetPostIdByEmailServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        StringBuilder jsonBuffer = new StringBuilder();
        String line;

        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }

        String email;
        try {
            JSONObject jsonRequest = new JSONObject(jsonBuffer.toString());
            email = jsonRequest.getString("userEmail");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"Invalid or missing 'userEmail' parameter.\"}");
            return;
        }

        if (email == null || email.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"Email parameter is required.\"}");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = InitializeConnection.connection()) {
                Statement statement = connection.createStatement();
                statement.execute("USE AnnouncementPortal");

                PreparedStatement stmt = connection.prepareStatement("SELECT id FROM Users WHERE email = ?");
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                   int postId = rs.getInt("id");
                    response.setContentType("application/json");
                    out.write("{\"postId\": " + postId + "}");

                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.write("{\"error\": \"No posts found for this institute.\"}");
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"An error occurred while fetching the post ID: " + e.getMessage() + "\"}");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"Database driver not found.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"Unexpected error occurred.\"}");
        }
    }
}
