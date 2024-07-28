package dto;


public class TurnStatus {
    private final TurnGuessStatus status;
    private final DTOTeam teamWhoseCardItWas;
    private final String guessOutcomeMsg;

    public TurnStatus(TurnGuessStatus s,DTOTeam t){
        status = s;
        teamWhoseCardItWas = t;
        guessOutcomeMsg = s.toString() + ((t!=null)? (t.getName() + (s.getVictory()? " Score: "+t.getScore() :"")): "");
    }

    public DTOTeam getTeamWhoseCardItWas() {
        return teamWhoseCardItWas;
    }

    public TurnGuessStatus getStatus() {
        return status;
    }

    public String getGuessOutcomeMsg(){
        return guessOutcomeMsg;
    }

}
