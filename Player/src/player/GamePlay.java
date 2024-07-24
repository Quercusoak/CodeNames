package player;

import dto.*;
import util.ClientUtils;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static player.Constants.scanner;

public class GamePlay {

    private final static String DEFINERS_TURN = "Team's definer, input word group definition:";
    private final static String GET_DEFINER_CARDS_NUM = "Input overall number of cards that fit the definition: ";
    private final static Integer QUIT_TURN = 0;
    private final static String GET_PLAYERS_CARD_GUESS = "Input card number, or press "+QUIT_TURN+" to end turn: ";
    private final static String MAIN_MENU = "\nChoose action:";
    private static String SELECTION_OUT_OF_BOUNDS(int maxCardsNum) {return "Number out of board's bound. Select number between 1 and "+maxCardsNum;}
    private final static String EXIT_MESSAGE = "Thanks for playing! Goodbye!";

    private final GameEndListener listener;
    private final DTOGameData game;
    private final DTOTeam team;
    private final Role role;

    public GamePlay(GameEndListener listener, DTOGameData game, DTOTeam team, Role role){
        this.listener = listener;
        this.game = game;
        this.team = team;
        this.role = role;
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
                        playTurn();
                        gameEnded = isGameEnded();
                        break;
                    case GAME_STATUS:
                        //displayGameStatus();
                        break;
                }
            } catch (RuntimeException e){
                System.out.println(e.getMessage());
            }
        }
        listener.onGameEnd();
    }

    private void playTurn() {
        // if not teams turn, if not roles turn, if somebody else acted first
        System.out.println("Input word group definition:");
        String userName = scanner.nextLine();
        switch (role) {
            case DEFINER:
                playTurnDefiner();
                break;
            case GUESSER:
                playTurnGuesser();
                break;
        }
    }

    private void playTurnDefiner(){

        System.out.println("Input word group definition:");
        String definition = scanner.nextLine();
        Integer numCargdsToGuess = getNumCardsToGuess(game.getCards().size(),GET_DEFINER_CARDS_NUM,true);
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

    private void playTurnGuesser(){
        int numGuesses = 0;
        boolean stopPlaying =false;
        Integer cardGuess;
        TurnStatus guessOutcome;

        System.out.println("\nTeam "+currentTeam.getName()+" start guessing word in definition: "+definition);

        while (numGuesses<numCargdsToGuess && !stopPlaying) {
            printBoard( !isDefiner);
            cardGuess = getNumCardsToGuess(engine.getGameBoard().getCards().size(), GET_PLAYERS_CARD_GUESS, !isDefiner);

            if (cardGuess.equals(QUIT_TURN)){
                System.out.println("End of turn.");
                stopPlaying = true;
                printTeamScore(engine.getCurrentTeam());
            }
            else{
                try{
                    guessOutcome = engine.playTurn(cardGuess - 1); //-1 for board indexes
                    printTeamScore(engine.getCurrentTeam());

                    /*Check if game ended: by victory or black card.*/
                    if (guessOutcome.getStatus().equals(TurnGuessStatus.BLACK) || guessOutcome.getStatus().getVictory()){
                        stopPlaying = true;
                        gameActiveFlag =false;
                    }
                    numGuesses++;
                    printTurnStauts(guessOutcome);
                }catch (CardAlreadyGuessed e){
                    System.out.println("The word: "+e.getWord()+ " was already guessed.");
                }catch (CardSelectionOutOfBound e){
                    System.out.println(e.getMessage());
                }
            }
        }
        if (gameActiveFlag) {
            System.out.println("End of turn.");
            engine.turnEnd();
        }
    }

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

    }
}
