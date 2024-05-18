package UI;

import ODT.GameBoard;
import engine.GameLogic;
import ODT.FileParams;
import engine.GameSession;

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
        FileParams odt = game.displayGameParameters();
        System.out.println("Number of possible words: " +odt.getGameWordsPossible());
        System.out.println("Number of possible black words: " +odt.getBlackWordsPossible());
        System.out.println("Number of words in game: " +odt.getNumWords());
        odt.getTeams().forEach((team,cards) -> System.out.println(team + ", number of cards: "+ cards));
    }

    public static void startGame(){
        game.startGame();
        printBoard();
    }

    private static void printBoard(){
        GameBoard b = game.getGameBoard();
        int maxLength = b.getCards().stream()
                .map(a->a.getWord())
                .map(String::length)
                .max(Integer::compare)
                .get();
        for (int i = 0; i < b.getRows(); i++) {
            printBoardRow(b,i,maxLength);
        }
    }

    private static void printBoardRow(GameBoard b, int i,int maxLength){
        int j;
        for (j = 0; j < b.getColumns(); j++) {
            System.out.print(String.format("%-" + (maxLength+1) + "s", b.getBoard()[i][j].getWord()));
        }
        System.out.println();
        for (j = 0; j < b.getColumns(); j++) {
            System.out.print(String.format("%-" + (maxLength+1) + "s", "["+b.getBoard()[i][j].getCardNumber()+"]"));
        }
        System.out.println();
    }
}
