package player;

public enum GameMenuOptions {
    PLAY_TURN(1,"Play your turn."),
    GAME_STATUS(2,"Display game status.");

    private final String option;
    final int number;

    GameMenuOptions(int num ,String str){
        option = str;
        number = num;
    }

    public String toString() {
        return option;
    }
    public int getNumber() {
        return number;
    }
}
