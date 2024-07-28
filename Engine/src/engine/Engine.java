package engine;

import dto.*;

import java.util.List;

public interface Engine
{
    /*recieves full path, always displayed*/
    GameData readGameFile(String XMLpath);

    /*Only displayed when file successfully loaded*/
    List<DTOGameData> displayGameParameters(List<GameData> gameDataList);

    void startGame(GameData game);

    TurnStatus playTurn(int cardNum, GameSession game);

    void turnEnd(GameSession game);

    //DTOBoard getGameBoard(GameData game);

    //Team getCurrentTeam(GameData game);

//    TeamsList getTeams();
}
