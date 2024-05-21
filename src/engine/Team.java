package engine;

public class Team {
    public Team(String name, int numCards){
        this.name=name;
        numberOfCards=numCards;
        score = 0;
        numTurnsPlayed = 0;
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
