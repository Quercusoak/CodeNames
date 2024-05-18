package engine;


import engine.generated.ECNGame;
import jar.GameParams;

import java.util.Set;

public interface Engine
{
    //recieves full path, always displayed
    void readGameFile(String XMLpath);

    /*Only displayed when file successfully loaded*/
    GameParams displayGameParameters();

    void startGame();
}
