package dto;

import engine.GameData;
import engine.GameStatus;
import java.util.HashMap;
import java.util.List;
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

    public String getGameName() {
        return gameName;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public String getDictionaryFileName() {
        return dictionaryFileName;
    }

    private final String gameName;
    private final GameStatus gameStatus;
    private final int rows;
    private final int cols;
    private final String dictionaryFileName;

    private final int numDictionaryWords;
    private final int numCards;
    private final int numBlackCards;
    private final Map<String, Integer> teams = new HashMap<>();
    private final TeamsList teamsList;

    public List<DTOTeam> getDtoTeams() {return teamsList.getTeamList();}


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
        this.teamsList = new TeamsList(gameData.getTeams());
    }
}
