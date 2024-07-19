public enum AdminMenuOptions {
    LOADXML("Load new xml file."),
    DISPLAYXML("Display game parametrs from file."),
    VIEWGAME("Observe an ongoing game."),
    EXIT("Exit.");

    private final String option;

    AdminMenuOptions(String str){
        option = str;
    }

    public String toString() {
        return option;
    }
}
