package engine;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameData {

    public GameData(Set<String> allCards, Set<String> allBlackCards){
        teams = new HashSet<>();
        allPossibleWords = allCards;
        allPossibleBlackWords = allBlackCards;
    }

    private int cardsCount;
    public int getCardsCount() {return cardsCount;}
    public void setCardsCount(int cards_count){ cardsCount = cards_count;}

    private int blackCardsCount;
    public int getBlackCardsCount() { return blackCardsCount; }
    public void setBlackCardsCount(int blackCardsCount) {this.blackCardsCount = blackCardsCount; }

    private int rows;
    public int getRows() { return rows;}
    public void setRows(int rows) {this.rows = rows;}

    private int columns;
    public int getColumns() {return columns;}
    public void setColumns(int columns) {this.columns = columns; }

    private Set<Team> teams;
    public Set<Team> getTeams() {
        return teams;
    }

    public void addTeam(String name, int numCards) {
        Team t = new Team(name,numCards);
        this.teams.add(t);
    }

    public Set<String> getAllPossibleBlackWords() {
        return allPossibleBlackWords;
    }

    public void setAllPossibleBlackWords(Set<String> allPossibleBlackWords) {
        this.allPossibleBlackWords = allPossibleBlackWords;
    }

    public Set<String> getAllPossibleWords() {
        return allPossibleWords;
    }

    public void setAllPossibleWords(Set<String> allPossibleWords) {
        this.allPossibleWords = allPossibleWords;
    }

    private Set<String> allPossibleWords;
    private Set<String> allPossibleBlackWords;

    private long numAllWords;
    private long numAllBlackWords;

    public long getNumAllWords() {
        return numAllWords;
    }

    public void setNumAllWords(long numAllWords) {
        this.numAllWords = numAllWords;
    }

    public long getNumAllBlackWords() {
        return numAllBlackWords;
    }

    public void setNumAllBlackWords(long numAllBlackWords) {
        this.numAllBlackWords = numAllBlackWords;
    }

    public void setGameData(int numPossibleWords, int numPossibleBlackWords, int numCards,
                            int numBlackCards, int rows, int columns){
        setNumAllWords(numPossibleWords);
        setNumAllBlackWords(numPossibleBlackWords);
        setCardsCount(numCards);
        setBlackCardsCount(numBlackCards);
        setRows(rows);
        setColumns(columns);
    }

    /////////////////
    public List<String> wordDictionary;
}
