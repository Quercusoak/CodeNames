package managers;
import engine.GameData;
import engine.GameLogic;

import java.util.ArrayList;
import java.util.List;

public class ServerManager {
    private final GameLogic gameLogic;
    private final List<GameData> gameDataList;
    private final Boolean adminSession;
    //List<Players> players;

    public ServerManager() {
        gameLogic = new engine.GameLogic();
        gameDataList = new ArrayList<>();
        adminSession = false;
    }

    public void AddGameData(String XMLPth) {
        gameDataList.add(gameLogic.readGameFile(XMLPth));
    }


}
