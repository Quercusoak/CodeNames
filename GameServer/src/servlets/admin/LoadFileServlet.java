package servlets.admin;

import exception.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

import jakarta.servlet.http.HttpSession;
import managers.AdminSessionManager;
import engine.GameManager;
import managers.Utils;
import java.nio.charset.StandardCharsets;

import java.io.IOException;


@WebServlet(name = "Add File", urlPatterns = "/admin/addFile")
public class LoadFileServlet extends HttpServlet {

    /*Load game file. Adds game to system.*/
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GameManager gameManager = Utils.getServerManager(getServletContext());

        String encodedPath =request.getParameter("xmlPath");
        String XMLPath = URLDecoder.decode(encodedPath, String.valueOf(StandardCharsets.UTF_8));

        response.setContentType("text/plain;charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(AdminSessionManager.ADMIN_SESSION_KEY))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Only one admin session allowed at a time.");
            return;
        }

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        if (XMLPath == null || XMLPath.isEmpty()) {
            response.getWriter().write("No file selected");
        }
        else{
            try {
                gameManager.AddGameData(XMLPath);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("File added successfully.");
            } catch (FileNotXML e) {
                response.getWriter().write("Not .xml file.");
            } catch (FileNotFound e){
                 response.getWriter().write("File not found.");
            } catch (FileInvalid e){
                 response.getWriter().write("File is wrong format for game.");
            } catch (EmptyTeamName e){
                 response.getWriter().write("All teams must have a name.");
            }catch (NotEnoughWordsException e){
                 response.getWriter().write("Game can't start with " +e.getNumCards()+" cards since there are "+e.getNumWords()+" words in file.");
            }catch (NotEnoughCardsException e){
                 response.getWriter().write("Can't hand out "+e.getSumCardsOfTeams()+" cards to playing teams- only "
                        +e.getNumCardsinGame()+" words in file.");
            }catch (GameLayoutException e){
                 response.getWriter().write("Board ("+e.getRows()+" x "+e.getColumns()+")"+" not large enough to contain "+e.getNumCards()+" cards");
            }catch (NotUniqueTeamNames e){
                 response.getWriter().write("Team names must be unique, change duplicate names: "+e.getRepeatingName());
            }catch (ZeroCards e){
                 response.getWriter().write("Number of cards in game and per team must be positive number.");
            }
        }
    }

}