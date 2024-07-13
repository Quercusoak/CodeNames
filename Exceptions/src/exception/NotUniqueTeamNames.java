package exception;

import java.util.List;

public class NotUniqueTeamNames extends RuntimeException {
    private final List<String> repeatingName;

    public List<String> getRepeatingName() {
        return repeatingName;
    }

    public NotUniqueTeamNames(List<String> repeatingName){
        this.repeatingName = repeatingName;
    }
}
