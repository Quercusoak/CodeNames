public enum AdminMenuOptions {
    LOAD_XML("Load new xml file."),
    DISPLAY_XML("Display game parametrs from file."),
    VIEW_GAME("Observe an ongoing game."),
    EXIT("Exit.");

    private final String option;

    AdminMenuOptions(String str){
        option = str;
    }

    public String toString() {
        return option;
    }
}
