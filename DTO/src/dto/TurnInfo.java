package dto;

public class TurnInfo {

    private String definitionToGuess;
    private DTOTeam currentTeam;
    private String nextTeam;
    private int numGuesses;
    private DTOBoard board;

    public TurnInfo(String definitionToGuess, DTOTeam currentTeam, String nextTeam, int numGuesses, DTOBoard board) {
        this.definitionToGuess = definitionToGuess;
        this.currentTeam = currentTeam;
        this.nextTeam = nextTeam;
        this.numGuesses = numGuesses;
        this.board = board;
    }

    public String getDefinitionToGuess() {return definitionToGuess;}
    public DTOTeam getCurrentTeam() {return currentTeam;}
    public int getNumGuesses() {return numGuesses;}
    public DTOBoard getBoard() {return board;}
}
