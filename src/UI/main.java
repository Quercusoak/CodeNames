package UI;

import Exceptions.CardSelectionOutOfBound;
import Exceptions.TurnQuit;
import ODT.CurrentTeam;
import ODT.GameBoard;
import engine.GameCard;
import engine.GameLogic;
import ODT.FileParams;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.stream.Stream;

public class main {
    private final static String SUCCESS_MESSAGE = "File loaded successfully.";
    private final static String GET_DEFINER_CARDS_NUM = "Input overall number of cards that fit the definition: ";
    private final static String GET_PLAYERS_CARD_GUESS = "Input card number, or press q to end turn: ";
    private final static String QUIT_TURN = "q";

    static GameLogic game = new GameLogic();

    public static void main(String[] args) {
        readParamsFile();
        showGameParameters();
        startGame();
        playTurn();
    }

    private static void showMenu(){
        /*Enum of menu options*/

        //After showing menu: try to execute selected method, catch NoFileLoadedException
    }

    public static void readParamsFile(){
        System.out.print("Enter full path of XML file: ");
        //Scanner scanner = new Scanner(System.in);
        //String path = scanner.nextLine();
        try {
            game.readGameFile("src/test/classic.xml");
            System.out.println(SUCCESS_MESSAGE);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void showGameParameters(){
        FileParams odt = game.displayGameParameters();
        System.out.println("Number of possible words: " +odt.getGameWordsPossible());
        System.out.println("Number of possible black words: " +odt.getBlackWordsPossible());
        System.out.println("Number of words in game: " +odt.getNumWords());
        odt.getTeams().forEach((team,cards) -> System.out.println(team + ", number of cards: "+ cards));
    }

    public static void startGame(){
        game.startGame();
    }


    public static void playTurn(){
        boolean isDefiner = true;
        Scanner scanner = new Scanner(System.in);

        GameBoard board = game.getGameBoard();

        /*Print currently playing team's name and score:*/
        printTeamScore();

        /*Definer's turn:*/
        printBoard(board, isDefiner);
        System.out.print("Input definition: ");
        String definition = scanner.nextLine();
        Integer numCargdsToGuess = getNumCardsToGuess(board.getCards().size(),GET_DEFINER_CARDS_NUM,isDefiner);

        /*Team's turn:*/
        // To quit: press q to end turn
        int numGuesses = 0;
        while (numGuesses<numCargdsToGuess) {
            printBoard(board, !isDefiner);
            try {
                Integer cardGuess = getNumCardsToGuess(board.getCards().size(), GET_PLAYERS_CARD_GUESS, !isDefiner);
                game.playTurn(cardGuess -1); //-1 for board indexes
            }catch (TurnQuit e){
                System.out.println(e.getMessage());
                printTeamScore();
                break;
            }catch (RuntimeException e){
                System.out.println(e.getMessage());
            }
            printTeamScore();
        }

        //in engine add teams score
        //increment team out of loop
    }


    /*Print currently playing team's name and score:*/
    private static void printTeamScore(){
        CurrentTeam currentTeam = game.getCurrentTeam();
        System.out.println(currentTeam.getName()+": "+currentTeam.getScore()+"\\"+currentTeam.getNumberOfCards());
    }

    /*Validation of card num input similar between definer and guessers (must be number in range of amount playing cards)
    * but guessers can quit at any moment, hence the boolean*/
    private static Integer getNumCardsToGuess(int maxCardsNum, String msg,boolean isDefiner){
        Integer numCargdsToGuess = 0;//I have to initialize but logically its meaningless
        Scanner scanner = new Scanner(System.in);
        boolean valid = false;
        String userGuess;

        do {
            try {
                System.out.print(msg);
                userGuess = scanner.nextLine();
                if (!isDefiner && userGuess.equals(QUIT_TURN)) { //team can quit on "q", then numCargdsToGuess irrelevant
                    throw new TurnQuit();
                }

                numCargdsToGuess = Integer.parseInt(userGuess,10); //tries to parse to decimal integer, throws exception
                if (numCargdsToGuess>maxCardsNum || numCargdsToGuess<1) {
                    throw new CardSelectionOutOfBound(maxCardsNum);
                }
                else {
                    valid = true;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Input numbers only.");
            }catch (CardSelectionOutOfBound e){
                System.out.println(e.getMessage());
            }
        } while (!valid);

        return numCargdsToGuess;
    }

    private static void printBoard(GameBoard b,boolean visible){
        int maxLength = b.getCards().stream()
                .map(a->a.getWord())
                .map(String::length)
                .max(Integer::compare)
                .get();
        for (int i = 0; i < b.getRows(); i++) {
            printBoardRow(b,i,(maxLength+1),visible);
        }
    }

    private static void printBoardRow(GameBoard b, int i,int maxLength,boolean visible){
        int j;
        String str;
        GameCard card;
        for (j = 0; j < b.getColumns(); j++) {
            System.out.print(String.format("%-" + (maxLength<15? 15:maxLength) + "s", b.getBoard()[i][j].getWord()));
        }
        System.out.println();
        for (j = 0; j < b.getColumns(); j++) {
            card = b.getBoard()[i][j];
            str = "["+card.getCardNumber()+"] "+(card.isFound()? "V ":"X ");
            if (visible || card.isFound()){
                str = str.concat((card.getTeam()!=null)?"("+card.getTeam().getName()+")" :(card.isBlack()? "(BLACK)":""));
            }
            System.out.print(String.format("%-" + (maxLength<15? 15:maxLength) + "s",str));
        }
        System.out.println();
    }
}
