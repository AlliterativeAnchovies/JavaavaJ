package sprites;
import sprites.*;

public class Person extends Sprite {
    protected int health;
    protected int maxHealth;
    //changes position by (dx,dy) if it is a valid move
    public void move(int dx,int dy) {
        //TO-DO MAKE METHOD
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

    public Person(int px,int py) {
        super(px,py,16,16,null);
        //TO-DO make Persons have sprite lists as well.
    }
}
