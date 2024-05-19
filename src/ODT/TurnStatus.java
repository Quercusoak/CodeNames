package ODT;

import engine.Team;

public class TurnStatus {
    private TurnGuessStatus status;
    private Team team;

    public TurnStatus(TurnGuessStatus s,Team t){
        status = s;
        team = t;
    }

    public Team getTeam() {
        return team;
    }

    public TurnGuessStatus getStatus() {
        return status;
    }
}
