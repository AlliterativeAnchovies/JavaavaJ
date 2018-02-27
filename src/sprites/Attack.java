package sprites;

import main.XMLNode;
import main.XML;

import java.awt.*;
import java.util.HashMap;

public class Attack extends Sprite {
    protected static HashMap<String, XMLNode> attackDatabase;
    protected int damage;
    private Attack(double x,double y,HashMap<String,Image[]> tlist,int damage) {
        super(x,y,16,16,tlist);
    }
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
    public static void init() {
        attackDatabase = new HashMap<>();
    }
}
