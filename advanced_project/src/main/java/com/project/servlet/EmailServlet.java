package com.project.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/getEmail")
public class EmailServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println("{\"email\": \"" + userEmail + "\"}");
    }
}

