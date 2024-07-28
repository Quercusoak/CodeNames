import com.google.gson.Gson;
import dto.DTOActiveGame;
import dto.DTOGameData;
import dto.GameStatus;
import okhttp3.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import util.ClientUtils;

import static util.ClientUtils.*;

import java.lang.reflect.Type;

public class Admin {

    private final static String MAIN_MENU ="\nChose action:";
    private final static String EXIT_MESSAGE = "Admin logged out successfully.";
    private final Gson gson = new Gson();

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
        if (login()) {
            menu();
        }
    }

    private boolean login(){
        Request request = new Request.Builder()
                .url(Constants.ADMIN_LOGIN)
                .get()
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(response.body().string());
            }
            return true;

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void logout() throws IOException {
        System.out.println(EXIT_MESSAGE);
        Request request = new Request.Builder()
                .url(Constants.ADMIN_LOGOUT)
                .get()
                .build();

        executeRequest(request);
    }

    private void menu() {
        boolean exit = false;
        while (!exit) {
            System.out.println(MAIN_MENU);
            Arrays.stream(AdminMenuOptions.values()).forEach(c->System.out.println((c.ordinal()+1)+") "+ c));
            try {
                switch (AdminMenuOptions.values()[getUserSelection(AdminMenuOptions.values().length , false)]) {
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
            } catch (RuntimeException | IOException e){
                System.out.println(e.getMessage());
            }
        }
    }

    private void addFile() throws IOException {

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


        System.out.println(executeRequest(request));
    }

    private void displayGameDataFromFile() throws IOException {

        Request request = new Request.Builder()
                .url(Constants.GAMES_LIST)
                .get()
                .build();


        String jsonData = executeRequest(request);
        Type listType = new TypeToken<List<DTOGameData>>() {}.getType();
        List<DTOGameData> gamesList = gson.fromJson(jsonData, listType);

        if (gamesList.isEmpty()) {
            System.out.println("No games found");
        }
        else {
            printGameData(gamesList);
        }
    }

    private void observeActiveGame() throws IOException {
        Request request = new Request.Builder()
                .url(Constants.GAMES_TO_JOIN)
                .get()
                .build();

        String jsonData = executeRequest(request);
        Type listType = new TypeToken<List<DTOGameData>>() {}.getType();
        List<DTOGameData> gamesList = gson.fromJson(jsonData, listType);

        if (gamesList.isEmpty()) {
            System.out.println("No active games.");
        }
        else {
            /*Displays al active games:*/
            System.out.println("Select game to join as an observer: ");
            gamesList.forEach(game -> {
                System.out.print((gamesList.indexOf(game)+1)+") Game name: " + game.getGameName());
                long numActiveTeams = game.getDtoTeams().stream()
                        .filter(t -> t.getNumRegisteredDefiners()==t.getNumRequiredDefiners() && t.getNumRegisteredGuessers()==t.getNumRequiredGuessers())
                        .count();
                int numTeams = game.getDtoTeams().size();
                System.out.println(", active teams: "+numActiveTeams+ "/"+numTeams);
            });

            /*Get admin's selection and display the selcted game:*/
            DTOGameData game = gamesList.get(getUserSelection(gamesList.size() , false));
            joinGame(game.getGameName());
        }
    }

    private void joinGame(String gameName) throws IOException {
        String url = HttpUrl
                .parse(Constants.OBSERVE_GAME)
                .newBuilder()
                .addQueryParameter("gameName", gameName)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        dynamicGameStatus(request);
        int optionSelected;

        do {
            System.out.println(MAIN_MENU);
            System.out.println("1) View dynamic game status\n2) Return to main menu");
            optionSelected = getUserSelection(2,false) + 1;
            if (optionSelected == 1) {
                dynamicGameStatus(request);
            }
        } while(optionSelected != 2);
    }

    private void dynamicGameStatus(Request request) throws IOException {

        String jsonData = executeRequest(request);
        DTOActiveGame game = gson.fromJson(jsonData, DTOActiveGame.class);
        printBoard(game.getBoard(), true);
        game.getTeams().forEach(ClientUtils::printTeamScore);
        System.out.println("Next turn: " + game.nextTeam().getName());
    }

    private String executeRequest(Request request) throws IOException{
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(response.body().string());
            }
            else {
                return response.body().string();
            }
        }
    }
}
