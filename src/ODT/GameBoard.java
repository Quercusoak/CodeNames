package ODT;

import engine.GameCard;

import java.util.Set;

public class GameBoard {
    private GameCard[][] board;
    public GameCard[][] getBoard() {
        return board;
    }

    private Set<GameCard> cardsInGame;
    public Set<GameCard> getCards() {
        return cardsInGame;
    }

    private int rows;
    public int getRows() {
        return rows;
    }

    private int columns;
    public int getColumns() {
        return columns;
    }

    public GameBoard(GameCard[][] board,Set<GameCard> cards,int rows,int columns){
        this.board=board;
        cardsInGame=cards;
        this.rows=rows;
        this.columns=columns;
    }
}
