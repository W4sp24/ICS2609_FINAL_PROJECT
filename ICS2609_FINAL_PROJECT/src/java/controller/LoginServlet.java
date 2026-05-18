/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import dao.DAOFactory;
import dao.DerbyAuthDAO;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.equals("") || password == null || password.equals("")) {
            response.sendRedirect("login.jsp?error=1");
            return;
        }

        DAOFactory factory = (DAOFactory) getServletContext().getAttribute("DAOFactory");
        DerbyAuthDAO auth = factory.getDerbyDAO();

        String role = auth.validateLogin(username, password);

        if (role == null) {
            response.sendRedirect("login.jsp?error=1");
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("role", role.toUpperCase());
        session.setAttribute("username", username);
        // TODO: call MySqlBusinessDAO.getUserByEmail(username) here once implemented
        // and store u_id + MySQL role in session for use in course/grade/enrollment pages

        switch (role.toUpperCase()) {
            case "ADMIN":
                response.sendRedirect("AdminDashboard");
                break;
            case "GUEST":
                response.sendRedirect("studentDashboard.jsp");
                break;
            default:
                response.sendRedirect("login.jsp?error=1");
        }
    }
}
