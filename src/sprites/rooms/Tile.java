package sprites.rooms;
import sprites.*;
import main.XML;
import main.XMLNode;
import java.awt.Color;
import java.util.*;

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
    public static int getTileIDFromColor(Color c) {
        List<XMLNode> tilesXML = tileData.getRoot().getChildrenWithKey("tile");
        for (XMLNode t : tilesXML) {
            String colorValue = t.getChildWithKey("color").getValue();
            if (Long.decode(colorValue)==c.getRGB()) {
                return Integer.parseInt(t.getAttributeWithName("id"));
            }
        }
        return -1;
    }

    //creates a new tile
    public Tile(int px,int py,int sx,int sy,int tID) {
        super(px,py,sx,sy);
        tileID = tID;
        status = TileState.NORMAL;
    }

    //later on, will handle grabbing tile data from a text file.
    public static void init() {
        System.out.println("Initializing tiles...");
        tileData = new XML("Resources/tiledata.xml");
    }

}
