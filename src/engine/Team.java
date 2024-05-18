package engine;

public class Team {
    public Team(String name, int numCards){
        this.name=name;
        this.numberOfCards=numCards;
        score = 0;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfCards() {
        return numberOfCards;
    }

    private String name;
    private int numberOfCards;
    private int score;
}
