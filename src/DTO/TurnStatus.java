package DTO;

import engine.Team;

public class TurnStatus {
    private TurnGuessStatus status;
    private Team team;
    private String msg;

    public TurnStatus(TurnGuessStatus s,Team t){
        status = s;
        team = t;
        msg = s.toString() + ((t!=null)? (t.getName() + (s.getVictory()? " Score: "+t.getScore() :"")): "");
    }

    public Team getTeam() {
        return team;
    }

    public TurnGuessStatus getStatus() {
        return status;
    }

    public String getMsg(){
        return msg;
    }
}
