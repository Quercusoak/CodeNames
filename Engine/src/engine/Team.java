package engine;

import dto.Role;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Team implements Serializable {
    public Team(String name, int numCards, int definers, int guessers){
        this.name=name;
        numberOfCards=numCards;
        numRequiredDefiners = definers;
        numRequiredGuessers = guessers;
        score = 0;
        numTurnsPlayed = 0;
        numRegisteredDefiners = 0;
        numRegisteredGuessers = 0;
        players = new HashMap<>(numRequiredDefiners+numRequiredGuessers);
    }

    private final String name;
    public String getName() {
        return name;
    }

    private final int numberOfCards;
    public int getNumberOfCards() {
        return numberOfCards;
    }

    private final int numRequiredDefiners;
    public int getNumRequiredDefiners() {return numRequiredDefiners;}

    private final int numRequiredGuessers;
    public int getNumRequiredGuessers() {return numRequiredGuessers;}

    private int numRegisteredDefiners;
    public int getNumRegisteredDefiners() {return numRegisteredDefiners;}

    private int numRegisteredGuessers;
    public int getNumRegisteredGuessers() {return numRegisteredGuessers;}

    private int score;
    public int getScore(){
        return score;
    }

    public void addPoint(){
        score++;
    }

    private int numTurnsPlayed;
    public int getNumTurnsPlayed() {
        return numTurnsPlayed;
    }
    public void incTurnCounter() {
        numTurnsPlayed++;
    }

    private final Map<String, Role> players;

    public boolean addPlayer(String name, Role role){
        boolean playerAdded = false;

        if (players.size()>=numRequiredDefiners+numRequiredGuessers){
            return false;
        }

        switch (role.getNumber()){
            case 1:
                if (numRegisteredDefiners<numRequiredDefiners){
                    players.put(name, role);
                    numRegisteredDefiners++;
                    playerAdded = true;
                }
                break;
            case 2:
                if (numRegisteredGuessers<numRequiredGuessers){
                    players.put(name, role);
                    numRegisteredGuessers++;
                    playerAdded = true;
                }
                break;
            default:
                break;
        }
        return playerAdded;
    }
}
