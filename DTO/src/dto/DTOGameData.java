package dto;

import java.util.List;

public class DTOGameData {

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

    private final int rows;
    public int getRows() {
        return rows;
    }

    private final int cols;
    public int getCols() {
        return cols;
    }

    private final List<DTOTeam> teamsList;
    public List<DTOTeam> getDtoTeams() {return teamsList;}

    private List<DTOCard> cards;
    public List<DTOCard> getCards() {
        return cards;
    }

    public DTOGameData(String name, GameStatus status, String dictionaryFileName, int numDictionaryWords, int numCards,int numBlackCards,
                       int rows, int cols ,List<DTOTeam> teams, List<DTOCard> cards){
        gameName = name;
        gameStatus = status;
        this.dictionaryFileName = dictionaryFileName;
        this.numDictionaryWords = numDictionaryWords;
        this.numCards = numCards;
        this.numBlackCards = numBlackCards;
        this.rows = rows;
        this.cols = cols;
        this.teamsList = teams;
        this.cards = cards;
        //teams.forEach(t ->teamsList.put(team.getName(),team.getNumberOfCards()));
    }
}
