package engine;

public class Team {
    public Team(){}

    public Team(String name, int numCards){
        this.name=name;
        this.numberOfCards=numCards;
    }

    public String getName() {
        return name;
    }

/*    public void setName(String name) {
        this.name = name;
    }*/

    public int getNumberOfCards() {
        return numberOfCards;
    }

/*    public void setNumberOfCards(int numberOfCards) {
        this.numberOfCards = numberOfCards;
    }*/

    private String name;
    private int numberOfCards;
}
