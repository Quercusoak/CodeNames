package ODT;

import engine.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileParams {
    public int getGameWordsPossible() {
        return gameWordsPossible;
    }

    public int getBlackWordsPossible() {
        return blackWordsPossible;
    }

    public int getNumWords() {
        return numWords;
    }

    public Map<String, Integer> getTeams() {
        return teams;
    }

    private int gameWordsPossible;
    private int blackWordsPossible;
    private int numWords;
    private Map<String, Integer> teams = new HashMap<>();


    public FileParams(int gameWordsPossible, int blackWordsPossible, int numWords, List<Team> teams){
        this.gameWordsPossible = gameWordsPossible;
        this.blackWordsPossible =blackWordsPossible;
        this.numWords = numWords;
        teams.forEach(team ->this.teams.put(team.getName(),team.getNumberOfCards()));
    }
}
