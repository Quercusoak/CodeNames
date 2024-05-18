package engine;

import java.util.HashSet;
import java.util.Set;

public class GameSession {
    private GameCard[][] board;
    private Set<GameCard> cardsInGame;
    private Set<Team> teams;

    public GameCard[][] getBoard() {
        return board;
    }

    public Set<GameCard> getCards() {
        return cardsInGame;
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public GameSession(int rows, int columns, Set<Team> teams){
        board = new GameCard[rows][columns];
        this.teams = teams;
        cardsInGame = new HashSet<>();
    }

    public void addCard(String word, Team team, boolean isBlack){
        cardsInGame.add(new GameCard(word, team, isBlack));
    }
    public void addCard(String word, boolean isBlack){
        cardsInGame.add(new GameCard(word, isBlack));
    }
}
