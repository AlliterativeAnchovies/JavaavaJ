package sprites;

import main.XMLNode;
import main.XML;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Attack extends Sprite {
    protected static HashMap<String, XMLNode> attackDatabase;
    protected int damage;
    private static ArrayList<Attack> allAttacks;

    /**
     * Getter for allAttacks
     * @return allAttacks, a list of all attacks.
     */
    public static ArrayList<Attack> getAllAttacks() {
        return allAttacks;
    }

    /**
     * This is internally how the object is created.  Because you have to call 'super' at the very start,
     * it requires some preprocessing, which is why this method is private and you should use the static
     * method createAttack instead
     * @param x location of sprite (x)
     * @param y location of sprite (y)
     * @param tlist pre-parsed sprite list
     * @param damage how much damage is done by this attack
     */
    private Attack(double x,double y,HashMap<String,Image[]> tlist,int damage) {
        super(x,y,16,16,tlist);
        allAttacks.add(this);
    }

    /**
     * This is the real constructor of the Attack class.
     * @param x location of sprite (x)
     * @param y location of sprite (y)
     * @param id name of sprite - in the form of a filepath, like "Attacks/fireball.xml".
     * @return
     */
    public static Attack createAttack(double x,double y,String id) {
        id = "Resources/"+id;
        if (attackDatabase.get(id)==null) {
            //initialize the attack from the node
            XML tree = new XML(id);
            attackDatabase.put(id,tree.getRoot());
        }
        HashMap<String,Image[]> tlist = new HashMap<>();
        parseSpriteList(attackDatabase.get(id),tlist);
        int tobedamage = Integer.parseInt(attackDatabase.get(id).getChildWithKey("Stats").getAttributeWithName("damage"));
        String tobename = attackDatabase.get(id).getChildWithKey("Stats").getAttributeWithName("name");
        Attack toReturn = new Attack(x,y,tlist,tobedamage);
        toReturn.name = tobename;
        return toReturn;
    }

    /**
     * Sets this class up for operation
     */
    public static void init() {
        attackDatabase = new HashMap<>();
        allAttacks = new ArrayList<>();
    }

    /**
     * Draws all attacks
     * @param offsetX This is the offset of the screen.  Used to translate screen coordinates to game coordinates.
     * @param offsetY See offsetX ya dummy.
     * @param g The rendering context.
     */
    public static void drawAttacks(int offsetX,int offsetY,Graphics g) {
        for (Attack a : allAttacks) {
            a.draw(offsetX,offsetY,g);
        }
    }
}
