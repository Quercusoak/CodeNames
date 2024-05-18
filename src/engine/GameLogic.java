package engine;

import Exceptions.*;
import ODT.CurrentTeam;
import ODT.FileParams;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ODT.GameBoard;
import engine.jaxb.generated.ECNGame;

public class GameLogic implements Engine {

    private GameData gameData = null;
    private GameSession game = null;

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
        gameData.addTeam(team1Name, team1NumCards);
        gameData.addTeam(team2Name, team2NumCards);
        gameData.setGameData(allWords,allBlackWords,numCards,numBlackCards,rows,columns);

    }

    public FileParams displayGameParameters(){
        if (gameData==null){
            throw new NoFileLoadedException();
        }
        FileParams odt = new FileParams(gameData.getWordsDictionary().size(),  gameData.getBlackWordsDictionary().size(),
                (gameData.getCardsCount() + gameData.getBlackCardsCount()), gameData.getTeams());
        return odt;
    }

    public void startGame(){
        game = new GameSession(gameData.getRows(), gameData.getColumns(), gameData.getTeams());

        /*Generate cards for game session*/
        generateCards();

        /*Put cards in board*/
        int i=0;
        for (GameCard card : game.getCards()){
            card.setCardNumber(i+1);
            game.getBoard()[i / gameData.getColumns()][i % gameData.getColumns()]=card;
            i++;
        }
    }

    private void generateCards(){
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
        IntStream.range(count,gameData.getCardsCount())
                .mapToObj(currGameCards::get)
                .forEach(a->game.addCard(a,!isBlack));
    }

    public GameBoard getGameBoard(){
        if (gameData==null){
            throw new NoFileLoadedException();
        }
        return new GameBoard(game.getBoard(), game.getCards(), gameData.getRows(), gameData.getColumns());
    }

    public CurrentTeam getCurrentTeam(){
        if (game==null){
            throw new GameInactiveException();
        }
        return new CurrentTeam(game.currTeamTurn);
    }

    @Override
    public void playTurn() {
        if (game==null){
            throw new GameInactiveException();
        }
        /*Display current playing team*/
        //game.currTeamTurn

        /**/
    }







    /*FOR TEST PURPOSES!!!!*/
    private void testShowWords(GameSession game){
        AtomicInteger i= new AtomicInteger(1);
        game.getCards().stream()
                .forEach(c->System.out.println((i.getAndIncrement())+") "+c.getWord()+" "
                        +((c.getTeam()!=null) ? c.getTeam().getName() : (c.isBlack()?"Black":"Neutral"))));
    }
    private void testPrintBoard(GameSession game){
        for (int i = 0; i < gameData.getRows(); i++) {
            testPrintRows(game,i);
        }
    }
    private void testPrintRows(GameSession game,int i){
        int maxLength = game.getCards().stream()
                .map(a->a.getWord())
                .map(String::length)
                .max(Integer::compare)
                .get();
        int j;
        for (j = 0; j < gameData.getColumns(); j++) {
            System.out.print(String.format("%-" + (maxLength+1) + "s", game.getBoard()[i][j].getWord()));
        }
        System.out.println();
        for (j = 0; j < gameData.getColumns(); j++) {
            System.out.print(String.format("%-" + (maxLength+1) + "s", "["+game.getBoard()[i][j].getCardNumber()+"]"));
        }
        System.out.println();
    }
}