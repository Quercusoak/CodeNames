package player;

import dto.*;
import okhttp3.*;
import util.ClientUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import static player.Constants.*;
import static util.ClientUtils.printBoard;

public class GamePlay {

    private final static String DEFINERS_TURN = "Team's definer, input word group definition:";
    private final static String GET_DEFINER_CARDS_NUM = "Input overall number of cards that fit the definition: ";
    private final static Integer QUIT_TURN = 0;
    private final static String GET_PLAYERS_CARD_GUESS = "Input card number, or press "+QUIT_TURN+" to end turn: ";
    private final static String MAIN_MENU = "\nChoose action:";
    private static String SELECTION_OUT_OF_BOUNDS(int maxCardsNum) {return "Number out of board's bound. Select number between 1 and "+maxCardsNum;}
    private final static String EXIT_MESSAGE = "Thanks for playing! Goodbye!";

    private final GameEndListener listener;
    private final DTOActiveGame gameData;
    private final DTOTeam team;
    private final Role role;
    private final OkHttpClient HTTP_CLIENT;

    public GamePlay(GameEndListener listener, DTOActiveGame game, DTOTeam team, Role role, OkHttpClient HTTP_CLIENT){
        this.listener = listener;
        this.gameData = game;
        this.team = team;
        this.role = role;
        this.HTTP_CLIENT = HTTP_CLIENT;
        gamePlay();
    }

    private void gamePlay(){
        boolean gameEnded = false;
        while (!gameEnded) {
            System.out.println(MAIN_MENU);
            Arrays.stream(GameMenuOptions.values()).forEach(c->System.out.println((c.ordinal()+1)+") "+ c));
            try {
                switch (GameMenuOptions.values()[ClientUtils.getUserSelection(GameMenuOptions.values().length,false)]) {
                    case PLAY_TURN:
                        try {
                            playTurn();
                        }catch (IOException e){
                            throw new RuntimeException(e.getMessage());
                        }
                        //gameEnded = isGameEnded();
                        break;
                    case GAME_STATUS:
                        displayGameStatus();
                        break;
                }
            } catch (RuntimeException e){
                System.out.println(e.getMessage());
            }
        }
        listener.onGameEnd();
    }

    private void playTurn() throws IOException {
        // if not teams turn, if not roles turn, if somebody else acted first
        switch (role) {
            case DEFINER:
                playTurnDefiner();
                break;
            case GUESSER:
                playTurnGuesser();
                break;
        }
    }

    private void playTurnDefiner() throws IOException {

        System.out.println("Input word group definition:");
        String definition = scanner.nextLine();
        Integer numCargdsToGuess = getNumCardsToGuess(gameData.getCardList().size(),GET_DEFINER_CARDS_NUM,true);

        RequestBody body = new FormBody.Builder()
                .add("definition", definition)
                .add("numCargdsToGuess", String.valueOf(numCargdsToGuess))
                .build();

        Request request = new Request.Builder()
                .url(PLAY_TURN)
                .post(body)
                .build();

        executeRequest(request);
    }

    private Integer getNumCardsToGuess(int maxCardsNum, String msg,boolean isDefiner){
        Integer numCargdsToGuess = 0;//I have to initialize but logically its meaningless
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

    private void playTurnGuesser() throws IOException {
        int numGuesses = 0;
        boolean stopPlaying = false;
        Integer cardGuess;


//        TurnInfo turnInfo = getTurnInfo();
        ActiveGameStatus activeGameStatus = getActiveGameStatus();

        System.out.println("\nTeam " + team.getName() + " start guessing word in definition: " /*+ turnInfo.getDefinitionToGuess()*/);

        printBoard(gameData.getCardList(), gameData.getRows(), gameData.getColumns(), false);
        cardGuess = getNumCardsToGuess(gameData.getCardList().size(), GET_PLAYERS_CARD_GUESS, false);

        RequestBody body = new FormBody.Builder()
                .add("cardNum", String.valueOf(cardGuess))
                .build();

        Request request = new Request.Builder()
                .url(PLAY_TURN)
                .post(body)
                .build();

        String jsonData = executeRequest(request);
        TurnStatus guessOutcome = GSON_INSTANCE.fromJson(jsonData, TurnStatus.class);


    }

//    private TurnInfo getTurnInfo() throws IOException {
//        Request request = new Request.Builder()
//                .url(T)
//                .post(body)
//                .build();
//
//        String jsonData = executeRequest(request);
//        TurnStatus guessOutcome = GSON_INSTANCE.fromJson(jsonData, TurnStatus.class);
//
//        return new TurnInfo("",team,1);
//    }

    private void displayGameStatus() throws IOException {

        ActiveGameStatus game = getActiveGameStatus();

        System.out.println("Game Status: " + game.getGameStatus());
        printBoard(game.getBoard().getCards(),game.getBoard().getRows(),game.getBoard().getColumns(),role.equals(Role.DEFINER));
        /*game.getDtoTeams().forEach(t->{
            printTeamScore(t);
            System.out.println("Number of turns played: " + t.getNumTurnsPlayed());
        });*/
        System.out.println("Next turn: " + game.getCurrentTeam().getName());
    }

    private ActiveGameStatus getActiveGameStatus() throws IOException {
        Request request = new Request.Builder()
                .url(GAME_STATUS)
                .get()
                .build();

        String jsonData = executeRequest(request);

        return GSON_INSTANCE.fromJson(jsonData, ActiveGameStatus.class);
    }

    private String executeRequest(Request request) throws IOException {
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(response.body().string());
            }
            else {
                return response.body().string();
            }
        }
    }

/*
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static void startLongPolling(String team) {
        Runnable pollTask = new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url("http://localhost:8080/checkTurn?team=" + team)
                        .get()
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            if ("Your turn".equals(responseBody)) {
                                System.out.println("It's your team's turn!");

                                // Simulate card selection
                                String selectedCard = selectCard();

                                // Send selected card to the server
                                RequestBody requestBody = new FormBody.Builder()
                                        .add("card", selectedCard)
                                        .add("team", team)
                                        .build();

                                Request selectCardRequest = new Request.Builder()
                                        .url("http://localhost:8080/selectCard")
                                        .post(requestBody)
                                        .build();

                                client.newCall(selectCardRequest).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        System.out.println(response.body().string());
                                    }
                                });

                                // Stop polling after the turn
                                scheduler.shutdown();
                            } else if ("Game ended".equals(responseBody)) {
                                System.out.println("The game has ended.");
                                // Handle game end
                                scheduler.shutdown();
                            } else {
                                // Continue polling
                                scheduler.schedule(this, 1, TimeUnit.SECONDS);
                            }
                        } else {
                            // Continue polling in case of failure
                            scheduler.schedule(this, 1, TimeUnit.SECONDS);
                        }
                    }
                });
            }
        };

        scheduler.schedule(pollTask, 0, TimeUnit.SECONDS);
    }

    private boolean isGameEnded(){

    }*/
}
