package engine;

import dto.Role;

public class Player {
    private String name;
    private GameData game;
    private Team team;
    private Role role;

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
    }

    public void removeGame(){
        this.game = null;
        this.team = null;
        this.role = null;
    }
}
