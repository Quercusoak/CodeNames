package dto;

import engine.GameCard;

public class DTOCard {
    private String word;
    private DTOTeam team;
    private boolean isBlack;
    private int cardNumber;
    private boolean isFound;

    public DTOCard(GameCard card){
        word=card.getWord();
        team = card.getTeam() == null ? null : new DTOTeam(card.getTeam());
        isBlack= card.isBlack();
        isFound= card.isFound();
        cardNumber =card.getCardNumber();
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
