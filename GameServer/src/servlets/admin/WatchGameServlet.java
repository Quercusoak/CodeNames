package servlets.admin;

import com.google.gson.Gson;
import dto.DTOActiveGame;
import engine.GameData;
import engine.GameManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import managers.AdminSessionManager;
import managers.Utils;

import java.io.IOException;
import java.io.PrintWriter;

/*View active game as an observer only.*/
@WebServlet(name = "Watch Active Game", urlPatterns = "/admin/watchGame")
public class WatchGameServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(AdminSessionManager.ADMIN_SESSION_KEY))) {
            response.setContentType("text/plain;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Only one admin session allowed at a time.");
            return;
        }

        GameManager gameManager = Utils.getServerManager(getServletContext());
        String gameName = request.getParameter("gameName");

        try{
            response.setContentType("application/json");
            DTOActiveGame game = gameManager.getActiveGameStatus(gameName);

            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                String json = gson.toJson(game);
                out.println(json);
                out.flush();
            }
        }catch (RuntimeException e){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().println(e.getMessage());
        }
    }
}
