package player;

public enum GameMenuOptions {
    GAME_STATUS(1,"Display game status"),
    PLAY_TURN(2,"Play your turn");

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
