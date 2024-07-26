package servlets.player;

import com.google.gson.Gson;
import engine.GameData;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import engine.GameManager;
import managers.SessionManger;
import managers.Utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = "/joinGame")
public class JoinGameServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        GameManager gameManager = Utils.getServerManager(getServletContext());
        String username = SessionManger.getUsername(request);

        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            response.setContentType("application/json");
            try (PrintWriter out = response.getWriter()) {
                List<GameData> gamesList = gameManager.getPendingGamesList();
                Gson gson = new Gson();
                String json = gson.toJson(gamesList);
                out.println(json);
                out.flush();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GameManager gameManager = Utils.getServerManager(getServletContext());
        String username = SessionManger.getUsername(request);

        if (username == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            String gameName = request.getParameter("gameName");
            String teamName = request.getParameter("teamName");
            String role = request.getParameter("role");

            if (gameManager.addPlayerToGame(username, gameName, teamName, role)) {
                response.setStatus(HttpServletResponse.SC_OK);
            }
            else{
                response.setContentType("text/plain;charset=UTF-8");
                response.sendError(HttpServletResponse.SC_CONFLICT,"Can't join selected game.");
            }
        }
    }
}
