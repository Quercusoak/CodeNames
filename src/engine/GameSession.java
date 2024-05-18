package engine;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameSession {
    private GameCard[][] board;
    private Set<GameCard> cardsInGame;
    private List<Team> teams;
    public Team currTeamTurn;

    public GameCard[][] getBoard() {
        return board;
    }

    public Set<GameCard> getCards() {
        return cardsInGame;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public GameSession(int rows, int columns, List<Team> teams){
        board = new GameCard[rows][columns];
        this.teams = teams;
        cardsInGame = new HashSet<>();
        currTeamTurn = teams.get(0);
    }

    public void addCard(String word, Team team, boolean isBlack){
        cardsInGame.add(new GameCard(word, team, isBlack));
    }
    public void addCard(String word, boolean isBlack){
        cardsInGame.add(new GameCard(word, isBlack));
    }
}
