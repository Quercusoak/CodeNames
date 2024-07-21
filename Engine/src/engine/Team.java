package engine;

import java.io.Serializable;

public class Team implements Serializable {
    public Team(String name, int numCards, int definers, int guessers){
        this.name=name;
        numberOfCards=numCards;
        numRequiredDefiners = definers;
        numRequiredGuessers = guessers;
        score = 0;
        numTurnsPlayed = 0;
        numRegisteredDefiners = 0;
        numRegisteredGuessers = 0;
    }

    private final String name;
    public String getName() {
        return name;
    }

    private final int numberOfCards;
    public int getNumberOfCards() {
        return numberOfCards;
    }

    private final int numRequiredDefiners;
    public int getNumRequiredDefiners() {return numRequiredDefiners;}

    private final int numRequiredGuessers;
    public int getNumRequiredGuessers() {return numRequiredGuessers;}

    private final int numRegisteredDefiners;
    public int getNumRegisteredDefiners() {return numRegisteredDefiners;}

    private final int numRegisteredGuessers;
    public int getNumRegisteredGuessers() {return numRegisteredGuessers;}

    private int score;
    public int getScore(){
        return score;
    }

    public void addPoint(){
        score++;
    }

    private int numTurnsPlayed;
    public int getNumTurnsPlayed() {
        return numTurnsPlayed;
    }
    public void incTurnCounter() {
        numTurnsPlayed++;
    }
}
