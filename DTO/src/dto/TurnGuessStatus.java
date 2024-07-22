package dto;

public enum TurnGuessStatus {
    BLACK("Black card. Game over."),
    CURRENTTEAM("Correct guess."),
    OTHERTEAM("Wrong guess."),
    NEUTRAL("Wrong guess - card is neutral."),
    VICTORYCURRENTTEAM("Correct! All words found.",true),
    VICTORYOTHERTEAM("Wrong guess. Other team found all words.",true);

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
