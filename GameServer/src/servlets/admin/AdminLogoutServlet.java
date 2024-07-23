package servlets.admin;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import managers.AdminSessionManager;

import java.io.IOException;

@WebServlet(name = "Admin logout", urlPatterns = "/admin/logout")
public class AdminLogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        synchronized (AdminSessionManager.class) {
            HttpSession session = request.getSession(false);

            if (session != null) {
                session.invalidate();
                AdminSessionManager.setAdminLoggedIn(false);
            }

            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("Admin logout successful.");
        }
    }
}
