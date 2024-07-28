package servlets.player;

import com.google.gson.Gson;
import dto.*;
import engine.GameManager;
import engine.GameSession;
import engine.Player;
import engine.Team;
import exception.CardAlreadyGuessed;
import exception.CardSelectionOutOfBound;
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

    /*   private final static Integer QUIT_TURN = 0;
       private Team currentTeam;
       private String definition;
       private int numCargdsToGuess;
       private GameSession game;
       private Role role = Role.DEFINER;*/
    private final Gson gson = new Gson();
    private final String NOT_PLAYER_TURN = "Not your turn yet.";
    private final String GAME_INACTIVE = "Can't play turn- game is pending.";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        GameManager gameManager = Utils.getServerManager(getServletContext());
        String username = SessionManger.getUsername(request);
        Player player = gameManager.getPlayer(username);
        String definition = "";
        int numCard = -1;

        response.setContentType("application/json");

        if (player.isGameOver()){
            TurnStatus status = new TurnStatus(TurnGuessStatus.NEUTRAL,null);
            status.setGameOver(player.getReasonGameOver());
            response.getWriter().write(gson.toJson(status));
        }

        switch (player.getRole()) {
            case DEFINER:
                definition = request.getParameter("definition");
                numCard = Integer.parseInt(request.getParameter("numCargdsToGuess"));
                break;
            case GUESSER:
                numCard = Integer.parseInt(request.getParameter("cardNum"));
                break;
        }

        try  {
            TurnStatus status = gameManager.singleTurn(player, numCard, definition);
            response.getWriter().write(gson.toJson(status));
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(e.getMessage());
        }

/*
        game = gameManager.getPlayerGame(username).getGameSession();
        currentTeam = gameManager.getCurrentTeam(gameManager.getPlayerGame(username));

        String errorMsg = null;

        if (game.getGameStatus().equals(GameStatus.ACTIVE)) {

            if (gameManager.isPlayerTurn(username, currentTeam.getName(), role)) {
                switch (role) {
                    case DEFINER:
                        playDefiner(request);
                        break;
                    case GUESSER:
                        try (PrintWriter out = response.getWriter()) {
                            response.setContentType("application/json");
                            TurnStatus status = playGuesser(request, gameManager);
                            String json = gson.toJson(status);
                            out.println(json);
                            out.flush();
                        } catch (CardAlreadyGuessed e) {
                            errorMsg = "The word: " + e.getWord() + " was already guessed.";
                        } catch (CardSelectionOutOfBound e) {
                            errorMsg = e.getMessage();
                        }
                        break;
                }
            } else {
                errorMsg = NOT_PLAYER_TURN;
            }
        }else{
            errorMsg = GAME_INACTIVE;
        }

        if (errorMsg != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(errorMsg);
        }*/
    }

    /*private void playDefiner(HttpServletRequest request) {
        definition = request.getParameter("definition");
        numCargdsToGuess = Integer.parseInt(request.getParameter("numCargdsToGuess"));
        role = Role.GUESSER;
    }

    private TurnStatus playGuesser(HttpServletRequest request, GameManager gameManager) {

        boolean isTeamPlaying = true;
        int cardGuess = Integer.parseInt(request.getParameter("cardNum"));
        TurnStatus guessOutcome = null;


        guessOutcome = gameManager.playTurn(cardGuess - 1, game); //-1 for board indexes


        if (guessOutcome.getStatus().equals(TurnGuessStatus.BLACK) || guessOutcome.getStatus().getVictory()) {
            gameManager.removeTeamFromGame(game, currentTeam);
            isTeamPlaying = false;
        }
        numCargdsToGuess--;


        if (numCargdsToGuess == 0 || !isTeamPlaying) {
            gameManager.turnEnd(game);
        }

        return guessOutcome;
    }*/

    /*private String printTurnStauts(TurnStatus g) {
        String msg = g.getStatus().toString() + "\n";
        switch (g.getStatus()) {
            case BLACK:
                msg = msg.concat("Team " + g.getTeam().getName() + " lost!");
                break;
            case OTHERTEAM:
            case CURRENTTEAM:
                msg = msg.concat("Team " + g.getTeam().getName() + " gets a point.");
                break;
            case VICTORYOTHERTEAM:
            case VICTORYCURRENTTEAM:
                msg = msg.concat("Team " + g.getTeam().getName() + " won!");
        }

        return msg;
    }*/

 /*   private void isGameOver(GameManager gameManager) {
        if (gameManager.isThereOneTeamLeft(game)) {
            ///ADD GAME OVER
        }
    }*/

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        GameManager gameManager = Utils.getServerManager(getServletContext());
        String username = SessionManger.getUsername(request);

        Player player = gameManager.getPlayer(username);

        if (player.isGameOver()) {
//            TurnInfo turnInfo = new TurnInfo(player.getReasonGameOver());
            DTOGameOver dtoGameOver = new DTOGameOver(player.getReasonGameOver());
        }

        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()){
            if (!player.isGameOver()) {
                TurnInfo turnInfo = gameManager.getTurnInfo(player);
                out.println(gson.toJson(turnInfo));
                DTOGameOver dtoGameOver = new DTOGameOver();
                out.println(dtoGameOver);
            }else {
                DTOGameOver dtoGameOver = new DTOGameOver(player.getReasonGameOver());
                out.println(gson.toJson(dtoGameOver));
            }
            out.flush();

        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(e.getMessage());

/*        game = gameManager.getPlayerGame(username).getGameSession();
        currentTeam = gameManager.getCurrentTeam(gameManager.getPlayerGame(username));

        if (game.getGameStatus().equals(GameStatus.PENDING)) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(GAME_INACTIVE);
        } else {
            if (gameManager.isPlayerTurn(username, currentTeam.getName(), role)) {

                DTOBoard board = gameManager.getBoard(game);
                TurnInfo turnInfo = null;
                switch (role) {
                    case DEFINER:
                        turnInfo = new TurnInfo(null, -1, board);
                        break;
                    case GUESSER:
                        turnInfo = new TurnInfo(definition, numCargdsToGuess, board);
                        break;
                }

                response.setContentType("application/json");
                response.getWriter().write(gson.toJson(turnInfo));
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write( NOT_PLAYER_TURN);
            }
        }*/
        }
    }
}
