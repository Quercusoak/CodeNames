package dto;

import java.util.List;

public class DTOActiveGame {

    private final GameStatus gameStatus;
    public GameStatus getGameStatus() {return gameStatus;}

    private final List<DTOTeam> teams;
    public List<DTOTeam> getTeams() {
        return teams;
    }

/*    private final int rows;
    public int getRows() {
        return rows;
    }

    private final int cols;
    public int getColumns() {
        return cols;
    }

    private final List<DTOCard> cards;
    public List<DTOCard> getCardList() {
        return cards;
    }*/

    private final DTOBoard board;
    public DTOBoard getBoard() {return board;}

    private final int currTeamIndex;

    public DTOTeam getPlayingTeam() {
        return teams.get(currTeamIndex);
    }

    public DTOTeam nextTeam(){
        int nextTeamIndex = (currTeamIndex == (teams.size() - 1)) ? 0 : currTeamIndex + 1;
        return teams.get(nextTeamIndex);
    }

    public DTOActiveGame(GameStatus status, DTOBoard board, List<DTOTeam> teams, int currTeamIndex, String definitionToGuess) {
        this.gameStatus = status;
        this.board = board;
        this.teams = teams;
        this.currTeamIndex = currTeamIndex;
        this.definitionToGuess = definitionToGuess;
    }

    private final String definitionToGuess;
    public String getDefinitionToGuess() {return definitionToGuess;}
}
