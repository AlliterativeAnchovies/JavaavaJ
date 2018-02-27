package sprites;
import main.Pair;
import main.Physics;
import main.XML;
import main.XMLNode;
import org.jetbrains.annotations.Contract;
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
    protected boolean isSpeaking = false;//these 3 are for
    protected boolean isMoving = false;//directives
    protected boolean isAttacking = false;//TODO: This is feeling a bit spaghetti-y.  Investigate consolidation

    /**
     * gets a list of all people in the game
     * @return the meaning of life
     */
    @Contract(pure = true)
    public static ArrayList<Person> getPeople() {
        return people;
    }

    /**
     * Have you ever just wanted someone to SHUT UP?
     * If so, call this function.
     *
     * (In reality, this function should not be called unless you know what you're doing.
     * It is used to interface 3 aspects of the program - rendering, people, and routines.
     * When an NPC as a speech directive, they will talk for a certain amount of time,
     * which must be rendered.  Once that time is up, the rendering must stop, and the routine
     * must move on to the next directive - but there needs to be a way of passing the information
     * that the talking is over.  This is part of that system.  If you think you need
     * to use this function, you probably don't.  You should ask me about it if you really want to use it.)
     */
    //these are called when they have completed a directive task
    public void stopSpeaking() {isSpeaking=false;}

    /**
     * Changes yer velocity by yer inputs
     * You probably want moveCapped() instead, which caps the velocity to a magnitude.
     * @param dx the change in x velocity
     * @param dy the change in why? velocity
     */
    //changes velocity by (dx,dy)
    public void move(double dx,double dy) {
        if (Room.isMoveableLocation((positionX+dx),(positionY+dy))) {
            velocityX+=dx;
            velocityY+=dy;
        }
    }

    /**
     * Changes your velocity by your inputs, but the last input is an upper bound on how large
     * your resultant velocity will be
     * @param dx the change in x velocity
     * @param dy the change in y velocity
     * @param magcap the cap on the magnitude.
     */
    //like move but caps velocity
    public void moveCapped(double dx,double dy,double magcap) {
        move(dx,dy);
        double mag = Physics.magnitude(velocityX,velocityY);
        if (mag>magcap) {
            velocityX*=magcap/mag;
            velocityY*=magcap/mag;
        }
    }

    /**
     * Moves person by vector (dx,dy) if and only if the tile at curPosition+(dx,dy) is a valid
     * tile for yer peep to be on.
     * @param dx The amount to move by
     * @param dy See dx.
     */
    //changes position by (dx,dy) if it is a valid move
    private void rawMove(double dx,double dy) {
        if (Room.isMoveableLocation(positionX+dx,positionY+dy)) {
            positionX+=dx;
            positionY+=dy;
        }
    }

    /**
     * Ever wanted to change someone's position, but couldn't handle invalid locations?  Well boy,
     * do I have the function for you!  This function literally makes your person go to the exact
     * position you want them to, regardless of whether it will cause them to drown, die, merge with
     * the walls, or spend an eternity encased in stone!
     * @param nx the new position
     * @param ny see px, or use critical thinking.
     */
    //sets position to (nx,ny), does not care if it is a valid move or not
    public void setPosition(double nx,double ny) {
        positionX = nx;
        positionY = ny;
    }

    /**
     * It is quite probably that health be gotten by this function.
     * @return health
     */
    public int getHealth() {return health;}

    /**
     * Imagine you're as healthy as possible - how health are you imaginarily?
     * @return the maximum health this person can have, aka the variable known as 'maxHealth'
     */
    public int getMaxHealth() {return maxHealth;}

    /**
     * Percentages are better than sandwiches because percentages will never abandon you.
     * @return the percentage of their health the person has remaining
     */
    public double getHealthPercentage() {return (1.0*health)/maxHealth;}

    /**
     * makes the health percentage look lower than it really is.  Good for making the player
     * feel more adrenaline and whatnots.  Currently I just square the value, but any
     * fudge-function is fine as long as fudge(0) = 0 and fudge(1) = 1 and fudge(x)<=x for all x on [0,1].
     * @return fondue
     */
    public double getFudgedHealthPercentage() {return Math.pow(getHealthPercentage(),2);}

    /**
     * OW!
     * @param damage How much you want to hurt me right now.
     */
    public void takeDamage(int damage) {
        health-=damage;
    }

    /**
     * Ever wanted to construct a person?  It's pretty easy.  The traditional method required you
     * to find a mommy and a daddy who loved eachother very much, but using this method you can create
     * a baby just by giving them a position and some graphics!
     * @param px their position
     * @param py how much money you expect them to make when they enter the workforce
     * @param tlist their pre-parsed sprite list.
     */
    public Person(int px, int py, HashMap<String,Image[]> tlist) {
        super(px,py,32,32,tlist);
        people.add(this);
    }

    static XMLNode npcXMLData;

    /**
     * This sets up everything the Person class could ever possibly need.
     */
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

    /**
     * Draws all people
     * @param offsetX converts screen-coords to game-coords
     * @param offsetY by taking into account position of camera
     * @param g the rendering context
     */
    public static void drawPeople(int offsetX,int offsetY,Graphics g) {
        for (Person p : people) {
            if (p.roomIn==Room.getRoomID()) {//only draw if they're in the current room
                p.draw(offsetX, offsetY, g);
            }
        }
    }

    //update function, called every tick

    /**
     * This is the function that is called on the outside to handle the updatation of all the peoples of the world.
     * Including, for example, the Scandinavians.
     */
    public static void staticUpdate() {
        for (Person p : people) {
            p.update();
        }
    }

    /**
     * This is the nitty-gritty intestinal function that handles the updatification of one specific person in the world,
     * such as Mehmed II, who coincidentally was the man to deal the final blow to the Roman Empire in 1453, thus forever
     * ending the dreams of those who dreamt of him not dealing the final blow to the Roman Empire in 1453.
     */
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

    /**
     * Checks if this person can see another sprite.
     * @param s A query-style string (if you're not sure about format, see explanation in Sprite.query())
     * @return true if they can see a sprite, false if they can't
     */
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
