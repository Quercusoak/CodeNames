package dto;

import java.util.List;

public class DTOActiveGame {

    public DTOActiveGame(int rows, int columns, List<DTOTeam> teams, List<DTOCard> cards) {
        this.rows = rows;
        this.cols = columns;
        this.teams = teams;
        this.cards = cards;
    }

    private final List<DTOTeam> teams;
    public List<DTOTeam> getTeams() {
        return teams;
    }

    private final int rows;
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
    }

    private int currTeamIndex;

    public DTOTeam getPlayingTeam() {
        return teams.get(currTeamIndex);
    }

    public DTOTeam nextTeam(){
        int nextTeamIndex = (currTeamIndex == (teams.size() - 1)) ? 0 : currTeamIndex + 1;
        return teams.get(nextTeamIndex);
    }
}
