package managers;

import jakarta.servlet.ServletContext;

public class Utils {

    private static final String SERVER_MANAGER_ATTRIBUTE_NAME = "serverManager";

    private static final Object serverManagerLock = new Object();

    public static ServerManager getServerManager(ServletContext servletContext) {
        synchronized (serverManagerLock) {
            if (servletContext.getAttribute(SERVER_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(SERVER_MANAGER_ATTRIBUTE_NAME, new ServerManager());
            }
        }
        return (ServerManager) servletContext.getAttribute(SERVER_MANAGER_ATTRIBUTE_NAME);
    }
}
