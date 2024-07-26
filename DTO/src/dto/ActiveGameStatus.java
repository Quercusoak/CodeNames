package dto;

import java.util.List;

public class ActiveGameStatus {

    private final GameStatus gameStatus;
    public GameStatus getGameStatus() {return gameStatus;}
    private final List<DTOTeam> teamsList;
    public List<DTOTeam> getDtoTeams() {return teamsList;}
    private final DTOBoard board;
    public DTOBoard getBoard() {return board;}
    private final DTOTeam currentTeam;
    public DTOTeam getCurrentTeam() {return currentTeam;}
    private final DTOTeam nextTeam;
    public DTOTeam getNextTeamTeam() {return nextTeam;}

    public ActiveGameStatus(GameStatus status, DTOBoard board, List<DTOTeam> teamsList, DTOTeam currentTeam, DTOTeam nextTeam) {
        this.teamsList = teamsList;
        this.board = board;
        this.currentTeam = currentTeam;
        this.nextTeam = nextTeam;
        this.gameStatus = status;
    }
}
