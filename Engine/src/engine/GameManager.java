package engine;
import dto.DTOGameData;
import dto.DTOTeam;
import dto.GameStatus;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {
    private final GameLogic gameLogic;
    private final List<DTOGameData> gameDataList;
    private final Set<String> playerSet;

    public GameManager() {
        gameLogic = new engine.GameLogic();
        gameDataList = new ArrayList<>();
        playerSet = new HashSet<>();
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

    public List<DTOGameData> getPendingGamesList() {
        return gameDataList.stream()
                .filter(g-> g.getGameStatus().equals(GameStatus.PENDING))
                .collect(Collectors.toList());
    }

    public boolean addPlayerToGame(String playerName, String gameName, String teamName, String role) {
        Optional<DTOGameData> game = gameDataList.stream()
                .filter(g->g.getGameName().equals(gameName))
                .findFirst();

        game.ifPresent(g-> {
            Optional<DTOTeam> team = g.getDtoTeams().stream()
                    .filter(t->t.getName().equals(teamName))
                    .findFirst();
            team.ifPresent(t->{
                //
                t.getName();
            });
                });
        return false;
    }

    public synchronized void addPlayer(String username) {
        playerSet.add(username);
    }

    public synchronized void removePlayer(String username) {
        playerSet.remove(username);
    }

    public synchronized Set<String> getPlayers() {
        return Collections.unmodifiableSet(playerSet);
    }

    public boolean isPlayerExists(String username) {
        return playerSet.contains(username);
    }
}
