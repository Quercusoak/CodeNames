package util;

import dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ClientUtils {

    private final static String SELECTION_OUT_OF_BOUNDS = "Input option number from list.";

    public static int getUserSelection(int numOptions){
        Scanner scanner = new Scanner(System.in);
        int userInput;
        int userSelection = -1;

        while (userSelection==-1) {
            try {
                userInput = Integer.parseInt(scanner.nextLine());
                if (userInput > 0 && userInput <=numOptions) {
                    userSelection = userInput-1;
                }
                else {
                    System.out.println(SELECTION_OUT_OF_BOUNDS);
                }
            }catch (RuntimeException e){
                System.out.println(SELECTION_OUT_OF_BOUNDS);
            }
        }
        return userSelection;
    }

    public static void printGameData(List<DTOGameData> gameList){
        gameList.forEach(game -> {
            System.out.println("\nGame name: " + game.getGameName());
            System.out.println("Status: " + game.getGameStatus());
            System.out.println("Board: " + game.getRows() + " x " + game.getCols());
            System.out.println("Dictionary file: " + game.getDictionaryFileName() + ", number of unique words: " + game.getNumDictionaryWords());
            System.out.println("Number of cards in game: " + game.getNumCards());
            System.out.println("Number of black cards in game: " + game.getNumBlackCards());

            game.getDtoTeams().forEach(team -> {
                System.out.println("Team: " + team.getName());
                System.out.println("\tWords to guess: " + team.getNumberOfCards());
                System.out.println("\t" + team.getNumRequiredDefiners() + " Definers");
                System.out.println("\t" + team.getNumRequiredGuessers() + " Guessers");
            });
        });
    }

    public static void printTeamList(List<DTOTeam> teamsList){
        teamsList.forEach(t -> {
            System.out.println("\n"+t.getName()+": "+t.getScore()+"\\"+t.getNumberOfCards());
            System.out.println("Number of turns played: " + t.getNumTurnsPlayed());
        });
    }

    public static void printBoard(List<DTOCard> cards, int rows, int cols){

        List<String> cardWords = cards.stream().map(DTOCard::getWord).collect(Collectors.toList());

        List<String> cardInfo = new ArrayList<>();
        cards.forEach(card->{
            String str = "["+card.getCardNumber()+"] "+(card.isFound()? "V ":"X ");
            str = str.concat((card.getTeam() != null) ? "(" + card.getTeam().getName() + ")" : (card.isBlack() ? "(BLACK)" : ""));
            cardInfo.add(str);
        });

        int maxLength = Math.max(getLongestWordLength(cardWords), getLongestWordLength(cardInfo)) +1;

        System.out.println();
        printBorder(maxLength, cols, "+");
        for (int i = 0; i < rows; i++) {
            if (i != 0) {
                printBorder(maxLength, cols, "|");
            }
            printBoardRow(cardWords, i, cols, maxLength);
            printBoardRow(cardInfo, i, cols, maxLength);
        }
        printBorder(maxLength, cols, "+");
        System.out.println();
    }

    private static void printBoardRow(List<String> b, int i,int col, int padding){
        for (int j = 0; j < col; j++) {
            System.out.print("| ");
            System.out.printf("%-" + padding + "s", ((i*col+j)<b.size())?b.get(i*col+j) : " "); //for asymetrical boards
        }
        System.out.println("|");
    }

    private static int getLongestWordLength(List<String> lst){
        return lst.stream()
                .map(String::length)
                .max(Integer::compare)
                .get();
    }

    private static void printBorder(int length, int numStrings, String str){
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        for (int i = 0; i <= length; i++) {
            sb.append("-");
        }
        for (int i = 0; i < numStrings; i++) {
            System.out.print(sb);
        }
        System.out.println(str);
    }
}
