package managers;
import dto.DTOActiveGame;
import dto.FileParams;
import engine.GameLogic;

import java.util.ArrayList;
import java.util.List;

public class ServerManager {
    private final GameLogic gameLogic;
    private final List<FileParams> gameDataList;
    private final List<DTOActiveGame> activeGameList;
    //List<Players> players;

    public ServerManager() {
        gameLogic = new engine.GameLogic();
        gameDataList = new ArrayList<>();
        activeGameList = new ArrayList<>();
    }

    public void AddGameData(String XMLPth) {
        gameDataList.add(new FileParams(gameLogic.readGameFile(XMLPth)));
    }

    public List<FileParams> getGameDataList() {
        return gameDataList;
    }

    public List<DTOActiveGame> getActiveGamesList() {
        return activeGameList;
    }
}
