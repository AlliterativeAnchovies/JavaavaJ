package sprites;

import main.XMLNode;
import sprites.Person;

import java.awt.*;
import java.util.HashMap;

public class Player extends Person {
    private static Player player;

    private Player(int px,int py,HashMap<String,Image[]> tlist) {
        super(px,py,tlist);
        roomIn = 0;
        name = "player";
    }

    public static Player getPlayer() {
        return player;
    }

    public static void init(XMLNode playerdata) {
        HashMap<String,Image[]> hashmapToFill = new HashMap<String,Image[]>();
        Sprite.parseSpriteList(playerdata,hashmapToFill);
        player = new Player(32,32,hashmapToFill);
    }
}
