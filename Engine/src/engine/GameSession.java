package engine;

import dto.GameStatus;
import dto.Role;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameSession {

    public GameSession(int rows, int columns,List<Team> teams){
        board = new GameCard[rows][columns];
        this.teams = new ArrayList<>();
        cardsInGame = new HashSet<>();
        currTeamIndex = 0;
        this.rows = rows;
        this.columns = columns;
        gameStatus = GameStatus.PENDING;
        this.teams.addAll(teams);
    }

    private List<Team> teams;
    public List<Team> getTeams() {
        return teams;
    }

    private final int rows;
    public int getRows() {
        return rows;
    }

    private final int columns;
    public int getColumns() {
        return columns;
    }

    private final GameCard[][] board;
    public GameCard[][] getBoard() {
        return board;
    }

    private Set<GameCard> cardsInGame;
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


    public void nextTeam() {
        currTeamIndex = (currTeamIndex >= (teams.size() - 1)) ? 0 : currTeamIndex + 1;
    }

    private GameStatus gameStatus;
    public GameStatus getGameStatus() {return gameStatus;}

    public void startGame() {
        this.gameStatus=GameStatus.ACTIVE;
    }

    private String definition;
    private int numCardsToGuess;
    private Role currentRole = Role.DEFINER;

    public String getDefinition() {
        return definition;
    }
    public void setDefinition(String definition) { this.definition = definition; }

    public int getNumCardsToGuess() { return numCardsToGuess; }
    public void setNumCardsToGuess(int numCardsToGuess) { this.numCardsToGuess = numCardsToGuess; }
    public void decrementNumCardsToGuess(){ numCardsToGuess--;}

    public Role getCurrentRole() {
        return currentRole;
    }
    public void setCurrentRole(Role currentRole) {
        this.currentRole = currentRole;
    }

    public void removeTeam(Team team, String reasonTeamOut){
        team.setTeamOUtOfGame(reasonTeamOut);

        Team tmp;
        if (getPlayingTeam().equals(team)){
            tmp = teams.get((currTeamIndex >= (teams.size() - 1)) ? 0 : currTeamIndex + 1); //get next team
        }else{
            tmp = teams.get(currTeamIndex);
        }

        teams.remove(team);
        currTeamIndex = teams.indexOf(tmp);
    }

    public void clearSession(List<Team> teams){
        cardsInGame.clear();
        currTeamIndex = 0;
        this.teams.clear();
        this.teams.addAll(teams);
        gameStatus = GameStatus.PENDING;
    }
}
