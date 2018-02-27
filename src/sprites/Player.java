package sprites;

import main.XMLNode;
import sprites.Person;

import java.awt.*;
import java.util.HashMap;

public class Player extends Person {
    private static Player player;

    /**
     * A hidden constructor for Player, because player is a singleton and so should be created
     * along with the rest of the singleton gang inside the static init method.
     * @param px their position
     * @param py their weight in gold
     * @param tlist a pre-parsed sprite list
     */
    private Player(int px,int py,HashMap<String,Image[]> tlist) {
        super(px,py,tlist);
        roomIn = 0;
        name = "player";
    }

    /**
     * When I was younger, I didn't know how to tie my shoes.
     * However, I learned eventually.
     * In fact, I think I've known for over half my life now.
     * Which is sad, when you think about it.
     * Life was simpler when I couldn't tie my shoes.
     * Because it was no longer stressful to walk around with untied shoes -
     * it didn't matter when I tied them, because I couldn't tie them.
     * Now it's a very nervewracking experience, because it's hard to find
     * a good place to stop and tie them.
     * @return the player
     */
    public static Player getPlayer() {
        return player;
    }

    /**
     * Inits everything a player could possibly need
     * @param playerdata The data of the player expressed in XML format.
     */
    public static void init(XMLNode playerdata) {
        HashMap<String,Image[]> hashmapToFill = new HashMap<String,Image[]>();
        Sprite.parseSpriteList(playerdata,hashmapToFill);
        player = new Player(32,32,hashmapToFill);
    }
}
