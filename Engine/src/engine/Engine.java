package engine;

import dto.*;

public interface Engine
{
    /*recieves full path, always displayed*/
    DTOGameData readGameFile(String XMLpath);

    /*Only displayed when file successfully loaded*/
    DTOGameData displayGameParameters();

    void startGame();

    TurnStatus playTurn(Integer cardNum);

    void turnEnd();

    DTOBoard getGameBoard();

    DTOTeam getCurrentTeam();

//    TeamsList getTeams();
}
