package exception;

public class CardSelectionOutOfBound extends RuntimeException{
    private String msg;

    public String getMessage() {
        return msg;
    }

    public CardSelectionOutOfBound(int maxCardsNum){
        msg="Number out of board's bound. Select number between 1 and "+maxCardsNum;
    }
}
