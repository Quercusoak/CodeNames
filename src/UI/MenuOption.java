package UI;

public enum MenuOption {
    LOADXML("Load new xml file."),
    DISPLAYXML("Display game parametrs from file."),
    NEWGAME("Start new game."),
    PLAYTURN("Play turn."),
    GAMESTATUS("Display game status."),
    EXIT("Exit.");

    private final String option;

    MenuOption(String str){
        option = str;
    }

    public String toString() {
        return option;
    }

}
