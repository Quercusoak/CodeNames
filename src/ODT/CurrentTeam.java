package ODT;

import engine.Team;

public class CurrentTeam {
    public CurrentTeam(Team t){
        name=t.getName();
        numberOfCards=t.getNumberOfCards();
        score = t.getScore();
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
}
