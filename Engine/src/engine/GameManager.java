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

    public void AddGameData(String XMLPth) {
        GameData gameToAdd = gameLogic.readGameFile(XMLPth);
        if (gameList.stream().noneMatch(g->g.getGameName().equals(gameToAdd.getGameName()))) {
            gameList.add(gameToAdd);
        }else{
            throw  new RuntimeException("Game name is taken.");
        }
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

    public TurnInfo singleTurn(Player player, int cardNum ,String definition) {

        if (!player.getGame().getGameStatus().equals(GameStatus.ACTIVE)) {
            throw new RuntimeException(GAME_INACTIVE);
        }

        GameData gameData = player.getGame();
        GameSession game = gameData.getGameSession();
        TurnInfo turnOutcome = null;

        if (!player.isPlayerTurn()) {
            throw new RuntimeException(NOT_PLAYER_TURN + "\nCurrently turn of team " + game.getPlayingTeam().getName() + ", role: " + game.getCurrentRole());
        }

        switch (game.getCurrentRole()) {
            case DEFINER:
                game.setDefinition(definition);
                game.setNumCardsToGuess(cardNum);
                game.setCurrentRole(Role.GUESSER);
                turnOutcome = new TurnInfo(gameLogic.getGameBoard(game));
                break;
            case GUESSER:

                if (cardNum == QUIT_TURN) {
                    gameLogic.turnEnd(game);
                    throw new RuntimeException(TURN_SKIPPED);
                }

                try {
                    TurnStatus guessOutcome = gameLogic.playTurn(cardNum - 1, game); //-1 for board indexes
                    DTOBoard board = gameLogic.getGameBoard(game); //Get updated board
                    String reasonGameOver = null;

                    if (guessOutcome.getStatus().equals(TurnGuessStatus.BLACK) || guessOutcome.getStatus().getVictory()) { //Check if team out of the game: by victory or black card.
                        String teamName = guessOutcome.getTeamWhoseCardItWas().getName();
                        Team team = game.getTeams().stream().filter(t -> t.getName().equals(teamName)).findFirst().get();

                        switch (guessOutcome.getStatus()) {
                            case BLACK:
                                reasonGameOver = "Black card- team lost.";
                                break;
                            case VICTORYCURRENTTEAM:
                                reasonGameOver = "Your team found all words and won!";
                                break;
                            case VICTORYOTHERTEAM:
                                reasonGameOver = "Team " + team.getName() + " found all words and won!";
                                break;
                        }

                        gameLogic.turnEnd(game);
                        game.removeTeam(team,reasonGameOver); //team removed from active game session, notifies player of reason

                        //check if game over - when only one team is left then notify the players of the remaining team that they lost, and remove them from game
                        if (game.getTeams().size()==1){
                            game.removeTeam(game.getTeams().stream().findFirst().get(),"Last team left - automatic lose");
                            gameData.gameEnded();// Clear the game session
                        }

                    } else {
                        game.decrementNumCardsToGuess();

                        if (game.getNumCardsToGuess() == 0) {
                            gameLogic.turnEnd(game);
                        }
                    }

                    turnOutcome = new TurnInfo(board, guessOutcome, reasonGameOver);

                } catch (CardAlreadyGuessed e) {
                    throw new RuntimeException("The word: " + e.getWord() + " was already guessed.");
                } catch (CardSelectionOutOfBound e) {
                    throw new RuntimeException(e.getMessage());
                }
                break;
        }
        return turnOutcome;
    }

    public TurnInfo getTurnInfo(Player player) {

        if (player.isGameOver()) {
            return new TurnInfo(player.getReasonGameOver());
        }

        GameSession game = player.getGame().getGameSession();

        if (!player.getGame().getGameStatus().equals(GameStatus.ACTIVE)) {
            throw new RuntimeException(GAME_INACTIVE);
        }

        if (!player.isPlayerTurn()) {
            throw new RuntimeException(NOT_PLAYER_TURN + "\nCurrently turn of team " + game.getPlayingTeam().getName() + ", role: " + game.getCurrentRole());
        }

        DTOBoard board = gameLogic.getGameBoard(game);

        return game.getCurrentRole().equals(Role.DEFINER)? new TurnInfo(board) : new TurnInfo(game.getDefinition(), game.getNumCardsToGuess(), board);

    }

    public DTOActiveGame getActiveGame(String playerName) {
        Player player =playerSet.get(playerName);
        if (player.isGameOver()){
            return new DTOActiveGame(player.getReasonGameOver());
        }else{
            return gameLogic.getActiveGameFromGame(player.getGame().getGameSession());
        }
    }


    public DTOActiveGame getActiveGameStatus(String gameName) {
        GameData game = gameList.stream()
                .filter(g->g.getGameName().equals(gameName))
                .findFirst()
                .get();
        if (game.getGameStatus().equals(GameStatus.ACTIVE)) {
            return gameLogic.getActiveGameFromGame(game.getGameSession());
        }else{
            throw new RuntimeException("Game Over.");
        }
    }

    public synchronized void addPlayer(String username) {
        playerSet.put(username,new Player(username));
    }

    public synchronized void removePlayer(String username) {
        playerSet.remove(username);
    }

    public boolean isPlayerExists(String username) {
        return playerSet.containsKey(username);
    }

    public Player getPlayer(String name){
        return playerSet.get(name);
    }

}
