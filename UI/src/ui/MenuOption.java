package ui;

public enum MenuOption {
    LOADXML("Load new xml file."),
    DISPLAYXML("Display game parametrs from file."),
    NEWGAME("Start new game."),
    PLAYTURN("Play turn."),
    GAMESTATUS("Display game status."),
    LOADSAVE("Load saved game."),
    SAVEGAME("Save game."),
    EXIT("Exit.");

    private final String option;

    MenuOption(String str){
        option = str;
    }

    public String toString() {
        return option;
    }

}
