package engine;

import Exceptions.*;
import jar.GameParams;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import engine.generated.ECNGame;

public class GameLogic implements Engine {

    private GameData gameData = null;

    private final static String JAXB_XML_GAME_PACKAGE_NAME = "engine.generated";

    @Override
    public void readGameFile(String XMLpath) {
        String extension = XMLpath.substring(XMLpath.lastIndexOf(".") , XMLpath.length());
        if (extension.equals(".xml")) {
            jaxbSchema(XMLpath);
        }
        else{
            throw new RuntimeException("Not .xml file.");
        }
    }


    private void jaxbSchema(String XMLpath){
        try {
            InputStream inputStream = new FileInputStream(XMLpath);
            JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            ECNGame ecnGame = (ECNGame) jaxbUnmarshaller.unmarshal(inputStream);
            loadFileGameData(ecnGame);
        } catch (JAXBException e) {
            throw new RuntimeException("File missing required fields.");
        } catch (FileNotFoundException e){
            throw new RuntimeException("File not found.");
        } catch (NotEnoughWordsException e){ //?????????????????????????????????????????????????????????????????
            throw new RuntimeException(e.getMessage());
        }
    }

    private void loadFileGameData(ECNGame ecnGame){

        /*Check if there are enough words to start game with specified card amount.*/
        String[] possibleWordsStr = ecnGame.getECNWords().getECNGameWords().split(" ");
        Set<String> allCards = Arrays.stream(possibleWordsStr).collect(Collectors.toSet());
        long numPossibleWords = allCards.stream().count();
        int numCards =  ecnGame.getECNBoard().getCardsCount();
        if (numPossibleWords < numCards){
            throw new NotEnoughWordsException(numPossibleWords,numCards,false);
        }

        /*Check if there are enough black words to start game with specified black card amount.*/
        Set<String> allBlackCards = Arrays.stream(ecnGame.getECNWords().getECNBlackWords().split(" ")).collect(Collectors.toSet());
        long numPossibleBlackWords =  allBlackCards.stream().count();
        int numBlackCards = ecnGame.getECNBoard().getBlackCardsCount();
        if (numPossibleBlackWords < numBlackCards){
            throw new NotEnoughWordsException(numPossibleBlackWords,numBlackCards,true);
        }

        /*Verify sum cards of teams isn't larger than amount cards in game.*/
        //How to make for more than 2 teams?
        int team1NumCards = ecnGame.getECNTeam1().getCardsCount();
        int team2NumCards = ecnGame.getECNTeam2().getCardsCount();
        int numCardsinGame = numCards + numBlackCards;
        if (team1NumCards + team2NumCards > numCardsinGame){
            throw new NotEnoughCardsException(team1NumCards + team2NumCards, numCardsinGame);
        }

        /*Verify rows X columns >= overall cards on board*/
        int rows = ecnGame.getECNBoard().getECNLayout().getRows();
        int columns = ecnGame.getECNBoard().getECNLayout().getColumns();
        if ((rows*columns)<numCardsinGame){
            throw new GameLayoutException(rows,columns,numCardsinGame);
        }

        /*Verify team names are unique*/
        //How to make for more than 2 teams?
        String team1Name = ecnGame.getECNTeam1().getName();
        String team2Name = ecnGame.getECNTeam2().getName();
        if (team1Name.equals(team2Name)) {
            throw new NotUniqueTeamNames(team1Name);
        }

        /*Keep sets of all gameWords and blackGameWords, not just those in current game.*/
        gameData = new GameData(allCards,allBlackCards);
//        gameData.setAllPossibleWords(allCards);
//        gameData.setAllPossibleBlackWords(allBlackCards);
        gameData.setNumAllWords(numPossibleWords);
        gameData.setNumAllBlackWords(numPossibleBlackWords);
        gameData.setCardsCount(numCards);
        gameData.setBlackCardsCount(numBlackCards);
        gameData.setRows(rows);
        gameData.setColumns(columns);
        gameData.addTeam(team1Name, team1NumCards);
        gameData.addTeam(team2Name, team2NumCards);

        ///trying with list,need to put in set in board
        gameData.wordDictionary = Arrays.stream(possibleWordsStr).distinct().collect(Collectors.toList());
    }

    public GameParams displayGameParameters(){
        if (gameData==null){
            throw new NoFileLoadedException();
        }
        GameParams odt = new GameParams(gameData.getNumAllWords(),  gameData.getNumAllBlackWords(),
                (gameData.getCardsCount() + gameData.getBlackCardsCount()), gameData.getTeams());
        return odt;
    }

    public void startGame(){
        /*Generate cards for game session*/
        Random rand = new Random();
        Set<String> currGameCards = rand.ints(0, (int)gameData.getNumAllWords())
                .distinct()
                .limit(gameData.getCardsCount())
                .mapToObj(gameData.wordDictionary::get)
                .collect(Collectors.toSet());



        /*Assign to teams and create cards*/

        /*Rest of words are neutral*/

        /*Generate black cards for game session*/

    }
}
