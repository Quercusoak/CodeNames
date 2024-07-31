package servlets.player;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import engine.GameManager;
import managers.SessionManger;
import managers.Utils;

import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class PlayerLogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usernameFromSession = SessionManger.getUsername(request);
        GameManager gameManager = Utils.getServerManager(getServletContext());

        if (usernameFromSession != null) {
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write(usernameFromSession + " removed successful.");
            gameManager.removePlayer(usernameFromSession);
            SessionManger.clearSession(request);
        }
    }
}
