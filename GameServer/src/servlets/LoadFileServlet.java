package servlets;

import exception.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import managers.ServerManager;
import managers.Utils;
import java.nio.charset.StandardCharsets;

import java.io.IOException;


@WebServlet(name = "Load File", urlPatterns = "/admin/loadFile")
public class LoadFileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.getWriter().println("Works!");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServerManager serverManager = Utils.getServerManager(getServletContext());

        String encodedPath =request.getParameter("xmlPath");
        String XMLPath = URLDecoder.decode(encodedPath, String.valueOf(StandardCharsets.UTF_8));

        if (XMLPath == null || XMLPath.isEmpty()) {
            response.getWriter().println("No file selected");
        }
        else{
            try {
                serverManager.AddGameData(XMLPath);
            } catch (FileNotXML e) {
                response.getWriter().println("Not .xml file.");
            } catch (FileNotFound e){
                 response.getWriter().println("File not found.");
            } catch (FileInvalid e){
                 response.getWriter().println("File is wrong format for game.");
            } catch (EmptyTeamName e){
                 response.getWriter().println("All teams must have a name.");
            }catch (NotEnoughWordsException e){
                 response.getWriter().println("Game can't start with " +e.getNumCards()+" cards since there are "+e.getNumWords()+" words in file.");
            }catch (NotEnoughCardsException e){
                 response.getWriter().println("Can't hand out "+e.getSumCardsOfTeams()+" cards to playing teams- only "
                        +e.getNumCardsinGame()+" words in file.");
            }catch (GameLayoutException e){
                 response.getWriter().println("Board ("+e.getRows()+" x "+e.getColumns()+")"+" not large enough to contain "+e.getNumCards()+" cards");
            }catch (NotUniqueTeamNames e){
                 response.getWriter().println("Team names must be unique, change duplicate names: "+e.getRepeatingName());
            }catch (ZeroCards e){
                 response.getWriter().println("Number of cards in game and per team must be positive number.");
            }
        }
    }

}

/*Load game file. Adds game to system.*/
/*Get a review of current game and status of games in system.*/
/*View active game as an observer only.*/