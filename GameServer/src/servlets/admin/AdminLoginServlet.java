package servlets.admin;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import managers.AdminSessionManager;
import java.io.IOException;

@WebServlet(name = "Admin login", urlPatterns = "/admin/login")
public class AdminLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/plain;charset=UTF-8");

        synchronized (AdminSessionManager.class) {
            if (AdminSessionManager.isAdminLoggedIn()) {
                response.getWriter().write("Admin already logged in.");
                return;
            }

            HttpSession session = request.getSession(true);
            session.setAttribute(AdminSessionManager.ADMIN_SESSION_KEY, true);
            AdminSessionManager.setAdminLoggedIn(true);
            response.getWriter().write("Admin login successful.");
        }
    }
}
