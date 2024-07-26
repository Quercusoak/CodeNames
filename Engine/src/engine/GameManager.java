package engine;
import dto.*;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {
    private final GameLogic gameLogic = new engine.GameLogic();;
    private final List<GameData> gameList = new ArrayList<>();;
    private final Map<String, Player> playerSet = new HashMap<>();;


    public void AddGameData(String XMLPth) {
        gameList.add(gameLogic.readGameFile(XMLPth));
    }

    public List<GameData> getGameList() {
        return gameList;
    }

    public List<GameData> getActiveGamesList() {
        return gameList.stream()
                .filter(g-> g.getGameStatus().equals(GameStatus.ACTIVE))
                .collect(Collectors.toList());
    }

    public List<GameData> getPendingGamesList() {
        return gameList.stream()
                .filter(g-> g.getGameStatus().equals(GameStatus.PENDING))
                .collect(Collectors.toList());
    }

    public boolean addPlayerToGame(String playerName, String gameName, String teamName, String role) {

     /*   AtomicBoolean result = new AtomicBoolean(false);

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
        return result.get();*/

        Player player = playerSet.get(playerName);

        Optional<GameData> game = gameList.stream()
                .filter(g->g.getGameName().equals(gameName))
                .findFirst();

        if(game.isPresent()) {

            Optional<Team> team = game.get().getTeams().stream()
                    .filter(t -> t.getName().equals(teamName))
                    .findFirst();

            if (team.isPresent()) {

                if (team.get().addPlayer(playerName, Role.valueOf(role))) {
                    player.setGame(game.get(),team.get(),Role.valueOf(role));
                    checkGameReady(game.get());
                    return true;
                }
            }
        }
        return false;
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

    public TurnStatus playTurn(int cardGuess, GameSession game) {
   /*     int QUIT_TURN = 0;
        boolean isTeamPlaying = true;
        TurnStatus guessOutcome = null;


        try {
            guessOutcome = gameLogic.playTurn(cardGuess - 1, game); //-1 for board indexes
            //printTeamScore(engine.getCurrentTeam());

            //Check if game ended: by victory or black card.
            if (guessOutcome.getStatus().equals(TurnGuessStatus.BLACK) || guessOutcome.getStatus().getVictory()) {
                game.getPlayingTeam().setTeamOUtOfGame();
                isTeamPlaying = false;
            }
            //numCargdsToGuess--;
            //printTurnStauts(guessOutcome);
        } catch (CardAlreadyGuessed e) {
            System.out.println("The word: " + e.getWord() + " was already guessed.");
        } catch (CardSelectionOutOfBound e) {
            System.out.println(e.getMessage());
        }

        return guessOutcome;*/

        return gameLogic.playTurn(cardGuess, game);
    }

    public void turnEnd(GameSession game) {
        gameLogic.turnEnd(game);
    }

    public DTOActiveGame getActiveGameStatus(GameSession game) {
        return gameLogic.getActiveGameFromGame(game);
    }

    public Team getCurrentTeam(GameData game) {
        return gameLogic.getCurrentTeam(game.getActiveGame());
    }

    public boolean isPlayerTurn(String playerName, String currentTeam, Role playerRole) {
        Player player = playerSet.get(playerName);
        // check if players game at all
        return player.getTeam().getName().equals(currentTeam) && player.getRole().equals(playerRole);
    }

    public synchronized void addPlayer(String username) {
        playerSet.put(username,new Player(username));
    }

    public synchronized void removePlayer(String username) {
        //playerSet.remove(playerSet.stream().filter(s->s.getName().equals(username)).findFirst().get());
        playerSet.remove(playerSet.get(username));
    }

    public synchronized Set<String> getPlayers() {
        return Collections.unmodifiableSet(playerSet.keySet());
    }

    public boolean isPlayerExists(String username) {
        //return playerSet.stream().anyMatch(s->s.getName().equals(username));
        return playerSet.containsKey(username);
    }

    public GameData getPlayerGame(String name){
        return playerSet.get(name).getGame();
    }

}
