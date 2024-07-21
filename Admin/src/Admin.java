import com.google.gson.Gson;
import dto.*;
import okhttp3.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.stream.Collectors;

public class Admin {

    private final static String SELECTION_OUT_OF_BOUNDS = "Input option number from list.";
    private final static String MAIN_MENU ="Chose action:";
    private final static String EXIT_MESSAGE = "Admin logged out successfully.";

    public final static OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                private final Map<String, List<Cookie>> cookieStore = new HashMap<>();

                @Override
                public void saveFromResponse(@NotNull HttpUrl url, @NotNull List<Cookie> cookies) {
                    cookieStore.put(url.host(), cookies);
                }

                @NotNull
                @Override
                public List<Cookie> loadForRequest(@NotNull HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<>();
                }
            }).build();


    public Admin(){
        login();
    }

    public void login(){
        Request request = new Request.Builder()
                .url(Constants.ADMIN_LOGIN)
                .get()
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(response.body().string());
            }
            menu();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void logout(){
        System.out.println(EXIT_MESSAGE);
        Request request = new Request.Builder()
                .url(Constants.ADMIN_LOGOUT)
                .get()
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println(response.body().string());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void menu() {
        boolean exit = false;
        while (!exit) {
            showMenu();
            try {
                switch (AdminMenuOptions.values()[getUserSelection(AdminMenuOptions.values().length)]) {
                    case LOAD_XML:
                        addFile();
                        break;
                    case DISPLAY_XML:
                        displayGameDataFromFile();
                        break;
                    case VIEW_GAME:
                        observeActiveGame();
                        break;
                    case EXIT:
                        logout();
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
        Arrays.stream(AdminMenuOptions.values()).forEach(c->System.out.println((c.ordinal()+1)+") "+ c));
    }

    private int getUserSelection(int numOptions){
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

    private void addFile() {

        System.out.print("Enter full path of XML file: ");
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();

        String xmlPath;

        try {
            xmlPath = URLEncoder.encode(path, StandardCharsets.UTF_8.toString());
        }
        catch (UnsupportedEncodingException e){
            throw new RuntimeException();
        }

        RequestBody body = new FormBody.Builder()
                .add("xmlPath", xmlPath)
                .build();

        Request request = new Request.Builder()
                .url(Constants.ADD_FILE)
                .post(body)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException(response.body().string());
            }

            System.out.println("File added successfully.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void displayGameDataFromFile() {

        Request request = new Request.Builder()
                .url(Constants.GAMES_LIST)
                .get()
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(response.body().string());
            }

            Gson gson = new Gson();
            String jsonData = response.body().string();
            Type listType = new TypeToken<List<FileParams>>() {}.getType();
            List<FileParams> gamesList = gson.fromJson(jsonData, listType);

            if (gamesList.isEmpty()) {
                System.out.println("No games found");
            }
            else {
                gamesList.forEach(params -> {
                    System.out.println("\nGame name: " + params.getGameName());
                    System.out.println("Status: " + params.getGameStatus());
                    System.out.println("Board: " + params.getRows() + " x " + params.getCols());
                    System.out.println("Dictionary file: " + params.getDictionaryFileName() + ", number of unique words: " + params.getNumDictionaryWords());
                    System.out.println("Number of cards in game: " + params.getNumCards());
                    System.out.println("Number of black cards in game: " + params.getNumBlackCards());

                    params.getDtoTeams().forEach(team -> {
                        System.out.println("Team: " + team.getName());
                        System.out.println("\tWords to guess: " + team.getNumberOfCards());
                        System.out.println("\t" + team.getNumRequiredDefiners() + " Definers");
                        System.out.println("\t" + team.getNumRequiredGuessers() + " Guessers");
                    });
                });
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void observeActiveGame(){
        Request request = new Request.Builder()
                .url(Constants.OBSERVE_GAME)
                .get()
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(response.body().string());
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<DTOActiveGame>>() {}.getType();
            List<DTOActiveGame> gamesList = gson.fromJson(response.body().string(), listType);

            if (gamesList.isEmpty()) {
                System.out.println("No active games.");
            }
            else {
                joinGame(gamesList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void joinGame(List<DTOActiveGame> gamesList){

        /*Displays al active games:*/
        System.out.print("Select game to join as an observer: ");
        gamesList.forEach(game -> {
            System.out.print((gamesList.indexOf(game)+1)+") " + game.getGameName());
            long numActiveTeams = game.getTeams().stream()
                    .filter(t -> t.getNumRegisteredDefiners()==t.getNumRequiredDefiners() && t.getNumRegisteredGuessers()==t.getNumRequiredGuessers())
                    .count();
            int numTeams = game.getTeams().size();
            System.out.println(", active teams: "+numActiveTeams+ "/"+numTeams);
        });

        /*Get admin's selection and display the selcted game:*/
        DTOActiveGame game = gamesList.get(getUserSelection(gamesList.size()));
        dynamicGameStatus(game);
        int optionSelected;

        do {
            System.out.println("1) View dynamic game status\n2) Return to main menu");
            optionSelected = getUserSelection(2);
            if (optionSelected == 1) {
                dynamicGameStatus(game);
            }
        } while(optionSelected != 2);
    }

    private void dynamicGameStatus(DTOActiveGame game){
        printBoard(game.getBoard());
        printTeamList(game.getTeams());
    }

    private void printTeamList(List<DTOTeam> teamsList){
        teamsList.forEach(t -> {
            System.out.println("\n"+t.getName()+": "+t.getScore()+"\\"+t.getNumberOfCards());
            System.out.println("Number of turns played: " + t.getNumTurnsPlayed());
        });
    }

    private void printBoard(DTOBoard b){

        List<String> cardWords = b.getCards().stream().map(DTOCard::getWord).collect(Collectors.toList());

        List<String> cardInfo = new ArrayList<>();
        b.getCards().forEach(card->{
            String str = "["+card.getCardNumber()+"] "+(card.isFound()? "V ":"X ");
            str = str.concat((card.getTeam() != null) ? "(" + card.getTeam().getName() + ")" : (card.isBlack() ? "(BLACK)" : ""));
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

    private void printBoardRow(List<String> b, int i,int col, int padding){
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
}
