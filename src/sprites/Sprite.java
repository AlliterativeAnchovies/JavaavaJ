package sprites;

import java.awt.Graphics;
import java.awt.Image;
import java.util.HashMap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Sprite {
    protected HashMap<String,Image[]> textureList;//different states are indexed by the strings, each state has an array
                                    //of animation frames
    protected int positionX;
    protected int positionY;
    protected int sizeX;
    protected int sizeY;
    protected String curState;
    protected int curFrame;

    //handles drawing of Sprite.  Takes in an offset and a graphics
    //context.  The offset is used in cases where we want to scroll the screen -
    //the tile's absolute position shouldn't change, but its position relative to the
    //screen will.
    //Always call super.draw from child classes' overriden draws
    public void draw(int offsetX,int offsetY,Graphics g) {
        g.drawImage(getCurImage(),offsetX+positionX,offsetY+positionY,null);
        curFrame=(curFrame+1)%(textureList.get(curState).length);//increment the frame
    }

    //changes sprite state
    public void changeState(String newstate) {
        curState = newstate;
        curFrame = 0;
    }

    //returns current image of sprite
    public Image getCurImage() {
        return textureList.get(curState)[curFrame];
    }

    public Sprite(int px,int py,int sx,int sy,HashMap<String,Image[]> tlist) {
        positionX = px;
        positionY = py;
        sizeX = sx;
        sizeY = sy;
        curState = "Default";
        curFrame = 0;
        textureList = tlist;
        /*File bmpFile = new File("Resources/Sprites/basicfloor.bmp");
        textureList = new HashMap<String,Image[]>();
        Image[] imageList = new Image[2];
        try {
            imageList[0] = ImageIO.read(bmpFile);
            imageList[1] = ImageIO.read(new File("Resources/Sprites/basicwall.bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        textureList.put("Default",imageList);*/
    }
}
