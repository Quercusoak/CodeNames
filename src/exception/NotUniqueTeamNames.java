package exception;

public class NotUniqueTeamNames extends RuntimeException {
    private String repeatingName;

    public String getRepeatingName() {
        return repeatingName;
    }

    public NotUniqueTeamNames(String repeatingName){
        this.repeatingName = repeatingName;
    }
}
