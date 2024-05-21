package Exceptions;

import engine.GameCard;

public class CardAlreadyGuessed extends RuntimeException {
    private String msg;

    public String getMessage() {
        return msg;
    }

    public CardAlreadyGuessed(GameCard card){
        msg = card.getWord()+ " was already guessed.";
    }
}
