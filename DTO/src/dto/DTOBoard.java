package dto;

import java.util.ArrayList;
import java.util.List;

public class DTOBoard {
    private final List<DTOCard> cards;

    public List<DTOCard> getCards() {
        return cards;
    }

    private final int rows;

    public int getRows() {
        return rows;
    }

    private final int columns;

    public int getColumns() {
        return columns;
    }

    public DTOBoard(List<DTOCard> cards, int rows, int columns) {
        this.cards = cards;
        this.rows = rows;
        this.columns = columns;
    }
}