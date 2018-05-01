package g7;

import battleship.interfaces.BattleshipsPlayer;
import tournament.player.PlayerFactory;

public class G7 implements PlayerFactory<BattleshipsPlayer>
{

    public G7(){}
    
    
    @Override
    public BattleshipsPlayer getNewInstance()
    {
        return new HotShotPlayer();
    }

    @Override
    public String getID()
    {
        return "G7";
    }

    @Override
    public String getName()
    {
        return "HotShot!";
    }

    @Override
    public String[] getAuthors()
    {
        String[] res = {"Magnus", "Rasmus", "Christian", "Mathias"};
        return res;
    }
    
}

