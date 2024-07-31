package servlets.player;

import com.google.gson.Gson;
import dto.DTOActiveGame;
import engine.GameManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managers.SessionManger;
import managers.Utils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/status")
public class GameStatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        GameManager gameManager = Utils.getServerManager(getServletContext());
        String username = SessionManger.getUsername(request);

        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            response.setContentType("application/json");

            try{
                DTOActiveGame activeGame = gameManager.getActiveGame(username);
                try (PrintWriter out = response.getWriter()) {
                    Gson gson = new Gson();
                    String json = gson.toJson(activeGame);
                    out.println(json);
                    out.flush();
                }
            }catch (RuntimeException e){
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().println(e.getMessage());
            }
        }
    }
}
