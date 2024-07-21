package player;

import java.util.Arrays;
import java.util.Scanner;

public class Player {
    private final static String SELECTION_OUT_OF_BOUNDS = "Input option number from list.";
    private final static String MAIN_MENU ="Chose action:";
    private final static String EXIT_MESSAGE = "Admin logged out successfully.";

    public void menu() {
        boolean exit = false;
        while (!exit) {
            showMenu();
            try {
                switch (PlayerMenuOptions.values()[getUserSelection(PlayerMenuOptions.values().length)]) {
                    case GAMES_INFO:
                        //printAllGames();
                        break;
                    case JOIN_GAME:
                        //JoinGame();
                        break;
                    case EXIT:
                        System.out.println(EXIT_MESSAGE);
                        exit = true;
                        break;
                }
            } catch (RuntimeException e){
                System.out.println(e.getMessage());
            }
        }
    }

    private void showMenu(){
        /*Enum of menu options*/
        System.out.println();
        System.out.println(MAIN_MENU);
        Arrays.stream(PlayerMenuOptions.values()).forEach(c->System.out.println((c.ordinal()+1)+") "+ c));
    }

    private int getUserSelection(int numOptions){
        Scanner scanner = new Scanner(System.in);
        int userInput;
        int userSelection = 0;

        while (userSelection==0) {
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
}
