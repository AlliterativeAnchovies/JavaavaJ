package sprites;

import java.awt.Graphics;

public class Sprite {
    //protected Image texture;
    protected int positionX;
    protected int positionY;
    protected int sizeX;
    protected int sizeY;

    //handles drawing of Sprite.  Takes in an offset and a graphics
    //context.  The offset is used in cases where we want to scroll the screen -
    //the tile's absolute position shouldn't change, but its position relative to the
    //screen will.
    //Always call super.draw from child classes' overriden draws
    public void draw(int offsetX,int offsetY,Graphics g) {
        //g.drawRect(offsetX+positionX,offsetY+positionY,sizeX,sizeY);
    }

    public Sprite(int px,int py,int sx,int sy) {
        positionX = px;
        positionY = py;
        sizeX = sx;
        sizeY = sy;
    }
}
