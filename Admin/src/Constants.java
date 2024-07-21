
public class Constants {

    // Server resources locations
    private final static String BASE_URL = "http://localhost:8080";
    private final static String CONTEXT_PATH = "/admin";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String ADD_FILE = FULL_SERVER_PATH + "/addFile";
    public final static String GAMES_LIST = FULL_SERVER_PATH + "/gamesList";
    public final static String OBSERVE_GAME = FULL_SERVER_PATH + "/joinGame";
    public final static String ADMIN_LOGIN = FULL_SERVER_PATH + "/login";
    public final static String ADMIN_LOGOUT = FULL_SERVER_PATH + "/logout";
}