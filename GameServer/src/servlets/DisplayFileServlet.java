package servlets;

import dto.DTOGameData;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import managers.AdminSessionManager;
import managers.ServerManager;
import managers.Utils;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "Display Games", urlPatterns = "/admin/gamesList")
public class DisplayFileServlet  extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ServerManager serverManager = Utils.getServerManager(getServletContext());

        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(AdminSessionManager.ADMIN_SESSION_KEY))) {
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Only one admin session allowed at a time.");
            return;
        }

        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            List<DTOGameData> gamesList = serverManager.getGameDataList();
            Gson gson = new Gson();
            String json = gson.toJson(gamesList);
            out.println(json);
            out.flush();
        }
    }
}