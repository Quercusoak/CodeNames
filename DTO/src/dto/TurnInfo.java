package dto;

public class TurnInfo {

    private final String definitionToGuess;
    private final int numGuesses;
    private final DTOBoard board;
    private final TurnStatus turnStatus;
    private final String reasonGameOver;

    public String getDefinitionToGuess() {return definitionToGuess;}
    public int getNumGuesses() {return numGuesses;}
    public DTOBoard getBoard() {return board;}
    public TurnStatus getTurnStatus() {return turnStatus;}
    public String getReasonGameOver() { return reasonGameOver; }

    //For guesser at the start of the turn
    public TurnInfo(String definitionToGuess, int numGuesses, DTOBoard board) {
        this.definitionToGuess = definitionToGuess;
        this.numGuesses = numGuesses;
        this.board = board;
        turnStatus = null;
        reasonGameOver = null;
    }

    //For definer to just get the board at start of turn
    public TurnInfo(DTOBoard board) {
        this.board = board;
        definitionToGuess = null;
        numGuesses = -1;
        turnStatus = null;
        reasonGameOver = null;
    }

    //For guesser to get turn outcome and relevant board
    public TurnInfo(DTOBoard board, TurnStatus status, String reasonGameOver) {
        this.board = board;
        this.turnStatus = status;
        this.reasonGameOver = reasonGameOver;
        definitionToGuess = null;
        numGuesses = -1;
    }

    //For when player attempts to play after the game has ended
    public TurnInfo(String reasonGameOver) {
        this.reasonGameOver = reasonGameOver;
        definitionToGuess = null;
        numGuesses = -1;
        board = null;
        turnStatus = null;
    }

}
