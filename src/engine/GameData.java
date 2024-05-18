package engine;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameData {

    public GameData(){
        teams = new HashSet<>();
    }

    private int cardsCount;
    public int getCardsCount() {return cardsCount;}

    private int blackCardsCount;
    public int getBlackCardsCount() { return blackCardsCount; }

    private int rows;
    public int getRows() { return rows;}

    private int columns;
    public int getColumns() {return columns;}

    private Set<Team> teams;
    public Set<Team> getTeams() {
        return teams;
    }

    public void addTeam(String name, int numCards) {
        Team t = new Team(name,numCards);
        this.teams.add(t);
    }

    public List<String> getBlackWordsDictionary() {
        return blackWordsDictionary;
    }

    public List<String> getWordsDictionary() {
        return wordsDictionary;
    }

    private List<String> wordsDictionary;
    private List<String> blackWordsDictionary;


    public void setGameData(List<String> allWords, List<String> allBlackWords, int numCards,
                            int numBlackCards, int rows, int columns){
        this.wordsDictionary = allWords;
        this.blackWordsDictionary = allBlackWords;
        this.cardsCount = numCards;
        this.blackCardsCount = numBlackCards;
        this.rows = rows;
        this.columns =columns;
    }
}
