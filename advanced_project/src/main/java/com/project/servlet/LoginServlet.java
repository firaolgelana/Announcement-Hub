package com.project.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import com.project.db.InitializeConnection;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            out.write("{\"status\":\"error\", \"message\":\"Invalid input. Please check all fields.\"}");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = InitializeConnection.connection()) {
                
                String sql = "SELECT * FROM Users WHERE email = ? AND password = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, email);
                    stmt.setString(2, password); 

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            String position = rs.getString("role");
                            HttpSession session = request.getSession();
                            session.setAttribute("isLoggedIn", true);
                            session.setAttribute("userEmail", email);
                            session.setAttribute("userPosition", position);
                            out.write("{\"status\":\"success\", \"position\":\""+position+"\"}");
                        } else {
                            out.write("{\"status\":\"error\", \"message\":\"Invalid email or password.\"}");
                        }
                    }
                }
            }
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
