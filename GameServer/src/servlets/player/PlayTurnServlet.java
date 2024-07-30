package servlets.player;

import com.google.gson.Gson;
import dto.*;
import engine.GameManager;
import engine.Player;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managers.SessionManger;
import managers.Utils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/playTurn")
public class PlayTurnServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        GameManager gameManager = Utils.getServerManager(getServletContext());
        String username = SessionManger.getUsername(request);
        Player player = gameManager.getPlayer(username);
        String definition = "";
        int numCard = -1;
        TurnInfo status;

        response.setContentType("application/json");

        if (player.isGameOver()){
            status = new TurnInfo(player.getReasonGameOver());
        }else {

            switch (player.getRole()) {
                case DEFINER:
                    definition = request.getParameter("definition");
                    numCard = Integer.parseInt(request.getParameter("numCargdsToGuess"));
                    break;
                case GUESSER:
                    numCard = Integer.parseInt(request.getParameter("cardNum"));
                    break;
            }

            try {
                status = gameManager.singleTurn(player, numCard, definition);
            } catch (RuntimeException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println(e.getMessage());
                return;
            }
        }

        try (PrintWriter out = response.getWriter()) {
            out.println(gson.toJson(status));
            out.flush();
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        GameManager gameManager = Utils.getServerManager(getServletContext());
        String username = SessionManger.getUsername(request);

        Player player = gameManager.getPlayer(username);

        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {
            try {
                TurnInfo turnInfo = gameManager.getTurnInfo(player);
                out.println(gson.toJson(turnInfo));
            } catch (RuntimeException e) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.println(e.getMessage());
            }
            out.flush();
        }
    }
}
