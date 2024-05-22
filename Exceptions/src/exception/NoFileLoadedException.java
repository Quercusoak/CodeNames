package exception;

public class NoFileLoadedException  extends RuntimeException{
    private String msg;

    public String getMessage() {
        return msg;
    }

    public NoFileLoadedException(){
        msg="Must load file first.";
    }
}
