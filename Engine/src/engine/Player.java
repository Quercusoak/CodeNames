package engine;

import dto.Role;

public class Player {
    private final String name;
    private GameData game;
    private Team team;
    private Role role;
    private boolean isGameOver;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {return name;}
    public GameData getGame() {return game;}
    public Team getTeam() {return team;}
    public Role getRole() {return role;}

    public void setGame(GameData game, Team team, Role role) {
        this.game = game;
        this.team = team;
        this.role = role;
        isGameOver = false;
        reasonGameOver=null;
    }

    public boolean isPlayerTurn(){
        return game.getGameSession().getPlayingTeam().getName().equals(team.getName()) && game.getGameSession().getCurrentRole().equals(role);
    }

    public void removeGame(){
        this.game = null;
        this.team = null;
        this.role = null;
    }

    private String reasonGameOver;

    public void setGameOver(String reason){
        isGameOver = true;
        reasonGameOver = reason;
    }

    public boolean isGameOver(){
        return isGameOver;
    }

    public String getReasonGameOver(){ return reasonGameOver; }
}
