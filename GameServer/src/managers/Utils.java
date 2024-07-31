package managers;

import engine.GameManager;
import jakarta.servlet.ServletContext;

public class Utils {

    private static final String SERVER_MANAGER_ATTRIBUTE_NAME = "serverManager";

    private static final Object serverManagerLock = new Object();

    public static GameManager getServerManager(ServletContext servletContext) {
        synchronized (serverManagerLock) {
            if (servletContext.getAttribute(SERVER_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(SERVER_MANAGER_ATTRIBUTE_NAME, new GameManager());
            }
        }
        return (GameManager) servletContext.getAttribute(SERVER_MANAGER_ATTRIBUTE_NAME);
    }
}
