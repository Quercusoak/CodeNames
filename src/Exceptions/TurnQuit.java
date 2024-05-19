package Exceptions;

public class TurnQuit extends RuntimeException{
    private String msg;

    public String getMessage() {
        return msg;
    }

    public TurnQuit(){
        msg="End of turn.";
    }
}
