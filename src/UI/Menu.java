package UI;

import DTO.*;
import Exceptions.*;
import engine.Engine;
import engine.GameLogic;

import java.util.*;
import java.util.stream.Collectors;

public class Menu {
    private final static String SUCCESS_MESSAGE = "File loaded successfully.";
    private final static String DEFINERS_TURN = "Team's definer, input word group definition:";
    private final static String GET_DEFINER_CARDS_NUM = "Input overall number of cards that fit the definition: ";
    private final static Integer QUIT_TURN = 0;
    private final static String GET_PLAYERS_CARD_GUESS = "Input card number, or press "+QUIT_TURN+" to end turn: ";
    private final static String MAIN_MENU = "Choose action:";
    private final static String SELECTION_OUT_OF_BOUNDS = "Input option number from list.";
    private final static String EXIT_MESSAGE = "Thanks for playing! Goodbye!";
    private final static String GAME_STARTED = "Game began.";

    static Engine engine = new GameLogic(); //engine is interface and game logic is implementation
    private boolean gameActiveFlag = false;
    private boolean fileLoadedFlag = false;

    public void Codenames() {
        boolean exit = false;
        while (!exit) {
            showMenu();
            try {
                switch (getUserSelection()) {
                    case LOADXML:
                        readParamsFile();
                        break;
                    case DISPLAYXML:
                        showGameParameters();
                        break;
                    case NEWGAME:
                        startGame();
                        break;
                    case PLAYTURN:
                        playTurn();
                        break;
                    case GAMESTATUS:
                        activeGameStatus();
                        break;
                    case EXIT:
                        displayExitMessage();
                        exit = true;
                        break;
                }
            } catch (RuntimeException e){
                System.out.println(e.getMessage());
            }
        }
    }

    public void showMenu(){
        /*Enum of menu options*/
        System.out.println();
        System.out.println(MAIN_MENU);
        Arrays.stream(MenuOption.values()).forEach(c->System.out.println((c.ordinal()+1)+") "+ c.toString()));
    }

