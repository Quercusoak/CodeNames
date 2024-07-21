package engine;

import engine.jaxb.generated.ECNTeam;
import exception.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dto.*;
import engine.jaxb.generated.ECNGame;

public class GameLogic implements Engine, Serializable {

    private GameData gameData = null;
    private GameSession game = null;

    private final static String JAXB_XML_GAME_PACKAGE_NAME = "engine.jaxb.generated";
    private final static String REGEX_TO_EXCLUDE_FROM_DICTIONARY = "[ \\n\\t\\r]";
    private final static String CHARS_TO_REMOVE_FROM_DICTIONARY = "[^\\p{L}]";

    @Override
    public GameData readGameFile(String XMLpath) {
        try {
            String extension = XMLpath.substring(XMLpath.lastIndexOf("."));
            if (extension.equals(".xml")) {
                return jaxbSchema(XMLpath);
            } else {
                throw new FileNotXML();
            }
        }catch (IndexOutOfBoundsException  e){
            throw new FileNotXML();
        }
    }

    private GameData jaxbSchema(String XMLpath){
        try {
            InputStream inputStream = new FileInputStream(XMLpath);
            JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            ECNGame ecnGame = (ECNGame) jaxbUnmarshaller.unmarshal(inputStream);
            return loadFileGameData(ecnGame, XMLpath);
        } catch (JAXBException e) {
            throw new FileInvalid();
        } catch (FileNotFoundException e){
            throw new FileNotFound();
        }
    }

    private GameData loadFileGameData(ECNGame ecnGame, String XMLpath){
        int numWords =  ecnGame.getECNBoard().getCardsCount();
        int numBlackWords = ecnGame.getECNBoard().getBlackCardsCount();
        int numCardsinGame = numWords + numBlackWords;

        /*Get the words from ECN-Dictionary-File*/
        String directoryPath = new File(XMLpath).getParent();
        String dictionaryFileName = directoryPath+"\\"+ ecnGame.getECNDictionaryFile();
        List<String> dictinary = getWordsFromXML(dictionaryFileName, numCardsinGame);

        /*Verify rows x columns >= overall cards on board*/
        int rows = ecnGame.getECNBoard().getECNLayout().getRows();
        int columns = ecnGame.getECNBoard().getECNLayout().getColumns();
        if ((rows*columns)<numCardsinGame){
            throw new GameLayoutException(rows,columns,numCardsinGame);
        }

        /*Get teams*/
        List<Team> teams = getTeamsFromXML(ecnGame.getECNTeams().getECNTeam(), numWords);

        /*Keep collection of all game words, not just those in current game.*/
        GameData mgameData = new GameData();
        mgameData.setGameData(dictinary, teams,numWords,numBlackWords,rows,columns, ecnGame.getName(), dictionaryFileName);

        return mgameData;
    }

    public FileParams displayGameParameters(){
        if (gameData==null){
            throw new NoFileLoadedException();
        }
        return new FileParams(gameData);
    }

