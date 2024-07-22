package dto;

public class DTOTeam {
    public DTOTeam(String teamName, int numberOfCards, int score ,int numTurnsPlayed,
                   int numRequiredDefiners, int numRegisteredDefiners, int numRequiredGuessers, int numRegisteredGuessers){
        name=teamName;
        this.numberOfCards= numberOfCards;
        this.score = score;
        this.numTurnsPlayed = numTurnsPlayed;
        this.numRequiredDefiners = numRequiredDefiners;
        this.numRequiredGuessers =numRequiredGuessers;
        this.numRegisteredDefiners = numRegisteredDefiners;
        this.numRegisteredGuessers = numRegisteredGuessers;
    }

    private final String name;
    public String getName() {
        return name;
    }

    private final int numberOfCards;
    public int getNumberOfCards() {
        return numberOfCards;
    }

    private final int score;
    public int getScore(){
        return score;
    }

    private final int numTurnsPlayed;
    public int getNumTurnsPlayed() {
        return numTurnsPlayed;
    }

    private final int numRequiredDefiners;
    public int getNumRequiredDefiners() {return numRequiredDefiners;}

    private final int numRequiredGuessers;
    public int getNumRequiredGuessers() {return numRequiredGuessers;}

    private final int numRegisteredDefiners;
    public int getNumRegisteredDefiners() {return numRegisteredDefiners;}

    private final int numRegisteredGuessers;
    public int getNumRegisteredGuessers() {return numRegisteredGuessers;}
}
