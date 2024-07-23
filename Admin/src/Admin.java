import com.google.gson.Gson;
import dto.DTOGameData;
import okhttp3.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import util.ClientUtils;

import java.lang.reflect.Type;

public class Admin {

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

    private void login(){
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

    private void menu() {
        boolean exit = false;
        while (!exit) {
            showMenu();
            try {
                switch (AdminMenuOptions.values()[ClientUtils.getUserSelection(AdminMenuOptions.values().length , false)]) {
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
            Type listType = new TypeToken<List<DTOGameData>>() {}.getType();
            List<DTOGameData> gamesList = gson.fromJson(jsonData, listType);

            if (gamesList.isEmpty()) {
                System.out.println("No games found");
            }
            else {
                ClientUtils.printGameData(gamesList);
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
            Type listType = new TypeToken<List<DTOGameData>>() {}.getType();
            List<DTOGameData> gamesList = gson.fromJson(response.body().string(), listType);

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

    private void joinGame(List<DTOGameData> gamesList){

        /*Displays al active games:*/
        System.out.print("Select game to join as an observer: ");
        gamesList.forEach(game -> {
            System.out.print((gamesList.indexOf(game)+1)+") " + game.getGameName());
            long numActiveTeams = game.getDtoTeams().stream()
                    .filter(t -> t.getNumRegisteredDefiners()==t.getNumRequiredDefiners() && t.getNumRegisteredGuessers()==t.getNumRequiredGuessers())
                    .count();
            int numTeams = game.getDtoTeams().size();
            System.out.println(", active teams: "+numActiveTeams+ "/"+numTeams);
        });

        /*Get admin's selection and display the selcted game:*/
        DTOGameData game = gamesList.get(ClientUtils.getUserSelection(gamesList.size() , false));
        dynamicGameStatus(game);
        int optionSelected;

        do {
            System.out.println("1) View dynamic game status\n2) Return to main menu");
            optionSelected = ClientUtils.getUserSelection(2,false);
            if (optionSelected == 1) {
                dynamicGameStatus(game);
            }
        } while(optionSelected != 2);
    }

    private void dynamicGameStatus(DTOGameData game){
        ClientUtils.printBoard(game.getCards(), game.getRows(), game.getCols());
        ClientUtils.printTeamsRunningScore(game.getDtoTeams());
    }
}
