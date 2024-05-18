package UI;

import engine.GameLogic;
import jar.GameParams;

import java.util.concurrent.atomic.AtomicInteger;

public class main {
    private final static String SUCCESS_MESSAGE = "File loaded successfully.";
    static GameLogic game = new GameLogic();

    public static void main(String[] args) {
        readParamsFile();
        showGameParameters();
        startGame();
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
        GameParams odt = game.displayGameParameters();
        System.out.println("Number of possible words: " +odt.getGameWordsPossible());
        System.out.println("Number of possible black words: " +odt.getBlackWordsPossible());
        System.out.println("Number of words in game: " +odt.getNumWords());
        odt.getTeams().forEach((team,cards) -> System.out.println(team + ", number of cards: "+ cards));
    }

    public static void startGame(){
        game.startGame();
    }
}
