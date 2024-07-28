package dto;


public class TurnStatus {
    private final TurnGuessStatus status;
    private final DTOTeam teamWhoseCardItWas;
    private final String msg;
    private boolean gameOver;

    public TurnStatus(TurnGuessStatus s,DTOTeam t){
        status = s;
        teamWhoseCardItWas = t;
        msg = s.toString() + ((t!=null)? (t.getName() + (s.getVictory()? " Score: "+t.getScore() :"")): "");
        gameOver = false;
    }

    public DTOTeam getTeamWhoseCardItWas() {
        return teamWhoseCardItWas;
    }

    public TurnGuessStatus getStatus() {
        return status;
    }

    public String getMsg(){
        return msg;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    private String reasonGameOver;

    public String getReasonGameOver() { return reasonGameOver; }

    public void setGameOver(String reason) {
        this.gameOver = true;
        this.reasonGameOver = reason;
    }
}
