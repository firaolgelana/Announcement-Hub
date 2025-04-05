package com.project.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class AuthFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String uri = req.getRequestURI();

        // Allow homepage and static resources (CSS, JS, images)
        if (uri.endsWith("homePage.html") ||
        uri.endsWith("login.html") ||
        uri.endsWith("signup.html") ||
        uri.endsWith("logout.html") ||
        uri.contains("login") ||
        uri.contains("signup") ||
        uri.contains("/css/") ||
        uri.contains("/js/")) {
        chain.doFilter(request, response); // Allow access to these pages
        return;
    }

        // Check if session exists and user is logged in
        if (session != null && Boolean.TRUE.equals(session.getAttribute("isLoggedIn"))) {
            chain.doFilter(request, response); // Proceed to the requested page
        } else {
            res.sendRedirect("homePage.html"); // Redirect to homepage
        }
    }

    public void destroy() {
        // Cleanup logic if needed
    }
}
