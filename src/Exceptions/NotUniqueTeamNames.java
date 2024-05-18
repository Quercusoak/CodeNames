package Exceptions;

public class NotUniqueTeamNames extends RuntimeException {
    private String msg;

    public String getMessage() {
        return msg;
    }

    public NotUniqueTeamNames(String repeatingName){
        msg = "Team names must be unique, change name: "+repeatingName;
    }
}
