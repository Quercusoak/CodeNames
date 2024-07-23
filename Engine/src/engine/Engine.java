package engine;

import dto.*;

public interface Engine
{
    /*recieves full path, always displayed*/
    GameData readGameFile(String XMLpath);

    /*Only displayed when file successfully loaded*/
    DTOGameData displayGameParameters(GameData game);

    void startGame(GameData game);

    TurnStatus playTurn(Integer cardNum, GameData game);

    void turnEnd(GameData game);

    DTOBoard getGameBoard(GameData game);

    DTOTeam getCurrentTeam(GameData game);

//    TeamsList getTeams();
}
