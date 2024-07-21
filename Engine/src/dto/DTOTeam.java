package dto;

import engine.Team;

public class DTOTeam {
    public DTOTeam(Team t){
        name=t.getName();
        numberOfCards=t.getNumberOfCards();
        score = t.getScore();
        numTurnsPlayed =t.getNumTurnsPlayed();
        numRequiredDefiners = t.getNumRequiredDefiners();
        numRequiredGuessers = t.getNumRequiredGuessers();
        numRegisteredDefiners = t.getNumRegisteredDefiners();
        numRegisteredGuessers = t.getNumRegisteredGuessers();
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
