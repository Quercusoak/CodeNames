package engine;

/*if (team == null and isBlack=false) then card is neutral */
public class GameCard {
    private String word;
    private Team team;
    private boolean isBlack;
    private int cardNumber;
    private boolean isFound;

    public GameCard(String word, Team team, boolean isBlack){
        this.word=word;
        this.team=team;
        this.isBlack=isBlack;
        isFound=false;
    }

    public String getWord() {
        return word;
    }

    public Team getTeam() {
        return team;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public GameCard(String word, boolean isBlack){
        this.word=word;
        this.isBlack=isBlack;
    }

    public void setTeam(Team t){
        team=t;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public boolean isFound() {
        return isFound;
    }

    public void setFound(boolean found) {
        isFound = found;
    }
}
