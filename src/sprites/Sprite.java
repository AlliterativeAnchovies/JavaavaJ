package sprites;

import main.Physics;
import main.XMLNode;
import sprites.rooms.Tile;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public class Sprite {
    protected HashMap<String,Image[]> textureList;//different states are indexed by the strings, each state has an array
                                    //of animation frames
    protected double positionX;
    protected double positionY;
    protected int sizeX;
    protected int sizeY;
    protected String curState;
    protected int curFrame;
    protected String name;
    public static List<Sprite> allSprites;

    final static int SPRITE_FRAME_RATE = 2;//how many frames before sprite updates texture;

    //handles drawing of Sprite.  Takes in an offset and a graphics
    //context.  The offset is used in cases where we want to scroll the screen -
    //the tile's absolute position shouldn't change, but its position relative to the
    //screen will.
    //Always call super.draw from child classes' overriden draws
    public void draw(int offsetX,int offsetY,Graphics g) {
        g.drawImage(getCurImage(),(int)(offsetX+positionX),(int)(offsetY+positionY),sizeX,sizeY,null);
        curFrame=(curFrame+1)%(textureList.get(curState).length*SPRITE_FRAME_RATE);//increment the frame
    }

    //changes sprite state (also resets frame on so it doesn't start in the middle of an animation)
    public void changeState(String newstate) {
        curState = newstate;
        curFrame = 0;
    }

    //changes the state, but only if its not already in the state. (so as to avoid constantly changing to a
    //certain state every update loop and having that reset the frame)
    public void changeStateIfNeeded(String newstate) {
        if (!curState.equals(newstate)) {
            changeState(newstate);
        }
    }

    //returns current image of sprite
    public Image getCurImage() {
        return textureList.get(curState)[curFrame/SPRITE_FRAME_RATE];
    }

    //creates a new sprite
    public Sprite(double px,double py,int sx,int sy,HashMap<String,Image[]> tlist) {
        positionX = px;
        positionY = py;
        sizeX = sx;
        sizeY = sy;
        curState = "Default";
        curFrame = 0;
        textureList = tlist;
        if (name==null) {name="";}
        allSprites.add(this);
    }

    //given some XML data, assuming it's in the standard form (see comment at bottom of file)
    //then this will fill hashmapToFill with the sprite data.
    public static void parseSpriteList(XMLNode t,HashMap<String,Image[]> hashmapToFill) {
        List<XMLNode> allSpriteStates = t.getChildrenWithKey("SpriteList");
        for (XMLNode spritestate : allSpriteStates) {
            String nameofstate = spritestate.getAttributeWithName("state");
            String[] filepaths = spritestate.getValue().split("\\|");
            Image[] theImages = new Image[filepaths.length];
            for (int i = 0;i<filepaths.length;i++) {
                try {
                    theImages[i] = ImageIO.read(new File("Resources/"+filepaths[i]));
                } catch (IOException e) {
                    System.out.println("Error!  It seems there is no "+filepaths[i]+" file in the Resources folder?");
                }
            }
            hashmapToFill.put(nameofstate,theImages);
        }
    }

    public static void init() {
        allSprites = new ArrayList<>();
    }

    public double getPositionX() {return positionX;}
    public double getPositionY() {return positionY;}

    public static List<Sprite> query(String s) {
        List<Sprite> tocheck = new ArrayList<>();
        List<Sprite> toReturn = new ArrayList<>();
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
                toReturn.add(spr);
            }
        }
        return toReturn;
    }
}

/*
Standard form for XML sprite data:
<SpriteList state="statename">
    list/of/files.bmp|
    seperated/by.bmp|
    pipe/symbol
</SpriteList>
</SpriteList state="otherstate">
    you/can/have.bmp|
    as/many/states.bmp|
    and/images/in/states.bmp|
    as/you/want.bmp
</SpriteList>
It is important to note that the filepaths should NOT include "Resources".  As they are all resources,
these paths represents paths local to the Resources folder.
 */