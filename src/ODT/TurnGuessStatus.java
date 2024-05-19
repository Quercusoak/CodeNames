package ODT;

public enum TurnGuessStatus {
    BLACK("Black card. Game over."),
    CURRENTTEAM("Correct guess. Team gets a point."),
    OTHERTEAM("Wrong guess. Other team gets a point."),
    NEUTRAL("Wrong guess - card is neutral."),
    VICTORYCURRENTTEAM("Correct! All words found. Team won."),
    VICTORYOTHERTEAM("Wrong guess. Other team found all words and won.");

    private String status;

    TurnGuessStatus(String status){
        this.status = status;
    }

    public String toString() {
        return status;
    }
}
