package com.project.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import com.project.db.InitializeConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
@WebServlet("/viewAnnouncementsIn")
public class ViewAnnouncementsIn extends HttpServlet{
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       HttpSession session = request.getSession();
       String email = (String) session.getAttribute("userEmail");

       try{
           Class.forName("com.mysql.cj.jdbc.Driver");
           Connection connection = InitializeConnection.connection();

           Statement statement = connection.createStatement();
           statement.execute("USE AnnouncementPortal");
           String sqlUser = "SELECT id FROM Users WHERE email = ?";
           PreparedStatement stmtUser = connection.prepareStatement(sqlUser);
           stmtUser.setString(1, email);
           ResultSet rsUser = stmtUser.executeQuery();
           
           if(rsUser.next()){
               int userId = rsUser.getInt("id");

               String sqlAnnouncements = "SELECT id, title, content, created_at FROM Announcements WHERE institute_id = ?";
               PreparedStatement stmtAnnouncements = connection.prepareStatement(sqlAnnouncements);
               stmtAnnouncements.setInt(1, userId);
               ResultSet rsAnnouncements = stmtAnnouncements.executeQuery();
               
               StringBuilder announcements = new StringBuilder();
               announcements.append("[");
               
               while(rsAnnouncements.next()){
                   if(announcements.length() > 1) {
                       announcements.append(",");
                   }
                   announcements.append("{")
                       .append("\"id\":").append(rsAnnouncements.getInt("id")).append(",")
                       .append("\"title\":\"").append(rsAnnouncements.getString("title")).append("\",")
                       .append("\"content\":\"").append(rsAnnouncements.getString("content")).append("\",")
                       .append("\"date\":\"").append(rsAnnouncements.getDate("created_at")).append("\"")
                       .append("}");
               }
               announcements.append("]");
               
               PrintWriter out = response.getWriter();
               out.println(announcements.toString());
           }
           else{
               PrintWriter out = response.getWriter();
               out.println("[]"); // No announcements
           }

       }catch(SQLException | ClassNotFoundException e){
           e.printStackTrace();
       }
    }
}
