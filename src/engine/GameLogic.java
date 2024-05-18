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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import engine.jaxb.generated.ECNGame;

public class GameLogic implements Engine {

    private GameData gameData = null;

    private final static String JAXB_XML_GAME_PACKAGE_NAME = "engine.jaxb.generated";
    private final static String REGEX_TO_EXCLUDE_FROM_DICTIONARY = "[ \\n]";

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
        String[] possibleWordsStr = ecnGame.getECNWords().getECNGameWords().split(REGEX_TO_EXCLUDE_FROM_DICTIONARY);
        List<String> allWords = Arrays.stream(possibleWordsStr)
                .filter(s -> !s.matches(REGEX_TO_EXCLUDE_FROM_DICTIONARY) && !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        int numPossibleWords = allWords.size();
        int numCards =  ecnGame.getECNBoard().getCardsCount();
        if (numPossibleWords < numCards){
            throw new NotEnoughWordsException(numPossibleWords,numCards,false);
        }

        /*Check if there are enough black words to start game with specified black card amount.*/
        List<String> allBlackWords = Arrays.stream(ecnGame.getECNWords()
                .getECNBlackWords().split(REGEX_TO_EXCLUDE_FROM_DICTIONARY))
                .filter(a->!a.matches(REGEX_TO_EXCLUDE_FROM_DICTIONARY) && !a.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        int numBlackCards = ecnGame.getECNBoard().getBlackCardsCount();
        if (allBlackWords.size() < numBlackCards){
            throw new NotEnoughWordsException(allBlackWords.size(),numBlackCards,true);
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
        gameData = new GameData();
//        gameData.setAllPossibleWords(allWords);
//        gameData.setAllPossibleBlackWords(allBlackWords);
       /* gameData.setNumAllWords(numPossibleWords);
        gameData.setNumAllBlackWords(numPossibleBlackWords);
        gameData.setCardsCount(numCards);
        gameData.setBlackCardsCount(numBlackCards);
        gameData.setRows(rows);
        gameData.setColumns(columns);*/
        gameData.addTeam(team1Name, team1NumCards);
        gameData.addTeam(team2Name, team2NumCards);
        gameData.setGameData(allWords,allBlackWords,numCards,numBlackCards,rows,columns);

    }

    public GameParams displayGameParameters(){
        if (gameData==null){
            throw new NoFileLoadedException();
        }
        GameParams odt = new GameParams(gameData.getWordsDictionary().size(),  gameData.getBlackWordsDictionary().size(),
                (gameData.getCardsCount() + gameData.getBlackCardsCount()), gameData.getTeams());
        return odt;
    }

    public void startGame(){
        GameSession game = new GameSession(gameData.getRows(), gameData.getColumns(), gameData.getTeams());

        /*Generate cards for game session*/
        generateCards(game);
        testShowWords(game);
    }

    private void generateCards(GameSession game){
        /*Generate cards for game session*/
        boolean isBlack = true;
        Random rand = new Random();
        List<String> currGameCards = rand.ints(0, (int)gameData.getWordsDictionary().size())
                .distinct()
                .limit(gameData.getCardsCount())
                .mapToObj(gameData.getWordsDictionary()::get)
                .collect(Collectors.toList());

        /*Generate black cards for game session*/
        Set<String> currGameBlackCards = rand.ints(0,gameData.getBlackWordsDictionary().size())
                .limit(gameData.getBlackCardsCount())
                .mapToObj(gameData.getBlackWordsDictionary()::get)
                .filter(p -> !currGameCards.contains(p))
                .collect(Collectors.toSet());

        currGameBlackCards.forEach(a->game.addCard(a,isBlack));

        /*Assign to teams and create cards*/
        int count=0;
        for (Team team : game.getTeams()){
            IntStream.range(count,team.getNumberOfCards() + count)
                    .mapToObj(currGameCards::get)
                    .forEach(a->game.addCard(a,team,!isBlack));

            count=count+team.getNumberOfCards();
        }

        /*Rest of words are neutral*/
        IntStream.range(count+ gameData.getBlackCardsCount(),gameData.getCardsCount())
                .mapToObj(currGameCards::get)
                .forEach(a->game.addCard(a,!isBlack));
    }

    /*FOR TEST PURPOSES!!!!*/
    private void testShowWords(GameSession game){
        /*AtomicInteger i= new AtomicInteger(1);
        currGameCards.stream().forEach(c->System.out.println((i.getAndIncrement())+") "+c));
        AtomicInteger j= new AtomicInteger(1);
        currGameBlackCards.stream().forEach(c->System.out.println((j.getAndIncrement())+") "+c));*/
        AtomicInteger i= new AtomicInteger(1);
        game.getCards().stream()
                .forEach(c->System.out.println((i.getAndIncrement())+") "+c.getWord()+" "
                        +((c.getTeam()!=null) ? c.getTeam().getName() : (c.isBlack()?"Black":"Neutral"))));
    }
}