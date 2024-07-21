package player;

public enum PlayerMenuOptions {
    GAMES_INFO("Show games info"),
    JOIN_GAME("Join game"),
    EXIT("Exit.");

    private final String option;

    PlayerMenuOptions(String str){
        option = str;
    }

    public String toString() {
        return option;
    }
}
