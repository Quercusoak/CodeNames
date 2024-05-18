package engine;


import ODT.FileParams;

public interface Engine
{
    /*recieves full path, always displayed*/
    void readGameFile(String XMLpath);

    /*Only displayed when file successfully loaded*/
    FileParams displayGameParameters();

    void startGame();

    void playTurn();
}
