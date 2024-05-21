package DTO;

import engine.GameCard;

import java.util.ArrayList;
import java.util.List;

public class DTOBoard {
    private List<DTOCard> cards;
    public List<DTOCard> getCards() {
        return cards;
    }

    private int rows;
    public int getRows() {
        return rows;
    }

    private int columns;
    public int getColumns() {
        return columns;
    }

    public DTOBoard(GameCard[][] board, int rows, int columns,int numCardsInBoard){
        cards = new ArrayList<>();
        for (int i=0; i< Math.min((rows*columns),numCardsInBoard); i++){
            cards.add(i,new DTOCard(board[i/columns][i%columns]));
        }
        this.rows=rows;
        this.columns=columns;
    }
}
