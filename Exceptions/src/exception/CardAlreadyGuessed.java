package exception;

public class CardAlreadyGuessed extends RuntimeException {
    private String cardWord;

    public String getWord() {
        return cardWord;
    }

    public CardAlreadyGuessed(String card){
        cardWord = card;
    }
}
