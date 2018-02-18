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
    private static ArrayList<Person> people;
    protected double velocityX;
    protected double velocityY;
    protected int roomIn;
    protected boolean isSpeaking = false;//these 2 are for
    protected boolean isMoving = false;//directives

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
        List<Sprite> tocheck = new ArrayList<>();
        String nameToSearch = "";
        //do a quick check if they're searching for the player, as this is probably a common
        //trigger and we don't want to have to search through an array every time.
        if (s.equals("person:player") || s.equals("player:player") || s.equals("player") || s.equals("sprite:player") || s.equals("player:@")) {
            tocheck.add(Player.getPlayer());
            nameToSearch = "player";
        }
        else {
            String[] splitstr = s.split(":");
            //tocheck[0] contains type of thing to check for: person, sprite, tile, item, npc
            //tocheck[1] contains the name of the person/thing.  Special case is the character '@'
            //which indicates that any/all that have the correct type are valid.
            //if tocheck.length == 1, that means the person who wrote the xml doc forgot to put a qualifier.
            //we will default to 'person' if so.
            if (splitstr.length == 1) {
                nameToSearch = splitstr[0];
                tocheck.addAll(Person.people);
            }
            else {
                nameToSearch = splitstr[1];
                if (splitstr[0].equals("person")) {
                    tocheck.addAll(Person.people);
                }
                else if (splitstr[0].equals("npc")) {
                    tocheck.addAll(NPC.allNPCs);
                }
                else if (splitstr[0].equals("sprite")) {
                    tocheck.addAll(Sprite.allSprites);
                }
                else if (splitstr[0].equals("tile")) {
                    tocheck.addAll(Tile.allTiles);
                }
                else {
                    System.out.println("There be something weird going on...  what's a '"+splitstr[0]+"'?");
                }
            }
        }
        for (Sprite spr : tocheck) {
            if (nameToSearch=="@" || spr.name.equals(nameToSearch)) {
                //spr is what we're checking for!
                //TODO actually check visibility, currently its just checkin' if its in a small radius
                if (Physics.magnitude(positionX-spr.positionX,positionY-spr.positionY)<Tile.TILE_WIDTH_IN_PIXELS*3) {
                    return true;
                }
            }
        }
        return false;
    }

}