    public MenuOption getUserSelection(){
        Scanner scanner = new Scanner(System.in);
        int userInput;
        MenuOption userSelection = null;
        int numOptions = MenuOption.values().length;

        while (userSelection==null) {
            try {
                userInput = Integer.parseInt(scanner.nextLine());
                if (userInput > 0 && userInput <=numOptions) {
                    userSelection = MenuOption.values()[userInput-1];
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

    public void readParamsFile(){
        System.out.print("Enter full path of XML file: ");
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        try {
            engine.readGameFile(path);
            System.out.println(SUCCESS_MESSAGE);
            fileLoadedFlag = true;
            gameActiveFlag = false;
        } catch (FileNotXML e) {
            System.out.println("Not .xml file.");
        } catch (FileNotFound e){
            System.out.println("File not found.");
        } catch (FileInvalid e){
            System.out.println("File is wrong format for game.");
        } catch (EmptyTeamName e){
            System.out.println("All teams must have a name.");
        }catch (NotEnoughWordsException e){
            System.out.println("Game can't start with " +e.getNumCards()+(e.isBlack() ? " black" :"")+
                    " cards since there are "+e.getNumWords()+(e.isBlack() ? " black" :"") +" words in file.");
        }catch (NotEnoughCardsException e){
            System.out.println("Can't hand out "+e.getSumCardsOfTeams()+" cards to playing teams- only "
                    +e.getNumCardsinGame()+" words in file.");
        }catch (GameLayoutException e){
            System.out.println("Board ("+e.getRows()+" x "+e.getColumns()+")"
                    +" not large enough to contain "+e.getNumCards()+" cards");
        }catch (NotUniqueTeamNames e){
            System.out.println("Team names must be unique, change name: "+e.getRepeatingName());
        }catch (ZeroCards e){
            System.out.println("Number of cards in game and per team must be positive number.");
        }
    }

    public void showGameParameters(){
        try {
            FileParams params = engine.displayGameParameters();
            System.out.println("Number of possible words: " + params.getGameWordsPossible());
            System.out.println("Number of possible black words: " + params.getBlackWordsPossible());
            System.out.println("Number of cards in game: " + params.getNumCards());
            System.out.println("Number of black cards in game: " + params.getNumBlackCards());
            params.getTeams().forEach((team, cards) -> System.out.println(team + ", number of cards: " + cards));
        }catch (NoFileLoadedException e){
            System.out.println(e.getMessage());
        }
    }

    public void startGame() {
        try {
            engine.startGame();
            gameActiveFlag = true;
            System.out.println(GAME_STARTED);
            printBoard(false);
        } catch (NoFileLoadedException e) {
            System.out.println(e.getMessage());
        } catch (NotEnoughDistinctBlackCards e){
            System.out.println("Cant start game with "+e.getNumBlackCardsDesired()
                    +" black cards since there are "+ e.getNumBlackCardsGenerated()
                    +" distinct black words that don't show in word dictionary.");
        }
    }

    public void playTurn(){
        if (!gameActiveFlag){
            System.out.println("Must start the game");
            return;
        }

        boolean isDefiner = true;
        Scanner scanner = new Scanner(System.in);
        DTOTeam currentTeam = engine.getCurrentTeam();

        /*Print currently playing team's name and score:*/
        printTeamScore(currentTeam);

        /*Definer's turn:*/
        printBoard(isDefiner);
        System.out.print(DEFINERS_TURN);
        String definition = scanner.nextLine();
        Integer numCargdsToGuess = getNumCardsToGuess(engine.getGameBoard().getCards().size(),GET_DEFINER_CARDS_NUM,isDefiner);

        /*Team's turn:*/
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

    private void printTurnStauts(TurnStatus g){
        System.out.println(g.getStatus().toString());
        switch (g.getStatus()){
            case BLACK:
                System.out.println("Team "+g.getTeam().getName()+" lost!");
                break;
            case OTHERTEAM:
            case CURRENTTEAM:
                System.out.println("Team "+g.getTeam().getName()+" gets a point.");
                break;
            case VICTORYOTHERTEAM:
            case VICTORYCURRENTTEAM:
                System.out.println("Team "+g.getTeam().getName()+" won!");
        }
    }

    /*Print currently playing team's name and score:*/
    private void printTeamScore(DTOTeam currentTeam){
        System.out.println("\n"+currentTeam.getName()+": "+currentTeam.getScore()+"\\"+currentTeam.getNumberOfCards());
    }

    /*Validation of card num input similar between definer and guessers (must be number in range of amount playing cards)
     * but guessers can quit at any moment, hence the boolean*/
    public Integer getNumCardsToGuess(int maxCardsNum, String msg,boolean isDefiner){
        Integer numCargdsToGuess = 0;//I have to initialize but logically its meaningless
        Scanner scanner = new Scanner(System.in);
        boolean valid = false;
        int min = isDefiner? 1 : 0; //team can quit on "0", definer must select a card

        while (!valid){
            try {
                System.out.print(msg);
                numCargdsToGuess = scanner.nextInt();

                if (numCargdsToGuess>maxCardsNum || numCargdsToGuess<min) {
                    throw new CardSelectionOutOfBound(maxCardsNum);
                }
                else {
                    valid = true;
                }
            } catch (InputMismatchException e) {
                scanner.nextLine(); //clear buffer
                System.out.println("Input numbers only.");
            }catch (CardSelectionOutOfBound e){
                System.out.println(e.getMessage());
            }
        }
        return numCargdsToGuess;
    }

    private void printBoard(boolean visible){
        DTOBoard b = engine.getGameBoard();

        List<String> cardWords = b.getCards().stream().map(DTOCard::getWord).collect(Collectors.toList());

        List<String> cardInfo = new ArrayList<>();
        b.getCards().forEach(card->{
                String str = "["+card.getCardNumber()+"] "+(card.isFound()? "V ":"X ");
                if (visible || card.isFound()) {
                    str = str.concat((card.getTeam() != null) ? "(" + card.getTeam().getName() + ")" : (card.isBlack() ? "(BLACK)" : ""));
                }
                cardInfo.add(str);
        });

        int maxLength = Math.max(getLongestWordLength(cardWords), getLongestWordLength(cardInfo)) +1;

        System.out.println();
        printBorder(maxLength, b.getColumns(), "+");
        for (int i = 0; i < b.getRows(); i++) {
            if (i != 0) {
                printBorder(maxLength, b.getColumns(), "|");
            }
            printBoardRow(cardWords, i, b.getColumns(), maxLength);
            printBoardRow(cardInfo, i, b.getColumns(), maxLength);
        }
        printBorder(maxLength, b.getColumns(), "+");
        System.out.println();
    }

    public void printBoardRow(List<String> b, int i,int col, int padding){
        for (int j = 0; j < col; j++) {
            System.out.print("| ");
            System.out.printf("%-" + padding + "s", ((i*col+j)<b.size())?b.get(i*col+j) : " "); //for asymetrical boards
        }
        System.out.println("|");
    }

    private int getLongestWordLength(List<String> lst){
        return lst.stream()
                .map(String::length)
                .max(Integer::compare)
                .get();
    }

    private void printBorder(int length, int numStrings, String str){
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

    public void activeGameStatus(){
        if (gameActiveFlag) {
            printBoard(true);
            TeamsList teams = engine.getTeams();
            for (DTOTeam t : teams.getTeamList()) {
                printTeamScore(t);
                System.out.println("Number of turns played: " + t.getNumTurnsPlayed());
            }
            System.out.println("Next turn: " + engine.getCurrentTeam().getName());
        }
        else{
            System.out.println("Must start the game");
        }
    }

    public void displayExitMessage(){
        System.out.println(EXIT_MESSAGE);
    }
}
