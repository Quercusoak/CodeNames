package exception;

public class NotEnoughWordsException extends RuntimeException {

    private int numWords;

    public int getNumWords() {
        return numWords;
    }

    private int numCards;

    public int getNumCards() {
        return numCards;
    }

    private boolean isBlack;

    public boolean isBlack() {
        return isBlack;
    }

    public NotEnoughWordsException(int numWords, int numCards, boolean black){
        this.numWords = numWords;
        this.isBlack=black;
        this.numCards =numCards;
    }
}
