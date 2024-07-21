package managers;

public class AdminSessionManager {

    public static final String ADMIN_SESSION_KEY = "isAdmin";
    private static final Object lock = new Object();
    private static boolean adminLoggedIn = false;

    public static boolean isAdminLoggedIn() {
        synchronized (lock) {
            return adminLoggedIn;
        }
    }

    public static void setAdminLoggedIn(boolean loggedIn) {
        synchronized (lock) {
            adminLoggedIn = loggedIn;
        }
    }
}

