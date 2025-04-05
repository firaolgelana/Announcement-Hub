package com.project.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import com.project.db.InitializeConnection;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Read form fields
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String role = request.getParameter("role");

        // Validate input fields
        if (name == null || email == null || password == null || confirmPassword == null || role == null || 
            name.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty() || 
            !password.equals(confirmPassword)) {
            out.write("{\"status\":\"error\", \"message\":\"Invalid input. Please check all fields.\"}");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = InitializeConnection.connection()) {
                String sql = "INSERT INTO Users (name, email, password, role) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, name);
                    stmt.setString(2, email);
                    stmt.setString(3, password); 
                    stmt.setString(4, role);

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        out.write("{\"status\":\"success\", \"message\":\"Signup successful.\"}");
                    } else {
                        out.write("{\"status\":\"error\", \"message\":\"Failed to register user. Please try again.\"}");
                    }
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            out.write("{\"status\":\"error\", \"message\":\"Email already exists. Please use a different email.\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            out.write("{\"status\":\"error\", \"message\":\"Database error: " + e.getMessage() + "\"}");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            out.write("{\"status\":\"error\", \"message\":\"Database driver not found.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            out.write("{\"status\":\"error\", \"message\":\"Unexpected error occurred.\"}");
        }
    }
}
