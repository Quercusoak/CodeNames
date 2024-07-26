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

public class Player implements GameEndListener{

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

    private void menu() {
        boolean exit = false;
        while (!exit) {
            System.out.println(MAIN_MENU);
            Arrays.stream(PlayerMenuOptions.values()).forEach(c->System.out.println((c.ordinal()+1)+") "+ c));
            try {
                switch (PlayerMenuOptions.values()[ClientUtils.getUserSelection(PlayerMenuOptions.values().length, false)]) {
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
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void printAllGames() throws IOException {
        Request request = new Request.Builder()
                .url(GAMES_LIST)
                .get()
                .build();

        String jsonData = executeRequest(request);
        Type listType = new TypeToken<List<DTOGameData>>() {
        }.getType();
        List<DTOGameData> gamesList = GSON_INSTANCE.fromJson(jsonData, listType);

        if (gamesList.isEmpty()) {
            System.out.println("No games found");
        } else {
            ClientUtils.printAllGames(gamesList);
        }

/*        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
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
        }*/
    }

    private void joinGame() throws IOException {
        Request request = new Request.Builder()
                .url(REGISTER_GAME)
                .get()
                .build();

        String jsonData = executeRequest(request);
        Type listType = new TypeToken<List<DTOGameData>>() {}.getType();
        List<DTOGameData> gameList = GSON_INSTANCE.fromJson(jsonData, listType);

        if (gameList.isEmpty()) {
            System.out.println("No pending games found");
        } else {
            selectGameToRegister(gameList);
        }

 /*       try (Response response = HTTP_CLIENT.newCall(request).execute()) {
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
        }*/
    }

    /*Sync teams and roles to catch first*/
    private void selectGameToRegister(List<DTOGameData> gameList) throws IOException {
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

    private void registerGame(DTOGameData game, DTOTeam team, Role role) throws IOException {

        RequestBody body = new FormBody.Builder()
                .add("gameName", game.getGameName())
                .add("teamName", team.getName())
                .add("role", role.toString())
                .build();

        Request request = new Request.Builder()
                .url(REGISTER_GAME)
                .post(body)
                .build();

        executeRequest(request);
        GamePlay activeGamePlay = new GamePlay(this, game,team,role, HTTP_CLIENT);
        //gamePlay(game.getGameName(),role);

/*        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(response.body().string());
            }

            gamePlay(game.getGameName());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }*/
    }

    /*private void gamePlay(String gameName,Role role){
        boolean gameEnded = false;
        while (!gameEnded) {
            System.out.println("\nChoose action:");
            Arrays.stream(GameMenuOptions.values()).forEach(c->System.out.println((c.ordinal()+1)+") "+ c));
            try {
                switch (GameMenuOptions.values()[ClientUtils.getUserSelection(GameMenuOptions.values().length,false)]) {
                    case PLAY_TURN:
                        playTurn(gameName,role);
                        gameEnded = isGameEnded(gameName);
                        break;
                    case GAME_STATUS:
                        //displayGameStatus();
                        break;
                }
            } catch (RuntimeException e){
                System.out.println(e.getMessage());
            }
        }
    }

    private void playTurn(String gameName, Role role) {
        // if not teams turn, if not roles turn, if somebody else acted first
        System.out.println("Input word group definition:");
        String userName = scanner.nextLine();
        switch (role) {
            case DEFINER:
                playTurnDefiner(gameName);
                break;
            case GUESSER:
                break;
        }
    }

    private void playTurnDefiner(String gameName){

        System.out.println("Input word group definition:");
        String definition = scanner.nextLine();
        Integer numCargdsToGuess = getNumCardsToGuess(engine.getGameBoard().getCards().size(),GET_DEFINER_CARDS_NUM,isDefiner);
    }

    private Integer getNumCardsToGuess(int maxCardsNum, String msg,boolean isDefiner){
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

    private boolean isGameEnded(String gameName){

    }*/

    private void exit(){

        Request request = new Request.Builder()
                .url(PLAYER_LOGOUT)
                .get()
                .build();

        try{
            System.out.println(executeRequest(request));
            System.out.println(EXIT_MESSAGE);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

        /*try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(response.body().string());
            }

            System.out.println(EXIT_MESSAGE);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }*/
    }

    @Override
    public void onGameEnd() {

    }
}
