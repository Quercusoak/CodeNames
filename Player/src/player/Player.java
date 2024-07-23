package player;

import com.google.gson.reflect.TypeToken;
import dto.DTOGameData;
import dto.DTOTeam;
import dto.Role;
import okhttp3.*;
import util.ClientUtils;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static player.Constants.*;

public class Player {

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

    public Player(){
        login();
    }

    private void login(){

        String userName;
        boolean validName = false;
        do {
            System.out.println("Please enter name:");
            userName = scanner.nextLine();

            if (userName.isEmpty()) {
                System.out.println("Name cannot be empty.");
            }
            else if (registerUserName(userName)) {
                validName = true;
            }
        }while(!validName);

        System.out.println("Welcome " + userName);
        menu();
    }

    private boolean registerUserName(String userName){
        String finalUrl = HttpUrl
                .parse(PLAYER_LOGIN)
                .newBuilder()
                .addQueryParameter("username", userName)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .get()
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(response.body().string());
            }
            else {
               return true;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void menu() {
        boolean exit = false;
        while (!exit) {
            showMenu();
            try {
                switch (PlayerMenuOptions.values()[ClientUtils.getUserSelection(PlayerMenuOptions.values().length,false)]) {
                    case GAMES_INFO:
                        printAllGames();
                        break;
                    case JOIN_GAME:
                        joinGame();
                        break;
                    case EXIT:
                        exit();
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

    private void printAllGames(){
        Request request = new Request.Builder()
                .url(GAMES_LIST)
                .get()
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(response.body().string());
            }

            String jsonData = response.body().string();
            Type listType = new TypeToken<List<DTOGameData>>() {}.getType();
            List<DTOGameData> gamesList = GSON_INSTANCE.fromJson(jsonData, listType);

            if (gamesList.isEmpty()) {
                System.out.println("No games found");
            }
            else {
                ClientUtils.printAllGames(gamesList);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void joinGame(){
        Request request = new Request.Builder()
                .url(REGISTER_GAME)
                .get()
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(response.body().string());
            }

            String jsonData = response.body().string();
            Type listType = new TypeToken<List<DTOGameData>>() {}.getType();
            List<DTOGameData> gameList = GSON_INSTANCE.fromJson(jsonData, listType);

            if (gameList.isEmpty()) {
                System.out.println("No pending games found");
            }
            else {
                selectGameToRegister(gameList);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /*Sync teams and roles to catch first*/
    private void selectGameToRegister(List<DTOGameData> gameList){
        gameList.forEach(game -> {
            System.out.print(gameList.indexOf(game)+1 +") "+game.getGameName());
            ClientUtils.printTeamsInfo(game.getDtoTeams());
        });

        int userSelection;

        //Select game
        System.out.println(SELECT_GAME);
        userSelection = ClientUtils.getUserSelection(gameList.size(),true);

        if (userSelection == ClientUtils.USER_SELECTED_QUIT) {
            return;
        }
        DTOGameData selectedGame = gameList.get(userSelection);

        //select team
        DTOTeam selectedTeam = null;
        do {
            System.out.println(SELECT_TEAM);
            userSelection = ClientUtils.getUserSelection(selectedGame.getDtoTeams().size(), true);

            if (userSelection == ClientUtils.USER_SELECTED_QUIT) {
                return;
            }
            selectedTeam = selectedGame.getDtoTeams().get(userSelection);

            if (isTeamFull(selectedTeam)){
                System.out.println(TEAM_IS_FULL);
                selectedTeam = null;
            }
        } while (selectedTeam == null);


        //Select role
        Role roleSelected = null;
        do {
            System.out.println(SELECT_ROLE);
            for (Role role : Role.values()) {
                System.out.println(role.getNumber() + ") " + role.toString());
            }

            userSelection = ClientUtils.getUserSelection(Role.values().length, true);
            if (userSelection == ClientUtils.USER_SELECTED_QUIT) {
                return;
            }

            roleSelected = Role.values()[userSelection];
            if (!isRoleAvailable(roleSelected, selectedTeam)) {
                System.out.println(ROLE_UNAVAILABLE);
                roleSelected = null;
            }
        }while (roleSelected == null);

        registerGame(selectedGame,selectedTeam,roleSelected);
    }

    private boolean isTeamFull(DTOTeam t){
        return (t.getNumRegisteredDefiners()==t.getNumRequiredDefiners() && t.getNumRegisteredGuessers()==t.getNumRequiredGuessers());
    }

    private boolean isRoleAvailable(Role role,DTOTeam team){
        switch (role){
            case DEFINER:
                return team.getNumRegisteredDefiners()!= team.getNumRequiredDefiners();
            case GUESSER:
                return team.getNumRegisteredGuessers()!= team.getNumRequiredGuessers();
            default:
                return false;
        }
    }

    private void registerGame(DTOGameData game, DTOTeam team, Role role){

        RequestBody body = new FormBody.Builder()
                .add("gameName", game.getGameName())
                .add("teamName", team.getName())
                .add("role", role.toString())
                .build();
        
        Request request = new Request.Builder()
                .url(REGISTER_GAME)
                .post(body)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(response.body().string());
            }

            //??

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void exit(){

        Request request = new Request.Builder()
                .url(PLAYER_LOGOUT)
                .get()
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(response.body().string());
            }

            System.out.println(EXIT_MESSAGE);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
