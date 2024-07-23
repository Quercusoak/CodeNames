package servlets.player;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import engine.GameManager;
import managers.SessionManger;
import managers.Utils;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class PlayerLogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String usernameFromSession = SessionManger.getUsername(request);
        GameManager gameManager = Utils.getServerManager(getServletContext());

        if (usernameFromSession != null) {
            gameManager.removePlayer(usernameFromSession);
            SessionManger.clearSession(request);
        }
    }
}
