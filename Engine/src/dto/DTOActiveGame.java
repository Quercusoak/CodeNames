package dto;

import engine.GameStatus;
import java.util.List;

public class DTOActiveGame {

    private final DTOBoard board;
    public DTOBoard getBoard() { return board; }

    private final String gameName;
    public String getGameName() {
        return gameName;
    }

    private final GameStatus gameStatus;
    public GameStatus getGameStatus() {
        return gameStatus;
    }

    private final String dictionaryFileName;
    public String getDictionaryFileName() {
        return dictionaryFileName;
    }

    private final int numDictionaryWords;
    public int getNumDictionaryWords() {
        return numDictionaryWords;
    }

    private final int numCards;
    public int getNumCards() {
        return numCards;
    }

    private final int numBlackCards;
    public int getNumBlackCards() {
        return numBlackCards;
    }

    private final List<DTOTeam> teamsList;
    public List<DTOTeam> getTeams() {return teamsList;}

    public DTOActiveGame(FileParams gameData, DTOBoard board) {
        this.board = board;
        gameName = gameData.getGameName();
        gameStatus = gameData.getGameStatus();
        dictionaryFileName = gameData.getDictionaryFileName();
        numDictionaryWords = gameData.getNumDictionaryWords();
        numCards = gameData.getNumCards();
        numBlackCards = gameData.getNumBlackCards();
        this.teamsList = gameData.getDtoTeams();
    }
}
