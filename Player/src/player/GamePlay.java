package player;

import dto.*;
import okhttp3.*;
import util.ClientUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

import static player.Constants.*;
import static util.ClientUtils.printBoard;

public class GamePlay {

    private final static String DEFINERS_TURN = " definer, input word group definition:";
    private final static String GET_DEFINER_CARDS_NUM = "Input overall number of cards that fit the definition: ";
    private final static Integer QUIT_TURN = 0;
    private final static String GET_PLAYERS_CARD_GUESS = "Input card number, or press "+QUIT_TURN+" to end turn: ";
    private final static String MAIN_MENU = "Choose action:";
    private static String SELECTION_OUT_OF_BOUNDS(int maxCardsNum) {return "Number out of board's bound. Select number between 1 and "+maxCardsNum;}
    private final static String EXIT_MESSAGE = "Thanks for playing! Goodbye!";

    private final String playerName;
    private final DTOTeam team;
    private final Role role;
    private final OkHttpClient HTTP_CLIENT;
    private final int numCardsOnBoard;
    private boolean gameEnded = false;

    public GamePlay( int numCardsOnBoard, DTOTeam team, Role role, OkHttpClient HTTP_CLIENT, String playerName ){
        this.team = team;
        this.role = role;
        this.HTTP_CLIENT = HTTP_CLIENT;
        this.numCardsOnBoard = numCardsOnBoard;
        this.playerName = playerName;
        gamePlay();
    }

    private void gamePlay() {

        System.out.println("\nWelcome to the game!");

        while (!gameEnded) {
            System.out.println("\n"+role.toString()+" of team "+team.getName()+", "+playerName);
            System.out.println(MAIN_MENU);
            Arrays.stream(GameMenuOptions.values()).forEach(c -> System.out.println((c.ordinal() + 1) + ") " + c));
            try {
                switch (GameMenuOptions.values()[ClientUtils.getUserSelection(GameMenuOptions.values().length, false)]) {
                    case GAME_STATUS:
                        displayGameStatus();
                        break;
                    case PLAY_TURN:
                        playTurn();
                        break;
                }
            } catch (RuntimeException | IOException e) {
                System.out.println("\n"+e.getMessage());
            }
        }

        System.out.println(EXIT_MESSAGE);
    }

    private void displayGameStatus() throws IOException {

        Request request = new Request.Builder()
                .url(GAME_STATUS)
                .get()
                .build();

        String jsonData = executeRequest(request);

        DTOActiveGame game =  GSON_INSTANCE.fromJson(jsonData, DTOActiveGame.class);
        if (game.getReasonGameEnded()!=null){
            gameEnded = true;
            throw new RuntimeException(game.getReasonGameEnded());
        }

        System.out.println("\nGame Status: " + game.getGameStatus());
        ClientUtils.printTeamScore(game.getPlayingTeam());
        System.out.println("Next turn: " + game.nextTeam().getName());

        if (game.getGameStatus().equals(GameStatus.ACTIVE)) {
            ClientUtils.printBoard(game.getBoard(),role.equals(Role.DEFINER));
        }
    }

    private void playTurn() throws IOException {
        Request request = new Request.Builder()
                .url(PLAY_TURN)
                .get()
                .build();

        String jsonData = executeRequest(request);

        TurnInfo turnInfo = GSON_INSTANCE.fromJson(jsonData, TurnInfo.class); //Get turn info if game active- otherwise catches error in menu that game pending
        checkGameEnded(turnInfo);

        //Got current game board, print it:
        printBoard(turnInfo.getBoard(),role.equals(Role.DEFINER));

        switch (role) {
            case DEFINER:
                playTurnDefiner();
                break;
            case GUESSER:
                System.out.println("Team " + team.getName() + ", start guessing "+turnInfo.getNumGuesses()+" words in definition: " + turnInfo.getDefinitionToGuess());
                playTurnGuesser();
                break;
        }
    }

    private void playTurnDefiner() throws IOException {

        System.out.println("Team's "+team.getName()+DEFINERS_TURN);
        String definition = scanner.nextLine();
        int numCargdsToGuess = getNumCardsToGuess(numCardsOnBoard,GET_DEFINER_CARDS_NUM,true);

        RequestBody body = new FormBody.Builder()
                .add("definition", definition)
                .add("numCargdsToGuess", String.valueOf(numCargdsToGuess))
                .build();

        Request request = new Request.Builder()
                .url(PLAY_TURN)
                .post(body)
                .build();

        String jsonData = executeRequest(request);
        TurnInfo info = GSON_INSTANCE.fromJson(jsonData, TurnInfo.class);
        checkGameEnded(info);
        System.out.println("Team "+team.getName()+" starting to guess words in definition "+definition+".");
    }

    private void playTurnGuesser() throws IOException {

        int cardGuess = getNumCardsToGuess(numCardsOnBoard, GET_PLAYERS_CARD_GUESS, false);

        RequestBody body = new FormBody.Builder()
                .add("cardNum", String.valueOf(cardGuess))
                .build();

        Request request = new Request.Builder()
                .url(PLAY_TURN)
                .post(body)
                .build();

        String jsonData = executeRequest(request);
        TurnInfo guessOutcome = GSON_INSTANCE.fromJson(jsonData, TurnInfo.class);

        printBoard(guessOutcome.getBoard(),false);
        printTurnStauts(guessOutcome.getTurnStatus());
    }

    private void checkGameEnded(TurnInfo turnInfo) {
        if (turnInfo.getReasonGameOver() != null) {
            gameEnded = true;
            throw new RuntimeException(turnInfo.getReasonGameOver());
        }
    }

    private String executeRequest(Request request) throws IOException {
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException(response.body().string());
            } else {
                return response.body().string();
            }
        }
    }

    private int getNumCardsToGuess(int maxCardsNum, String msg,boolean isDefiner){
        int numCargdsToGuess = 0;//I have to initialize but logically its meaningless
        Scanner scanner = new Scanner(System.in);
        boolean valid = false;
        int min = isDefiner? 1 : 0; //team can quit on "0", definer must select a card

        while (!valid){
            try {
                System.out.print(msg);
                numCargdsToGuess = scanner.nextInt();

                if (numCargdsToGuess>maxCardsNum || numCargdsToGuess<min) {
                    System.out.println(SELECTION_OUT_OF_BOUNDS(maxCardsNum));
                }
                else {
                    valid = true;
                }
            } catch (InputMismatchException e) {
                scanner.nextLine(); //clear buffer
                System.out.println("Input numbers only.");
            }
        }
        return numCargdsToGuess;
    }

    private void printTurnStauts(TurnStatus g) {
        System.out.println(g.getStatus().toString());

        switch (g.getStatus()){
            case BLACK:
                System.out.println("Team "+g.getTeamWhoseCardItWas().getName()+" lost!");
                gameEnded = true;
                break;
            case OTHERTEAM:
            case CURRENTTEAM:
                System.out.println("Team "+g.getTeamWhoseCardItWas().getName()+" gets a point.");
                break;
            case VICTORYOTHERTEAM:
                System.out.println("Team "+g.getTeamWhoseCardItWas().getName()+" won!");
                break;
            case VICTORYCURRENTTEAM:
                System.out.println("Team "+g.getTeamWhoseCardItWas().getName()+", you have won!");
                gameEnded = true;
                break;
        }
    }
}
