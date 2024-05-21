package exception;

import engine.GameCard;

public class CardAlreadyGuessed extends RuntimeException {
    private String cardWord;

    public String getWord() {
        return cardWord;
    }

    public CardAlreadyGuessed(GameCard card){
        cardWord = card.getWord();
    }
}
