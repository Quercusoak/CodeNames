import okhttp3.OkHttpClient;

public class Constants {

    // Server resources locations
    private final static String BASE_URL = "http://localhost:8080";
    private final static String CONTEXT_PATH = "/admin";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String ADD_FILE = FULL_SERVER_PATH + "/loadFile";
    public final static String GAMES_LIST = FULL_SERVER_PATH + "/gameslist";
    public final static String OBSERVE_GAME = FULL_SERVER_PATH + "/join";

    public final static OkHttpClient HTTP_CLIENT = new OkHttpClient();

}