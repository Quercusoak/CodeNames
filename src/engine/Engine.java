package engine;


import DTO.*;

public interface Engine
{
    /*recieves full path, always displayed*/
    void readGameFile(String XMLpath);

    /*Only displayed when file successfully loaded*/
    FileParams displayGameParameters();

    void startGame();

    TurnStatus playTurn(Integer cardNum);

    void turnEnd();

    DTOBoard getGameBoard();

    DTOTeam getCurrentTeam();

    TeamsList getTeams();
}
