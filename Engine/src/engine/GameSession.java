package engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameSession {

    public GameSession(int rows, int columns, List<Team> teams){
        board = new GameCard[rows][columns];
        this.teams = new ArrayList<>();
        teams.forEach(t->this.teams.add(new Team(t)));
        cardsInGame = new HashSet<>();
        currTeamIndex = 0;
        this.rows = rows;
        this.cols = columns;
    }

    private final List<Team> teams;
    public List<Team> getTeams() {
        return teams;
    }

    private final int rows;
    public int getRows() {
        return rows;
    }

    private final int cols;
    public int getColumns() {
        return cols;
    }

    private final GameCard[][] board;
    public GameCard[][] getBoard() {
        return board;
    }

    private final Set<GameCard> cardsInGame;
    public Set<GameCard> getCards() {
        return cardsInGame;
    }

    private int currTeamIndex;

    public Team getPlayingTeam() {
        return teams.get(currTeamIndex);
    }


    public void addCard(String word, Team team, boolean isBlack){
        cardsInGame.add(new GameCard(word, team, isBlack));
    }
    public void addCard(String word, boolean isBlack){
        cardsInGame.add(new GameCard(word, isBlack));
    }

    public void clearFinishedGame()
    {
        cardsInGame.clear();
        currTeamIndex = 0;
        teams.forEach(Team::clearTeam);
    }

    public void nextTeam(){
        do {
            currTeamIndex = (currTeamIndex == (teams.size() - 1)) ? 0 : currTeamIndex + 1;
        }while (!teams.get(currTeamIndex).isTeamPlaying());
    }

    public void removeTeam(Team team){
        teams.remove(team);
    }
}
