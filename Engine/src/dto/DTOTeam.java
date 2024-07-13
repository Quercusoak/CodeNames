package dto;

import engine.Team;

public class DTOTeam {
    public DTOTeam(Team t){
        name=t.getName();
        numberOfCards=t.getNumberOfCards();
        score = t.getScore();
        numTurnsPlayed =t.getNumTurnsPlayed();
        numDefiners = t.getNumDefiners();
        numGuessers = t.getNumGuessers();
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

    private final int numDefiners;
    public int getNumDefiners() {return numDefiners;}

    private final int numGuessers;
    public int getNumGuessers() {return numGuessers;}
}
