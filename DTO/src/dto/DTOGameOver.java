package dto;

public class DTOGameOver {

    public DTOGameOver(String reasonGameOver) {
        this.reasonGameOver = reasonGameOver;
        gameOver= true;
    }

    public DTOGameOver() {
        gameOver= false;
    }

    private final boolean gameOver;

    public boolean isGameOver() {
        return gameOver;
    }

    private String reasonGameOver;

    public String getReasonGameOver() { return reasonGameOver; }
}
