package sprites.rooms;
import sprites.*;
import main.XML;
import main.XMLNode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Tile extends Sprite {
    final static int TILE_WIDTH_IN_PIXELS = 32;
    final static int TILE_HEIGHT_IN_PIXELS = 32;
    protected int tileID;
    static XML tileData;
    public enum TileState {
        NORMAL
    }
    private TileState status;

    //returns true if the tile can be walked on by the player normally
    public boolean isFloor() {return tileID>=0&&tileID<1000;}
    //returns true if the tile is a traditionally 'wall' tile (can never be walked on by player)
    public boolean isWall() {return tileID>=1000&&tileID<2000;}
    //change the status of the tile
    public void changeStatus(TileState newstatus) {status=newstatus;}

    //get tile id from color of pixel (used when loading Room from .bmp)
    public static int getTileIDFromColor(int c) {
        List<XMLNode> tilesXML = tileData.getRoot().getChildrenWithKey("Tile");
        for (XMLNode t : tilesXML) {
            String colorValue = t.getChildWithKey("Color").getValue();
            long cV = Long.decode(colorValue);
            if (cV==Integer.toUnsignedLong(c)) {
                return Integer.parseInt(t.getAttributeWithName("id"));
            }
        }
        return -1;
    }

    //creates a new tile
    public Tile(int px,int py,int sx,int sy,int tID) {
        super(px,py,sx,sy,getTileTexturesetFromID(tID));
        tileID = tID;
        status = TileState.NORMAL;
    }

    //given a tile id, it creates the textureset of said id by looking at the xml document.
    //Note: while this method is perfectly efficient, it's currently being called in a really inefficient manner -
    //instead of calling it once per distinct tileID, it is called once per tile!  But I'm too lazy to implement that :)
    //will only do it later if it turns out that the inefficiencies cause a noticeable pain.
    public static HashMap<String,Image[]> getTileTexturesetFromID(int tID) {
        HashMap<String,Image[]> toReturn = new HashMap<String,Image[]>();
        List<XMLNode> tilesXML = tileData.getRoot().getChildrenWithKey("Tile");
        for (XMLNode t : tilesXML) {
            int idvalue = Integer.parseInt(t.getAttributeWithName("id"));
            if (idvalue==tID) {
                //found the correct node!
                Sprite.parseSpriteList(t,toReturn);
                break;
            }
        }
        return toReturn;
    }

    //initializes all information needed for tiles
    public static void init() {
        System.out.println("Initializing tiles...");
        tileData = new XML("Resources/tiledata.xml");
    }

}
