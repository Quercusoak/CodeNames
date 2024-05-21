package engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameSession {
    private GameCard[][] board;
    private Set<GameCard> cardsInGame;
    private List<Team> teams;
    private int currTeamIndex;

    public Team getPlayingTeam() {
        return teams.get(currTeamIndex);
    };

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
        this.teams = new ArrayList<>();
        teams.forEach(t->this.teams.add(new Team(t.getName(),t.getNumberOfCards())));
        cardsInGame = new HashSet<>();
        currTeamIndex = 0;
    }

    public void addCard(String word, Team team, boolean isBlack){
        cardsInGame.add(new GameCard(word, team, isBlack));
    }
    public void addCard(String word, boolean isBlack){
        cardsInGame.add(new GameCard(word, isBlack));
    }

    public void nextTeam(){
        currTeamIndex = (currTeamIndex == (teams.size()-1))? 0: currTeamIndex+1;
    }
}
