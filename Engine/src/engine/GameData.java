package engine;

import dto.GameStatus;

import java.io.Serializable;
import java.util.List;

public class GameData implements Serializable {

    private final String gameName;
    public String getGameName() {return gameName;}

    private GameStatus gameStatus;
    public GameStatus getGameStatus() {return gameSession.getGameStatus();}
    public void newActiveGameSession() {
        this.gameStatus = GameStatus.ACTIVE;
        this.gameSession.setGameStatus(GameStatus.ACTIVE);
    }
    public void gameEnded(){
        this.gameStatus = GameStatus.PENDING;
        gameSession.clearSession();
    }

    private final String dictionaryFileName;
    public String getDictionaryFileName() {return dictionaryFileName;}

    public List<String> getDictionaryWords() {
        return dictionaryWords;
    }
    private final List<String> dictionaryWords;

    private final int numCards;
    public int getNumCards() {return numCards;}

    private final int numBlackCards;
    public int getNumBlackCards() { return numBlackCards; }

    private final int rows;
    public int getRows() { return rows;}

    private final int columns;
    public int getColumns() {return columns;}

    private final List<Team> teams;
    public List<Team> getTeams() {
        return teams;
    }

    private final GameSession gameSession;
    public GameSession getGameSession() {return gameSession;}

    private final int numDictionaryWords;
/*    private final GameCard[][] board;
    public GameCard[][] getBoard() {
        return board;
    }

    private final Set<GameCard> cardsInGame;
    public Set<GameCard> getCards() {
        return cardsInGame;
    }

    private int currTeamIndex;

    public Team getPlayingTeam() {
        return teams.get(currTeamIndex);
    }*/


    public GameData(List<String> allWords, List<Team> teams, int numCards,
                            int numBlackCards, int rows, int columns, String gameName ,String dictionaryFileName){
        this.gameName = gameName;
//        gameStatus = GameStatus.PENDING;
        this.dictionaryFileName = dictionaryFileName;
        this.dictionaryWords = allWords;
        this.numCards = numCards;
        this.numBlackCards = numBlackCards;
        this.rows = rows;
        this.columns =columns;
        this.teams = teams;
        numDictionaryWords = allWords.size();

        gameSession = new GameSession(rows,columns,teams);
        gameStatus = gameSession.getGameStatus();
/*        board = new GameCard[rows][columns];
        cardsInGame = new HashSet<>();
        currTeamIndex = 0;*/
    }

/*    public void addCard(String word, Team team, boolean isBlack){
        cardsInGame.add(new GameCard(word, team, isBlack));
    }
    public void addCard(String word, boolean isBlack){
        cardsInGame.add(new GameCard(word, isBlack));
    }

    public void clearFinishedGame()
    {
        cardsInGame.clear();
        currTeamIndex = 0;
        teams.forEach(Team::clearTeam);
    }

    public void nextTeam(){
        do {
            currTeamIndex = (currTeamIndex == (teams.size() - 1)) ? 0 : currTeamIndex + 1;
        }while (!teams.get(currTeamIndex).isTeamPlaying());
    }*/
}
