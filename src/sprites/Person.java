package sprites;
import main.Pair;
import main.Physics;
import main.XML;
import main.XMLNode;
import sprites.rooms.*;

import java.util.ArrayList;
import java.util.List;
import java.awt.Image;
import java.awt.Graphics;
import java.util.HashMap;


public class Person extends Sprite {
    protected int health;
    protected int maxHealth;
    public static ArrayList<Person> people;
    protected double velocityX;
    protected double velocityY;
    protected int roomIn;
    protected boolean isSpeaking = false;//these 3 are for
    protected boolean isMoving = false;//directives
    protected boolean isAttacking = false;//TODO: This is feeling a bit spaghetti-y.  Investigate consolidation.

    //these are called when they have completed a directive task
    public void stopSpeaking() {isSpeaking=false;}

    //changes velocity by (dx,dy)
    public void move(double dx,double dy) {
        if (Room.isMoveableLocation((positionX+dx),(positionY+dy))) {
            velocityX+=dx;
            velocityY+=dy;
        }
    }

    //like move but caps velocity
    public void moveCapped(double dx,double dy,double magcap) {
        move(dx,dy);
        double mag = Physics.magnitude(velocityX,velocityY);
        if (mag>magcap) {
            velocityX*=magcap/mag;
            velocityY*=magcap/mag;
        }
    }

    //changes position by (dx,dy) if it is a valid move
    private void rawMove(double dx,double dy) {
        if (Room.isMoveableLocation(positionX+dx,positionY+dy)) {
            positionX+=dx;
            positionY+=dy;
        }
    }
    //sets position to (nx,ny), does not care if it is a valid move or not
    public void setPosition(double nx,double ny) {
        positionX = nx;
        positionY = ny;
    }
    public int getHealth() {return health;}
    public int getMaxHealth() {return maxHealth;}
    public double getHealthPercentage() {return (1.0*health)/maxHealth;}

    //makes the health percentage look lower than it really is.  Good for making the player
    //feel more adrenaline and whatnots.  Currently I just square the value, but any
    //fudge-function is fine as long as fudge(0) = 0 and fudge(1) = 1 and fudge(x)<=x for all x on [0,1].
    public double getFudgedHealthPercentage() {return Math.pow(getHealthPercentage(),2);}

    public void takeDamage(int damage) {
        health-=damage;
    }

    public Person(int px, int py, HashMap<String,Image[]> tlist) {
        super(px,py,32,32,tlist);
        people.add(this);
    }

    static XMLNode npcXMLData;
    public static void init() {
        people = new ArrayList<Person>();
        npcXMLData = (new XML("Resources/persondata.xml")).getRoot();
        //initialize the player
        XMLNode playerdata = npcXMLData.getChildWithKey("Player");
        Player.init(playerdata);
        //initialize the npcs
        List<XMLNode> npcdata = npcXMLData.getChildrenWithKey("NPC");
        for (XMLNode node : npcdata) {
            XMLNode pos = node.getChildWithKey("StartingPosition");
            int posx = Integer.parseInt(pos.getAttributeWithName("x"));
            int posy = Integer.parseInt(pos.getAttributeWithName("y"));
            int room = Integer.parseInt(pos.getAttributeWithName("room"));
            String n = node.getAttributeWithName("name");
            String xmlroutine = node.getChildWithKey("Routine").getAttributeWithName("file");
            HashMap<String,Image[]> hashmapToFill = new HashMap<String,Image[]>();
            Sprite.parseSpriteList(node,hashmapToFill);
            NPC npcConcerned = new NPC(posx,posy,hashmapToFill,n,room,xmlroutine);
        }
    }

    //draws all people
    public static void drawPeople(int offsetX,int offsetY,Graphics g) {
        for (Person p : people) {
            if (p.roomIn==Room.getRoomID()) {//only draw if they're in the current room
                p.draw(offsetX, offsetY, g);
            }
        }
    }

    //update function, called every tick
    public static void staticUpdate() {
        for (Person p : people) {
            p.update();
        }
    }

    protected void update() {
        //normalize their velocity
        double magnitude = Physics.magnitude(velocityX,velocityY);
        if (magnitude>5) {
            Pair<Double,Double> normalized = Physics.normalize(velocityX,velocityY);
            velocityX = normalized.x*5;
            velocityY = normalized.y*5;
        }
        else {
            velocityX *= 0.95;
            velocityY *= 0.95;
        }
        rawMove(velocityX,velocityY);
        if (magnitude<0.1) {//if mag < 0.1, it's not really moving.  Just stop it.
            velocityX = 0;
            velocityY = 0;
            changeStateIfNeeded("Default");
        }
        else {
            changeStateIfNeeded("Moving");
        }
    }

    //returns true if a Sprite with the name 's' can be seen by this person.
    public boolean canSee(String s) {
        List<Sprite> candidates = Sprite.query(s);
        //spr is what we're checking for!
        //TODO actually check visibility, currently its just checking if its in a small radius
        for (Sprite candidate : candidates) {
            if (Physics.magnitude(positionX - candidate.positionX, positionY - candidate.positionY) < Tile.TILE_WIDTH_IN_PIXELS * 3) {
                return true;
            }
        }
        return false;
    }

}
