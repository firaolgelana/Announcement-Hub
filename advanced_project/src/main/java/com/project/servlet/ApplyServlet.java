package com.project.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import com.project.db.InitializeConnection;

@WebServlet("/apply")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class ApplyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String github = request.getParameter("github");
        String linkedin = request.getParameter("linkedin");
        Part resumePart = request.getPart("resume");
        String postId = request.getParameter("postId");
        

        if (email == null || postId == null || resumePart == null) {
            response.getWriter().print("error: Missing required fields (email, postId, or resume)");
            return;
        }

        Connection connection = null;
        try {
            connection = InitializeConnection.connection();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        try {
            Statement statement = connection.createStatement();
            statement.execute("USE announcementportal"); 
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().print("error: " + e.getMessage());
            return;
        }

        try (
            PreparedStatement userStmt = connection.prepareStatement("SELECT id FROM Users WHERE email = ?");
            PreparedStatement insertStmt = connection.prepareStatement(
                "INSERT INTO JobSeekerProfiles (full_name, email, github_link, linkedin_link, resume, user_id, post_id) VALUES (?, ?, ?, ?, ?, ?, ?)"
            )
        ) {
            connection.setAutoCommit(false); 

            userStmt.setString(1, email);
            try (ResultSet userResult = userStmt.executeQuery()) {
                if (!userResult.next()) {
                    response.getWriter().print("error: User not found");
                    return;
                }
                int userId = userResult.getInt("id");

                insertStmt.setString(1, fullName);
                insertStmt.setString(2, email);
                insertStmt.setString(3, github);
                insertStmt.setString(4, linkedin);

                InputStream resumeInputStream = resumePart.getInputStream();
                insertStmt.setBlob(5, resumeInputStream);
                insertStmt.setInt(6, userId);
                insertStmt.setInt(7, Integer.parseInt(postId));

                int rowsInserted = insertStmt.executeUpdate();
                if (rowsInserted > 0) {
                    connection.commit(); 
                    response.getWriter().print("success");
                } else {
                    connection.rollback(); 
                    response.getWriter().print("error: Application submission failed");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback(); 
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            response.getWriter().print("error: " + e.getMessage());
        }
     
    }
}
