package dto;

import engine.GameData;
import engine.GameStatus;

import java.util.HashMap;
import java.util.Map;

public class FileParams {
    public int getNumDictionaryWords() {
        return numDictionaryWords;
    }

    public int getNumCards() {
        return numCards;
    }

    public Map<String, Integer> getTeams() {
        return teams;
    }

    public int getNumBlackCards() {
        return numBlackCards;
    }

    public final String gameName;
    public final GameStatus gameStatus;
    public final int rows;
    public final int cols;
    public final String dictionaryFileName;

    private final int numDictionaryWords;
    private final int numCards;
    private final int numBlackCards;
    private final Map<String, Integer> teams = new HashMap<>();


    public FileParams(GameData gameData){
        gameName = gameData.getGameName();
        gameStatus = gameData.getGameStatus();
        rows = gameData.getRows();
        cols = gameData.getColumns();
        dictionaryFileName = gameData.getDictionaryFileName();

        numDictionaryWords = gameData.getDictionaryWords().size();
        numCards = gameData.getCardsCount();
        numBlackCards = gameData.getBlackCardsCount();
        gameData.getTeams().forEach(team ->this.teams.put(team.getName(),team.getNumberOfCards()));
    }
}
