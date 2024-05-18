package Exceptions;

public class NotEnoughCardsException extends RuntimeException{
    private String msg;

    public String getMessage() {
        return msg;
    }

    public NotEnoughCardsException(int sumCardsOfTeams, int numCardsinGame){
        this.msg = "Can't hand out "+sumCardsOfTeams+" cards to playing teams- only "
                +numCardsinGame+" words in file.";
    }
}
