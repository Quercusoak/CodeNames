package exception;

public class NotEnoughWordsException extends RuntimeException {

    private final int numWords;

    public int getNumWords() {
        return numWords;
    }

    private final int numCards;

    public int getNumCards() {
        return numCards;
    }

    public NotEnoughWordsException(int numWords, int numCards){
        this.numWords = numWords;
        this.numCards =numCards;
    }
}
