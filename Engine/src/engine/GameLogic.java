package engine;

import dto.*;
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

import engine.jaxb.generated.ECNGame;

public class GameLogic implements Engine, Serializable {

    private final static String JAXB_XML_GAME_PACKAGE_NAME = "engine.jaxb.generated";
    private final static String REGEX_TO_EXCLUDE_FROM_DICTIONARY = "[ \\n\\t\\r]";
    private final static String CHARS_TO_REMOVE_FROM_DICTIONARY = "[^\\p{L}]";
    private final static Integer QUIT_TURN = 0;


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

        return new GameData(dictinary, teams,numWords,numBlackWords,rows,columns, ecnGame.getName(), ecnGame.getECNDictionaryFile());
    }

    private DTOGameData getDTOGameDataFromGame(GameData game) {
        /*List<DTOCard> cards = new ArrayList<>();
        if (game.getGameStatus().equals(GameStatus.ACTIVE)) {
            game.getActiveGame().getCards().forEach(card -> cards.add(new DTOCard(card.getWord(),
                    card.getTeam() == null ? null : getDTOTeamFromTeam(card.getTeam()),
                    card.isBlack(), card.getCardNumber(), card.isFound())));
        }*/

        List<DTOTeam> teams = new ArrayList<>();
        game.getTeams().forEach(t -> teams.add(getDTOTeamFromTeam(t)));

        return new DTOGameData(game.getGameName(), game.getGameStatus(), game.getDictionaryFileName(), game.getDictionaryWords().size(),
                game.getCardsCount(), game.getBlackCardsCount(), game.getRows(), game.getColumns(), teams);
    }

    public DTOActiveGame getActiveGameFromGame(GameSession game) {
        List<DTOCard> cards = new ArrayList<>();
        game.getCards().forEach(card -> cards.add(new DTOCard(card.getWord(),
                card.getTeam() == null ? null : getDTOTeamFromTeam(card.getTeam()),
                card.isBlack(), card.getCardNumber(), card.isFound())));

        List<DTOTeam> teams = new ArrayList<>();
        game.getTeams().forEach(t -> teams.add(getDTOTeamFromTeam(t)));

        return new DTOActiveGame(game.getRows(), game.getColumns(), teams, cards);
    }

    public void startGame(GameData game){

        game.setGameStatus(GameStatus.ACTIVE);

        game.setActiveGame(new GameSession(game.getRows(), game.getColumns(), game.getTeams()));

        /*Generate cards for game session*/
        generateCards(game);

        /*Put cards in board*/
        /*int i=0;
        for (GameCard card : game.getCards()){
            card.setCardNumber(i+1);
            game.getBoard()[i / game.getColumns()][i % game.getColumns()]=card;
            i++;
        }*/
    }

    private void generateCards(GameData game){
        /*Generate cards for game session*/
        boolean isBlack = true;

        List<String> dict = game.getDictionaryWords();
        Collections.shuffle(dict);

        //Partition dictionary into two lists - cards and black cards
        Map<Boolean, List<String>> partitioned = dict.stream()
                .distinct()
                .limit(game.getCardsCount()+ game.getBlackCardsCount())
                .collect(Collectors.partitioningBy(i -> dict.indexOf(i) < game.getCardsCount()));

        List<String> currGameCards = partitioned.get(true);
        List<String> currGameBlackCards = partitioned.get(false);

        currGameBlackCards.forEach(a->game.getActiveGame().addCard(a,isBlack));

        /*Assign to teams and create cards*/
        int count=0;
        for (Team team : game.getTeams()){
            IntStream.range(count,team.getNumberOfCards() + count)
                    .mapToObj(currGameCards::get)
                    .forEach(a->game.getActiveGame().addCard(a,team,!isBlack));

            count=count+team.getNumberOfCards();
        }

        /*Rest of words are neutral*/
        IntStream.range(count,game.getCardsCount())
                .mapToObj(currGameCards::get)
                .forEach(a->game.getActiveGame().addCard(a,!isBlack));
    }

    public DTOBoard getGameBoard(GameSession game){
        if (game==null){
            throw new GameInactiveException();
        }

        GameCard[][] board = game.getBoard();
        List<DTOCard> cards =  new ArrayList<>();
        int rows = game.getRows(), columns = game.getColumns();
        for (int i = 0; i < Math.min((rows * columns), game.getCards().size()); i++) {
            GameCard card = board[i / columns][i % columns];
            cards.add(i, new DTOCard(card.getWord(),
                    card.getTeam() == null ? null : getDTOTeamFromTeam(card.getTeam()),
                    card.isBlack(),card.getCardNumber(), card.isFound()
            ));
        }

        return new DTOBoard(cards, game.getRows(), game.getColumns());
    }

    public Team getCurrentTeam(GameSession game){
        if (game==null){
            throw new GameInactiveException();
        }

        return game.getPlayingTeam();
    }

    @Override
    public TurnStatus playTurn(int cardNum,GameSession game) {
        if (game==null){
            throw new GameInactiveException();
        }

        //team chose to stop guessing
        if (cardNum == QUIT_TURN){
            return new TurnStatus(TurnGuessStatus.TEAMSKIPPED,getDTOTeamFromTeam(game.getPlayingTeam()));
        }

        if (cardNum>game.getCards().size() || cardNum<0) {
            throw new CardSelectionOutOfBound(game.getCards().size());
        }

        GameCard card = game.getBoard()[cardNum / game.getColumns()][cardNum % game.getColumns()];

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

        return new TurnStatus(guessStatus,teamWhoseCardItIs==null? null : getDTOTeamFromTeam(teamWhoseCardItIs));
    }

    @Override
    public void turnEnd(GameSession game) {
        /*Increment teams turn count*/
        game.getPlayingTeam().incTurnCounter();
        /*Increment current team to next:*/
        game.nextTeam();
    }

//    public TeamsList getTeams(){
//        return new TeamsList(game.getTeams());
//    }

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

    private DTOTeam getDTOTeamFromTeam(Team t){
        return new DTOTeam(t.getName(),t.getNumberOfCards(), t.getScore(), t.getNumTurnsPlayed(), t.getNumRequiredDefiners(),
                 t.getNumRegisteredDefiners(), t.getNumRequiredGuessers(),t.getNumRegisteredGuessers());
    }

    public DTOGameData displayGameParameters(GameData gameData){
        if (gameData==null){
            throw new NoFileLoadedException();
        }
        return getDTOGameDataFromGame(gameData);
    }
}