package sprites;
import main.Pair;
import main.Physics;
import main.XML;
import main.XMLNode;
import sprites.rooms.Room;

import java.util.ArrayList;
import java.awt.*;
import java.util.HashMap;


public class Person extends Sprite {
    protected int health;
    protected int maxHealth;
    private static ArrayList<Person> people;
    protected double velocityX;
    protected double velocityY;
    //changes velocity by (dx,dy)
    public void move(int dx,int dy) {
        if (Room.isMoveableLocation(positionX+dx,positionY+dy)) {
            velocityX+=dx;
            velocityY+=dy;
        }
    }
    //changes position by (dx,dy) if it is a valid move
    private void rawMove(int dx,int dy) {
        if (Room.isMoveableLocation(positionX+dx,positionY+dy)) {
            positionX+=dx;
            positionY+=dy;
        }
    }
    //sets position to (nx,ny), does not care if it is a valid move or not
    public void setPosition(int nx,int ny) {
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
    }

    //draws all people
    public static void drawPeople(int offsetX,int offsetY,Graphics g) {
        for (Person p : people) {
            p.draw(offsetX,offsetY,g);
        }
    }

    //update function, called every tick
    public static void update() {
        for (Person p : people) {
            //normalize their velocity
            Pair<Double,Double> normalized = Physics.normalize(p.velocityX,p.velocityY);
            p.velocityX = 4*normalized.x;//see, this is why C++ has the right idea with pointers.  Physics.normalize
            p.velocityY = 4*normalized.y;//should ideally be void, and take in pointers to the velocity to change it!
                                        //It would make this code much shorter and eliminate the need to use Pair
                                        //and deal with the dumb 'generics can't use raw types' stuffs.
                                        //(In all honesty, I'm starting to think Java doesn't want us to use raw types.
                                        //Maybe its better if we use its Integer/Double classes everywhere instead?)
            p.rawMove((int)p.velocityX,(int)p.velocityY);
        }
    }

}
