package dto;


public class TurnStatus {
    private TurnGuessStatus status;
    private DTOTeam team;
    private String msg;

    public TurnStatus(TurnGuessStatus s,DTOTeam t){
        status = s;
        team = t;
        msg = s.toString() + ((t!=null)? (t.getName() + (s.getVictory()? " Score: "+t.getScore() :"")): "");
    }

    public DTOTeam getTeam() {
        return team;
    }

    public TurnGuessStatus getStatus() {
        return status;
    }

    public String getMsg(){
        return msg;
    }
}
