package sprites;

import main.XMLNode;

import java.awt.Graphics;
import java.awt.Image;
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

    public double getPositionX() {return positionX;}
    public double getPositionY() {return positionY;}
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