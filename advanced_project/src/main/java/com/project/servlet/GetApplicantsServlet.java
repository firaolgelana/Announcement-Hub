package com.project.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONArray;
import org.json.JSONObject;

import com.project.db.InitializeConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/getApplicantsByPostId")
public class GetApplicantsServlet extends HttpServlet {

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

        int postId;
        try {
            JSONObject jsonRequest = new JSONObject(jsonBuffer.toString());
            postId = jsonRequest.getInt("postId");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"Invalid or missing 'postId' parameter.\"}");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = InitializeConnection.connection()) {
                Statement statement = connection.createStatement();
                statement.execute("USE AnnouncementPortal");

                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT full_name, email, github_link, linkedin_link, resume FROM JobSeekerProfiles WHERE post_id = ?");
                stmt.setInt(1, postId);
                ResultSet rs = stmt.executeQuery();

                response.setContentType("application/json");
                JSONArray jsonArray = new JSONArray();

                while (rs.next()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("fullName", rs.getString("full_name"));
                    jsonObject.put("email", rs.getString("email"));
                    jsonObject.put("github", rs.getString("github_link"));
                    jsonObject.put("linkedin", rs.getString("linkedin_link"));
                    jsonObject.put("resume", rs.getString("resume"));
                    jsonArray.put(jsonObject);
                }

                out.write(jsonArray.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"An error occurred while fetching applicants: " + e.getMessage() + "\"}");
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
