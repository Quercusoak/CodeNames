package engine;
import dto.GameStatus;
import dto.Role;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class GameManager {
    private final GameLogic gameLogic;
    private final List<GameData> gameDataList;
    private final Set<String> playerSet;

    public GameManager() {
        gameLogic = new engine.GameLogic();
        gameDataList = new ArrayList<>();
        playerSet = new HashSet<>();
    }

    public void AddGameData(String XMLPth) {
        gameDataList.add(gameLogic.readGameFile(XMLPth));
    }

    public List<GameData> getGameDataList() {
        return gameDataList;
    }

    public List<GameData> getActiveGamesList() {
        return gameDataList.stream()
                .filter(g-> g.getGameStatus().equals(GameStatus.ACTIVE))
                .collect(Collectors.toList());
    }

    public List<GameData> getPendingGamesList() {
        return gameDataList.stream()
                .filter(g-> g.getGameStatus().equals(GameStatus.PENDING))
                .collect(Collectors.toList());
    }

    public boolean addPlayerToGame(String playerName, String gameName, String teamName, String role) {

        AtomicBoolean result = new AtomicBoolean(false);

        gameDataList.stream()
                .filter(g -> g.getGameName().equals(gameName))
                .findFirst()
                .ifPresent(game -> game.getTeams().stream()
                        .filter(t -> t.getName().equals(teamName))
                        .findFirst()
                        .ifPresent(team -> {
                            if (team.addPlayer(playerName, Role.valueOf(role))) {
                                checkGameReady(game);
                                result.set(true);
                            }
                        }));
        return result.get();

        /*Optional<GameData> game = gameDataList.stream()
                .filter(g->g.getGameName().equals(gameName))
                .findFirst();

        if(!game.isPresent()) {
            return false;
        }
        Optional<Team> team = game.get().getTeams().stream()
                .filter(t->t.getName().equals(teamName))
                .findFirst();

        if(!team.isPresent()) {
            return false;
        }

        if (team.get().addPlayer(playerName,Role.valueOf(role))){
            checkGameReady(game.get());
            return true;
        }

        return false;*/
    }

    //For a game we added a player to- check if ready to begin
    private void checkGameReady(GameData game){

        long availableTeams = game.getTeams().stream()
                .filter(t -> !(t.getNumRegisteredDefiners()==t.getNumRequiredDefiners() && t.getNumRegisteredGuessers()==t.getNumRequiredGuessers()))
                .count();

        if (0 == availableTeams){
            gameLogic.startGame(game);
        }

    }

    public void playTurn(GameData game) {

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
