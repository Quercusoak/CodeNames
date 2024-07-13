package engine;

import java.io.Serializable;
import java.util.List;

public class GameData implements Serializable {

    public GameData(){
        gameStatus = GameStatus.PENDING;
    }

    private int cardsCount;
    public int getCardsCount() {return cardsCount;}

    private int blackCardsCount;
    public int getBlackCardsCount() { return blackCardsCount; }

    private int rows;
    public int getRows() { return rows;}

    private int columns;
    public int getColumns() {return columns;}

    private List<Team> teams;
    public List<Team> getTeams() {
        return teams;
    }

    public List<String> getDictionaryWords() {
        return dictionaryWords;
    }
    private List<String> dictionaryWords;

    private String gameName;
    public String getGameName() {return gameName;}

    private String dictionaryFileName;
    public String getDictionaryFileName() {return dictionaryFileName;}

    private GameStatus gameStatus;
    public GameStatus getGameStatus() {return gameStatus;}
    private void setGameStatus(GameStatus gameStatus) {this.gameStatus = gameStatus;}

    public void setGameData(List<String> allWords, List<Team> teams, int numCards,
                            int numBlackCards, int rows, int columns, String gameName ,String dictionaryFileName){
        this.dictionaryWords = allWords;
        this.teams = teams;
        this.cardsCount = numCards;
        this.blackCardsCount = numBlackCards;
        this.rows = rows;
        this.columns =columns;
        this.gameName = gameName;
        this.dictionaryFileName = dictionaryFileName;
    }
}
