package servlets.player;

import com.google.gson.Gson;
import dto.*;
import engine.GameData;
import engine.GameManager;
import engine.GameSession;
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
import java.util.List;

@WebServlet(urlPatterns = "/playTurn")
public class PlayTurnServlet extends HttpServlet {

    private final static Integer QUIT_TURN = 0;
    private Team currentTeam;
    private String definition;
    private int numCargdsToGuess;
    private GameSession game;
    private Role role = Role.DEFINER;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        GameManager gameManager = Utils.getServerManager(getServletContext());
        String username = SessionManger.getUsername(request);

        game = gameManager.getPlayerGame(username).getActiveGame();
        currentTeam = gameManager.getCurrentTeam(gameManager.getPlayerGame(username));

        String errorMsg=null;

        if (gameManager.isPlayerTurn(username, currentTeam.getName(), role)){
            switch (role) {
                case DEFINER:
                    playDefiner(request);
                    break;
                case GUESSER:
                    try (PrintWriter out = response.getWriter()) {
                        response.setContentType("application/json");
                        TurnStatus status = playGuesser(request, gameManager);
                        Gson gson = new Gson();
                        String json = gson.toJson(status);
                        out.println(json);
                        out.flush();
                    } catch (CardAlreadyGuessed e) {
                        errorMsg = "The word: " + e.getWord() + " was already guessed.";
                    } catch (CardSelectionOutOfBound e) {
                        errorMsg =e.getMessage();
                    }
                    break;
            }
        }
        else{
            errorMsg = "Not your turn yet.";
        }

        if (errorMsg!=null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,errorMsg);
        }
    }

    private void playDefiner(HttpServletRequest request) {
        definition = request.getParameter("definition");
        numCargdsToGuess = Integer.parseInt(request.getParameter("numCargdsToGuess"));
        role = Role.GUESSER;
    }

    private TurnStatus playGuesser(HttpServletRequest request, GameManager gameManager) {

        boolean isTeamPlaying = true;
        int cardGuess = Integer.parseInt(request.getParameter("cardNum"));
        TurnStatus guessOutcome = null;


        guessOutcome = gameManager.playTurn(cardGuess - 1, game); //-1 for board indexes
        //printTeamScore(engine.getCurrentTeam());

        /*Check if game ended: by victory or black card.*/
        if (guessOutcome.getStatus().equals(TurnGuessStatus.BLACK) || guessOutcome.getStatus().getVictory()) {
            currentTeam.setTeamOUtOfGame();
            isTeamPlaying = false;
        }
        numCargdsToGuess--;
        //printTurnStauts(guessOutcome);


        if (numCargdsToGuess == 0 || !isTeamPlaying) {
            gameManager.turnEnd(game);
        }

        return guessOutcome;

       /* boolean isTeamPlaying = true;
        int cardGuess = Integer.parseInt(request.getParameter("cardNum"));
        TurnStatus guessOutcome;

        if (cardGuess == QUIT_TURN){
            //printTeamScore(engine.getCurrentTeam());
        }
        else{
            try{
                guessOutcome = gameManager.playTurn(cardGuess - 1,game); //-1 for board indexes
                //printTeamScore(engine.getCurrentTeam());

                //Check if game ended: by victory or black card.
                if (guessOutcome.getStatus().equals(TurnGuessStatus.BLACK) || guessOutcome.getStatus().getVictory()){
                    currentTeam.setTeamOUtOfGame();
                    isTeamPlaying = false;
                }
                numCargdsToGuess--;
                //printTurnStauts(guessOutcome);
            }catch (CardAlreadyGuessed e){
                System.out.println("The word: "+e.getWord()+ " was already guessed.");
            }catch (CardSelectionOutOfBound e){
                System.out.println(e.getMessage());
            }
        }

        if (numCargdsToGuess==0 && !isTeamPlaying){
            gameManager.turnEnd(game);
        }*/
    }

}
