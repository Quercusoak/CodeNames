package dto;

public enum Role {
    DEFINER(1, "Definer"),
    GUESSER(2, "Guesser");

    final int number;
    final String text;

    Role(int number, String text) {
        this.number = number;
        this.text = text;
    }

    public String toString() {
        return text;
    }

    public int getNumber() {
        return number;
    }
}
