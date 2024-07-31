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
        this.gameSession.startGame();
    }

    public void gameEnded(){
        this.gameStatus = GameStatus.PENDING;
        teams.forEach(Team::clearTeam);
        gameSession.clearSession(teams);
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
    }
}
