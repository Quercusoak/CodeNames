package dto;

public class DTOCard {
    private final String word;
    private final DTOTeam team;
    private final boolean isBlack;
    private final int cardNumber;
    private final boolean isFound;

    public DTOCard(String word, DTOTeam team, boolean isBlack, int cardNumber, boolean isFound){
        this.word = word;
        this.team = team;
        this.isBlack = isBlack;
        this.isFound = isFound;
        this.cardNumber = cardNumber;
    }

    public String getWord() {
        return word;
    }

    public DTOTeam getTeam() {
        return team;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public boolean isFound() {
        return isFound;
    }
}
