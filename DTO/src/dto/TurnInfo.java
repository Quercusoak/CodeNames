package dto;

public class TurnInfo {

    private final String definitionToGuess;
    public String getDefinitionToGuess() {return definitionToGuess;}

    private final int numGuesses;
    public int getNumGuesses() {return numGuesses;}

    private final DTOBoard board;
    public DTOBoard getBoard() {return board;}

    public TurnInfo(String definitionToGuess, int numGuesses, DTOBoard board) {
        this.definitionToGuess = definitionToGuess;
        this.numGuesses = numGuesses;
        this.board = board;
        gameOver = false;
    }

    public TurnInfo(DTOBoard board) {
        this.definitionToGuess = "";
        this.numGuesses = -1;
        this.board = board;
        gameOver = false;
    }

    public TurnInfo(String reason) {
        this.definitionToGuess = "";
        this.numGuesses = -1;
        this.board = null;
        gameOver = true;
        this.reasonGameOver = reason;

    }

    private final boolean gameOver;

    public boolean isGameOver() {
        return gameOver;
    }

    private String reasonGameOver;

    public String getReasonGameOver() { return reasonGameOver; }

}
