package servlets.player;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import engine.GameManager;
import managers.SessionManger;
import managers.Utils;

import java.io.IOException;

@WebServlet(urlPatterns = "/login")
public class PlayerLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");

        String usernameFromSession = SessionManger.getUsername(request);
        GameManager gameManager = Utils.getServerManager(getServletContext());

        if (usernameFromSession == null) {

            String usernameFromParameter = request.getParameter(SessionManger.USERNAME);

            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            }
            else {
                usernameFromParameter = usernameFromParameter.trim();

                synchronized (this) {
                    if (gameManager.isPlayerExists(usernameFromParameter)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";

                        // stands for unauthorized as there is already such user with this name
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getOutputStream().print(errorMessage);
                    }
                    else {
                        gameManager.addPlayer(usernameFromParameter);
                        //set the username in a session so it will be available on each request
                        //the true parameter means that if a session object does not exists yet
                        //create a new one
                        request.getSession(true).setAttribute(SessionManger.USERNAME, usernameFromParameter);


                        response.setStatus(HttpServletResponse.SC_OK);
                    }
                }
            }
        }
        else {
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
