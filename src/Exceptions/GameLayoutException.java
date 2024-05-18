package Exceptions;

public class GameLayoutException extends RuntimeException {
    private String msg;

    public String getMessage() {
        return msg;
    }

    public GameLayoutException(int rows, int columns, int numGameCards){
        msg = "Game layout ("+rows+" X "+columns+")"+" not large enough to contain "+numGameCards+" cards";
    }
}
