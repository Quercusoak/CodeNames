package exception;

public class NotEnoughCardsException extends RuntimeException{
    public int getSumCardsOfTeams() {
        return sumCardsOfTeams;
    }

    public int getNumCardsinGame() {
        return numCardsinGame;
    }

    private final int sumCardsOfTeams;
    private final int numCardsinGame;

    public NotEnoughCardsException(int sumCardsOfTeams, int numCardsinGame){
         this.sumCardsOfTeams = sumCardsOfTeams;
         this.numCardsinGame = numCardsinGame;
    }
}
