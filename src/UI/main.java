package UI;

import ODT.CurrentTeam;
import ODT.GameBoard;
import engine.GameCard;
import engine.GameLogic;
import ODT.FileParams;
import engine.GameSession;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class main {
    private final static String SUCCESS_MESSAGE = "File loaded successfully.";
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
        boolean visibleBoard =true;
        CurrentTeam t = game.getCurrentTeam();
        System.out.println(t.getName()+": "+t.getScore()+"\\"+t.getNumberOfCards());

        /*Definer's turn:*/
        printBoard(!visibleBoard);
        System.out.print("Input definition: ");
        Scanner scanner = new Scanner(System.in);
        String definition = scanner.nextLine();
        System.out.print("Enter all card numbers in definition, then press enter: ");
        Set<Integer> definitionCards = Arrays.stream(scanner.nextLine().split(" "))
                .map(a-> {
                    try {
                        return Integer.parseInt(a);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .collect(Collectors.toSet());

        /*Team's turn:*/
        printBoard(visibleBoard);
        System.out.println("Select card number: ");

    }

    private static void printBoard(boolean visible){
        GameBoard b = game.getGameBoard();
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
