package player;

import com.google.gson.Gson;

import java.util.Scanner;

public class Constants {
    private final static String BASE_URL = "http://localhost:8080/GameServer_war";

    public final static String GAMES_LIST = BASE_URL + "/gamesList";
    public final static String JOIN_GAME = BASE_URL + "/joinGame";
    public final static String PLAYER_LOGIN = BASE_URL + "/login";
    public final static String PLAYER_LOGOUT = BASE_URL + "/logout";

    public final static String GAME_STATUS = BASE_URL + "/status";
    public final static String PLAY_TURN = BASE_URL + "/playTurn";

    public final static String MAIN_MENU ="\nChose action:";
    public final static String EXIT_MESSAGE = "\nThanks for playing!";
    public final static String GO_BACK = " You can enter q to return to previous menu.";
    public final static String SELECT_GAME = "Enter number of game to join."+GO_BACK;
    public final static String SELECT_TEAM = "Enter number of team to join."+GO_BACK;
    public final static String SELECT_ROLE = "Enter number of role to join."+GO_BACK;
    public final static String TEAM_IS_FULL = "No available spot on the selected team. Select another team.";
    public final static String ROLE_UNAVAILABLE = "Selected role is all taken. Select another role.";

    public static final Scanner scanner = new Scanner(System.in);

    public final static Gson GSON_INSTANCE = new Gson();

}
