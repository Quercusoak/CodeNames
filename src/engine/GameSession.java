package engine;

import java.util.HashSet;
import java.util.Set;

public class GameSession {
    private String[][] board;
    private Set<GameCard> cardsInGame;
    private Set<Team> teams;

    public String[][] getBoard() {
        return board;
    }

    public Set<GameCard> getCards() {
        return cardsInGame;
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public GameSession(int rows, int columns, Set<Team> teams){
        board = new String[rows][columns];
        this.teams = teams;
        cardsInGame = new HashSet<>();
    }

    public void setCardsInGame(Set<GameCard> cards){cardsInGame = cards;}

    public void addCard(String word, Team team, boolean isBlack){
        cardsInGame.add(new GameCard(word, team, isBlack));
    }
    public void addCard(String word, boolean isBlack){
        cardsInGame.add(new GameCard(word, isBlack));
    }
}
