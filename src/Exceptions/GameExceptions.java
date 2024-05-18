package Exceptions;

public enum GameExceptions {
    //LAYOUT("Game layout ("+rows+" X "+columns+")"+" not large enough to contain "+numGameCards+" cards"),
    NOFILELOADED("Must load file first.");// , NOTENOUGHCARDS, NOTENOUGHWORDS;

    private String msg;

    GameExceptions(String msg){
        this.msg=msg;
    }
}
