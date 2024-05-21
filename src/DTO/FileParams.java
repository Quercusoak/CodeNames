package DTO;

import engine.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileParams {
    public int getGameWordsPossible() {
        return gameWordsPossible;
    }

    public int getBlackWordsPossible() {
        return blackWordsPossible;
    }

    public int getNumCards() {
        return numCards;
    }

    public Map<String, Integer> getTeams() {
        return teams;
    }

    public int getNumBlackCards() {
        return numBlackCards;
    }

    private int gameWordsPossible;
    private int blackWordsPossible;
    private int numCards;
    private int numBlackCards;
    private Map<String, Integer> teams = new HashMap<>();


    public FileParams(int gameWordsPossible, int blackWordsPossible, int numWords,int numBlack, List<Team> teams){
        this.gameWordsPossible = gameWordsPossible;
        this.blackWordsPossible =blackWordsPossible;
        this.numCards = numWords;
        numBlackCards =numBlack;
        teams.forEach(team ->this.teams.put(team.getName(),team.getNumberOfCards()));
    }
}
