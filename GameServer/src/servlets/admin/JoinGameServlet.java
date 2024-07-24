package servlets.admin;

import com.google.gson.Gson;
import engine.GameData;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import managers.AdminSessionManager;
import engine.GameManager;
import managers.Utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/*View active game as an observer only.*/
@WebServlet(name = "Join Active Game", urlPatterns = "/admin/joinGame")
public class JoinGameServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(AdminSessionManager.ADMIN_SESSION_KEY))) {
            response.setContentType("text/plain;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Only one admin session allowed at a time.");
            return;
        }

        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            GameManager gameManager = Utils.getServerManager(getServletContext());
            List<GameData> gamesList = gameManager.getActiveGamesList();
            Gson gson = new Gson();
            String json = gson.toJson(gamesList);
            out.println(json);
            out.flush();
        }
    }
}