    public void startGame(){
        if (gameData==null){
            throw new NoFileLoadedException();
        }
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

        List<String> dict = gameData.getDictionaryWords();
        Collections.shuffle(dict);

        //Partition dictionary into two lists - cards and black cards
        Map<Boolean, List<String>> partitioned = dict.stream()
                .distinct()
                .limit(gameData.getCardsCount()+ gameData.getBlackCardsCount())
                .collect(Collectors.partitioningBy(i -> dict.indexOf(i) < gameData.getCardsCount()));

        List<String> currGameCards = partitioned.get(true);
        List<String> currGameBlackCards = partitioned.get(false);

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

    public DTOBoard getGameBoard(){
        if (game==null){
            throw new GameInactiveException();
        }
        return new DTOBoard(game.getBoard(), gameData.getRows(), gameData.getColumns(),game.getCards().size());
    }

    public DTOTeam getCurrentTeam(){
        if (game==null){
            throw new GameInactiveException();
        }
        return new DTOTeam(game.getPlayingTeam());
    }

    @Override
    public TurnStatus playTurn(Integer cardNum) {
        if (game==null){
            throw new GameInactiveException();
        }

        if (cardNum>game.getCards().size() || cardNum<0) {
            throw new CardSelectionOutOfBound(game.getCards().size());
        }

        GameCard card = game.getBoard()[cardNum / gameData.getColumns()][cardNum % gameData.getColumns()];

        /*Check if card was found already*/
        if (card.isFound()){
            throw new CardAlreadyGuessed(card.getWord());
        }

        /*Change card status to found*/
        card.setFound();

        /*Check whose card was guessed*/
        TurnGuessStatus guessStatus;
        Team teamWhoseCardItIs = null;

        if (card.getTeam()!=null) {
            teamWhoseCardItIs = card.getTeam();
            teamWhoseCardItIs.addPoint();
            boolean isCurrTeamsCard = teamWhoseCardItIs.equals(game.getPlayingTeam());

            if (teamWhoseCardItIs.getNumberOfCards() != teamWhoseCardItIs.getScore()) {
                guessStatus = isCurrTeamsCard ? TurnGuessStatus.CURRENTTEAM : TurnGuessStatus.OTHERTEAM;
            }
            else{ //all words are found
                guessStatus = isCurrTeamsCard ? TurnGuessStatus.VICTORYCURRENTTEAM : TurnGuessStatus.VICTORYOTHERTEAM;
            }
        }
        else if (card.isBlack()){
            guessStatus = TurnGuessStatus.BLACK;
            teamWhoseCardItIs = game.getPlayingTeam();
        }
        else {
            guessStatus = TurnGuessStatus.NEUTRAL;
        }

        return new TurnStatus(guessStatus,teamWhoseCardItIs);
    }

    @Override
    public void turnEnd() {
        /*Increment teams turn count*/
        game.getPlayingTeam().incTurnCounter();
        /*Increment current team to next:*/
        game.nextTeam();
    }

    public TeamsList getTeams(){
        return new TeamsList(game.getTeams());
    }

    private List<String> getWordsFromXML(String ecnDictionaryFile, int numCardsinGame){
        /*Get the words from ECN-Dictionary-File*/
        Set<String> dictionary = new HashSet<>();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        Files.newInputStream(Paths.get(ecnDictionaryFile))))) {

            in.lines()
                    .forEach(s-> dictionary.addAll(Arrays.stream(s
                                    .replaceAll(CHARS_TO_REMOVE_FROM_DICTIONARY," ") //removes from words numbers, symbols, punctuations (it's -> it)
                                    .split(REGEX_TO_EXCLUDE_FROM_DICTIONARY))
                            .filter(a-> a.length() > 1) //exclude empty strings and single letters
                            .map(String::toLowerCase)
                            .distinct()
                            .collect(Collectors.toList())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*Check if there are enough words to start game with specified card amount.*/
        int numPossibleWords = dictionary.size();
        if (numPossibleWords == 0) {
            throw new ZeroCards();
        }
        if (numPossibleWords < numCardsinGame){
            throw new NotEnoughWordsException(numPossibleWords,numCardsinGame);
        }

        return new ArrayList<>(dictionary);
    }

    private List<Team> getTeamsFromXML(List<engine.jaxb.generated.ECNTeam> ecnTeams, int numWordsInGame){
        /*Get teams*/
        List<Team> teams = new ArrayList<>();

        /*Verify sum cards of teams isn't larger than amount cards in game.*/
        int sumTeamsCards = ecnTeams.stream().peek(t -> {
            /*verify every team has positive amount of cards.*/
            if (t.getCardsCount() <= 0){
                throw new ZeroCards();
            }
            /*Verify guessers and definers >=1*/
            if (t.getDefiners() <1 || t.getGuessers() <1){
                throw new NotEnoughTeamPlayers();
            }
        }).mapToInt(ECNTeam::getCardsCount).sum();

        if (sumTeamsCards > numWordsInGame){
            throw new NotEnoughCardsException(sumTeamsCards, numWordsInGame);
        }

        /*Verify all teams have a name*/
        List<String> teamNames = ecnTeams.stream().map(ECNTeam::getName).collect(Collectors.toList());
        if (teamNames.isEmpty()){
            throw new EmptyTeamName();
        }

        /*Verify team names are unique, otherwise return list of duplicate names.*/
        List<String> nonUniqueTeamNames = teamNames.stream().filter(i-> Collections.frequency(teamNames, i)> 1).collect(Collectors.toList());
        if (!nonUniqueTeamNames.isEmpty()) {
            throw new NotUniqueTeamNames(nonUniqueTeamNames);
        }

        ecnTeams.forEach(t -> teams.add(new Team(t.getName(), t.getCardsCount(), t.getDefiners(),t.getGuessers())));

        return teams;
    }
}