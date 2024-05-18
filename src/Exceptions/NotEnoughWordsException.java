package Exceptions;

public class NotEnoughWordsException extends RuntimeException {

    private String msg;

    public String getMessage() {
        return msg;
    }

    public NotEnoughWordsException(long numWords, int numCards, boolean black){
        this.msg = "Game can't start with "+(black ? "black" :"") +numCards+
                " cards since there are only "+numWords+(black ? "black" :"") +" words in file.";
    }
}
