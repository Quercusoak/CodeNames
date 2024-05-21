package DTO;

import engine.Team;

public class DTOTeam {
    public DTOTeam(Team t){
        name=t.getName();
        numberOfCards=t.getNumberOfCards();
        score = t.getScore();
        numTurnsPlayed =t.getNumTurnsPlayed();
    }

    private String name;
    public String getName() {
        return name;
    }

    private int numberOfCards;
    public int getNumberOfCards() {
        return numberOfCards;
    }

    private int score;
    public int getScore(){
        return score;
    }

    private int numTurnsPlayed;
    public int getNumTurnsPlayed() {
        return numTurnsPlayed;
    }
}
