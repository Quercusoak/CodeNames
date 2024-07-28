package engine;
import dto.*;
import exception.CardAlreadyGuessed;
import exception.CardSelectionOutOfBound;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {
    private final GameLogic gameLogic = new GameLogic();
    private final List<GameData> gameList = new ArrayList<>();
    private final Map<String, Player> playerSet = new HashMap<>();

    private final String NOT_PLAYER_TURN = "Not your turn yet.";
    private final String GAME_INACTIVE = "Can't play turn- game is pending.";
    private final String TURN_SKIPPED = "Team stopped guessing.";
    private final static Integer QUIT_TURN = 0;
//    private final Object gameLock = new Object();

    public void AddGameData(String XMLPth) {
        gameList.add(gameLogic.readGameFile(XMLPth));
    }

    public List<GameData> getGameList() {
        return gameList;
    }

    public List<GameData> getActiveGamesList() {
        return gameList.stream()
                .filter(g-> g.getGameSession().getGameStatus().equals(GameStatus.ACTIVE))
                .collect(Collectors.toList());
    }

    public List<GameData> getPendingGamesList() {
        return gameList.stream()
                .filter(g-> g.getGameSession().getGameStatus().equals(GameStatus.PENDING))
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

                if (team.get().addPlayer(player, Role.valueOf(role))) {
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

    public TurnStatus singleTurn(Player player, int cardNum ,String definition) {

        if (player.getGame().getGameStatus().equals(GameStatus.ACTIVE)) {

            GameSession game = player.getGame().getGameSession();

            if (player.isPlayerTurn()) {
                TurnStatus guessOutcome = null;

                switch (game.getCurrentRole()) {
                    case DEFINER:
                        game.setDefinition(definition);
                        game.setNumCardsToGuess(cardNum);
                        game.setCurrentRole(Role.GUESSER);
                        break;
                    case GUESSER:

                        if (cardNum == QUIT_TURN) {
                            gameLogic.turnEnd(game);
                            throw new RuntimeException(TURN_SKIPPED);
                        }

                        try {
                            guessOutcome = gameLogic.playTurn(cardNum - 1, game); //-1 for board indexes

                            /*Check if team out of the game: by victory or black card.*/
                            if (guessOutcome.getStatus().equals(TurnGuessStatus.BLACK) || guessOutcome.getStatus().getVictory()) {
                                /*Team tmp = game.getPlayingTeam();
                                game.nextTeam();
                                game.getTeams().remove(tmp);*/ //team removed from active game session

                                String teamName = guessOutcome.getTeamWhoseCardItWas().getName();
                                Team team =game.getTeams().stream().filter(t->t.getName().equals(teamName)).findFirst().get();
                                String reasonGameOver = "placeholder";

                                switch (guessOutcome.getStatus()){
                                    case BLACK:
                                        reasonGameOver = "Black card- team lost.";
                                        break;
                                    case VICTORYCURRENTTEAM:
                                        reasonGameOver =  "Your team found all words and won!";
                                        break;
                                    case VICTORYOTHERTEAM:
                                        reasonGameOver =  "Team "+team.getName()+" found all words and won!";
                                        break;
                                }

                                team.setTeamOUtOfGame(reasonGameOver);

                                //something here doesnt add up
                                if (game.getTeams().stream().filter(Team::isTeamPlaying).count() == 1){
                                    reasonGameOver = "Last team left - automatic lose";

                                }
                                

                                guessOutcome.setGameOver(reasonGameOver);

                                game.setCurrentRole(Role.DEFINER);
                            }
                            game.decrementNumCardsToGuess();

                            if (game.getNumCardsToGuess() == 0) {
                                game.setCurrentRole(Role.DEFINER);
                                gameLogic.turnEnd(game);
                            }

                        } catch (CardAlreadyGuessed e) {
                            throw new RuntimeException("The word: " + e.getWord() + " was already guessed.");
                        } catch (CardSelectionOutOfBound e) {
                            throw new RuntimeException(e.getMessage());
                        }
                        break;
                }

                return guessOutcome;

            } else {
                throw new RuntimeException(NOT_PLAYER_TURN+"\nCurrently turn of team "+game.getPlayingTeam().getName()+", role: "+game.getCurrentRole());
            }
        } else {
            throw new RuntimeException(GAME_INACTIVE);
        }
    }

    /*public TurnStatus playTurn(int cardGuess, GameSession game) {
   *//*     int QUIT_TURN = 0;
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

        return guessOutcome;*//*

        return gameLogic.playTurn(cardGuess, game);
    }

    public void turnEnd(GameSession game) {
        gameLogic.turnEnd(game);
    }*/

    public DTOBoard getBoard(GameSession game) {
        return gameLogic.getGameBoard(game);
    }

    public TurnInfo getTurnInfo(Player player) {

        GameSession game = player.getGame().getGameSession();

        if (player.getGame().getGameStatus().equals(GameStatus.ACTIVE)) {

            if (player.isPlayerTurn()) {
                TurnInfo turnInfo = null;
                DTOBoard board = gameLogic.getGameBoard(game);

                switch (game.getCurrentRole()) {
                    case DEFINER:
                        turnInfo = new TurnInfo(board);
                        break;
                    case GUESSER:
                        turnInfo = new TurnInfo(game.getDefinition(), game.getNumCardsToGuess(), board);
                        break;
                }
                return turnInfo;
            }else {
                throw new RuntimeException(NOT_PLAYER_TURN+"\nCurrently turn of team "+game.getPlayingTeam().getName()+", role: "+game.getCurrentRole());
            }
        } else {
            throw new RuntimeException(GAME_INACTIVE);
        }
    }

    public DTOActiveGame getActiveGame(String playerName) {
        GameData game = playerSet.get(playerName).getGame();
        return game==null? null: gameLogic.getActiveGameFromGame(game.getGameSession());
    }


    public boolean isThereOneTeamLeft(GameSession game) {
        return game.getTeams().size() == 1;
    }

    public DTOActiveGame getActiveGameStatus(GameSession game) {
        return gameLogic.getActiveGameFromGame(game);
    }

    /*public Team getCurrentTeam(GameData game) {
        return gameLogic.getCurrentTeam(game.getGameSession());
    }

    public boolean isPlayerTurn(String playerName, GameSession game) {
        Player player = playerSet.get(playerName);
        return player.getTeam().equals(game.getPlayingTeam()) && player.getRole().equals(game.getCurrentRole());
    }

    public boolean isPlayerTurn(String playerName, String currentTeam, Role playerRole) {
        Player player = playerSet.get(playerName);
        // check if players game at all
        return player.getTeam().getName().equals(currentTeam) && player.getRole().equals(playerRole);
    }*/

    public synchronized void addPlayer(String username) {
        playerSet.put(username,new Player(username));
    }

    public synchronized void removePlayer(String username) {
        //playerSet.remove(playerSet.stream().filter(s->s.getName().equals(username)).findFirst().get());
        playerSet.remove(playerSet.get(username));
    }

//    public synchronized Set<String> getPlayers() {
//        return Collections.unmodifiableSet(playerSet.keySet());
//    }

    public boolean isPlayerExists(String username) {
        //return playerSet.stream().anyMatch(s->s.getName().equals(username));
        return playerSet.containsKey(username);
    }

    public GameData getPlayerGame(String name){
        return playerSet.get(name).getGame();
    }

    public Player getPlayer(String name){
        return playerSet.get(name);
    }

}
