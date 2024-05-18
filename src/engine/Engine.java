package engine;


import jar.GameParams;

public interface Engine
{
    /*recieves full path, always displayed*/
    void readGameFile(String XMLpath);

    /*Only displayed when file successfully loaded*/
    GameParams displayGameParameters();

    void startGame();
}
