package engine;


import ODT.*;

public interface Engine
{
    /*recieves full path, always displayed*/
    void readGameFile(String XMLpath);

    /*Only displayed when file successfully loaded*/
    FileParams displayGameParameters();

    void startGame();

    TurnStatus playTurn(Integer cardNum);

    GameBoard getGameBoard();

    CurrentTeam getCurrentTeam();
}
