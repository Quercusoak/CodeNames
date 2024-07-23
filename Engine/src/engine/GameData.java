package engine;

import dto.GameStatus;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameData implements Serializable {

    private String gameName;
    public String getGameName() {return gameName;}

    private GameStatus gameStatus;
    public GameStatus getGameStatus() {return gameStatus;}
    public void setGameStatus(GameStatus gameStatus) {this.gameStatus = gameStatus;}

    private String dictionaryFileName;
    public String getDictionaryFileName() {return dictionaryFileName;}

    public List<String> getDictionaryWords() {
        return dictionaryWords;
    }
    private List<String> dictionaryWords;

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

    private final GameCard[][] board;
    private final Set<GameCard> cardsInGame;
    private int currTeamIndex;

    public Team getPlayingTeam() {
        return teams.get(currTeamIndex);
    }

    public GameCard[][] getBoard() {
        return board;
    }

    public Set<GameCard> getCards() {
        return cardsInGame;
    }

    public GameData(List<String> allWords, List<Team> teams, int numCards,
                            int numBlackCards, int rows, int columns, String gameName ,String dictionaryFileName){
        this.gameName = gameName;
        gameStatus = GameStatus.PENDING;
        this.dictionaryFileName = dictionaryFileName;
        this.dictionaryWords = allWords;
        this.cardsCount = numCards;
        this.blackCardsCount = numBlackCards;
        this.rows = rows;
        this.columns =columns;
        this.teams = teams;
        board = new GameCard[rows][columns];
        cardsInGame = new HashSet<>();
        currTeamIndex = 0;
    }

    public void addCard(String word, Team team, boolean isBlack){
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
        currTeamIndex = (currTeamIndex == (teams.size()-1))? 0: currTeamIndex+1;
    }
}
