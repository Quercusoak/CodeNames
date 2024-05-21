package Exceptions;

public class NotEnoughDistinctBlackCards extends RuntimeException{
    public NotEnoughDistinctBlackCards(int numBlackCardsGenerated, int numBlackCardsDesired){
        this.numBlackCardsDesired =numBlackCardsDesired;
        this.numBlackCardsGenerated =numBlackCardsGenerated;
    }

    private int numBlackCardsGenerated;
    private int numBlackCardsDesired;

    public int getNumBlackCardsDesired() {
        return numBlackCardsDesired;
    }

    public int getNumBlackCardsGenerated() {
        return numBlackCardsGenerated;
    }
}
