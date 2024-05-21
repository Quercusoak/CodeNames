package DTO;

public enum TurnGuessStatus {
    BLACK("Black card. Game over."),
    CURRENTTEAM("Correct guess. Team gets a point: "),
    OTHERTEAM("Wrong guess. Other team gets a point: "),
    NEUTRAL("Wrong guess - card is neutral."),
    VICTORYCURRENTTEAM("Correct! All words found. Team won: ",true),
    VICTORYOTHERTEAM("Wrong guess. Other team found all words and won: ",true);

    private String status;
    private boolean isVictory;

    TurnGuessStatus(String status){
        this.status = status;
        isVictory = false;
    }

    TurnGuessStatus(String status, boolean isVictory){
        this.status = status;
        this.isVictory = isVictory;
    }

    public boolean getVictory(){ return isVictory;}

    public String toString() {
        return status;
    }
}
