package managers;
import dto.DTOGameData;
import dto.GameStatus;
import engine.GameLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServerManager {
    private final GameLogic gameLogic;
    private final List<DTOGameData> gameDataList;
    //List<Players> players;

    public ServerManager() {
        gameLogic = new engine.GameLogic();
        gameDataList = new ArrayList<>();
    }

    public void AddGameData(String XMLPth) {
        gameDataList.add(gameLogic.readGameFile(XMLPth));
    }

    public List<DTOGameData> getGameDataList() {
        return gameDataList;
    }

    public List<DTOGameData> getActiveGamesList() {
        return gameDataList.stream()
                .filter(g-> g.getGameStatus().equals(GameStatus.ACTIVE))
                .collect(Collectors.toList());
    }
}
