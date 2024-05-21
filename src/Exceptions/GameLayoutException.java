package Exceptions;

public class GameLayoutException extends RuntimeException {

    public int getRows() {
        return rows;
    }

    private int rows;

    private int columns;

    public int getColumns() {
        return columns;
    }

    private int numCards;

    public int getNumCards() {
        return numCards;
    }

    public GameLayoutException(int rows, int columns, int numGameCards){
        this.rows =rows;
        this.columns = columns;
        numCards=numGameCards;
    }
}
