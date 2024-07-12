package exception;

public class GameInactiveException extends RuntimeException{
    private String msg;

    public String getMessage() {
        return msg;
    }

    public GameInactiveException(){
        msg="Must first start new game.";
    }
}
