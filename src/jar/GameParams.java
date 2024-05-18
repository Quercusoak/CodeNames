package jar;

import engine.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameParams {
    public long getGameWordsPossible() {
        return gameWordsPossible;
    }

    public long getBlackWordsPossible() {
        return blackWordsPossible;
    }

    public int getNumWords() {
        return numWords;
    }

    public Map<String, Integer> getTeams() {
        return teams;
    }

    private long gameWordsPossible;
    private long blackWordsPossible;
    private int numWords;
    private Map<String, Integer> teams = new HashMap<>();


    public GameParams(long gameWordsPossible, long blackWordsPossible, int numWords, Set<Team> teams){
        this.gameWordsPossible = gameWordsPossible;
        this.blackWordsPossible =blackWordsPossible;
        this.numWords = numWords;
        teams.forEach(team ->this.teams.put(team.getName(),team.getNumberOfCards()));
    }
}
