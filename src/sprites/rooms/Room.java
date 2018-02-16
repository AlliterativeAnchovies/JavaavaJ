package sprites.rooms;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Room {
    public static Room[] rooms;
    public static final int ROOM_AMOUNT = 1;
    private Tile[][] tiles;
    public static int curRoom = 0;

    //gets the current room
    public static Room getCurRoom() {
        return rooms[curRoom];
    }

    //creates a room from a tile array
    public Room(Tile[][] ts) {
        tiles = ts;
    }

    //get's the tile at (x,y) [game coordinates, not screen coordinates]
    public Tile getTile(int x, int y) {
        return tiles[x][y];
    }

    //draws the room (note that rooms themselves are not sprites, but they are a collection
    //of sprites).
    private void draw(int offsetx, int offsety, Graphics g) {
        for (Tile[] row : tiles) {
            for (Tile t : row) {
                t.draw(offsetx, offsety, g);
            }
        }
    }

    //static method that handles rendering the current room
    public static void drawRooms(int offsetx,int offsety,Graphics g) {
        getCurRoom().draw(offsetx,offsety,g);
    }

    //does all tile class-level initialization stuff.
    //later on, this should handle reading sprites.rooms from an image file
    public static void init() {
        System.out.println("Initializing rooms...");
        rooms = new Room[ROOM_AMOUNT];
        for (int r = 0; r < ROOM_AMOUNT; r++) {
            File bmpFile = new File("Resources/Rooms/room_" + r + ".bmp");
            try {
                BufferedImage image = ImageIO.read(bmpFile);
                int wd = image.getWidth();
                int ht = image.getHeight();
                Tile[][] tiles_ = new Tile[wd][ht];
                for (int i = 0; i < wd; i++) {
                    for (int j = 0; j < ht; j++) {
                        int pixelvalue = image.getRGB(i,j);
                        tiles_[i][j] = new Tile(
                                i * Tile.TILE_WIDTH_IN_PIXELS,
                                j * Tile.TILE_HEIGHT_IN_PIXELS,
                                Tile.TILE_WIDTH_IN_PIXELS,
                                Tile.TILE_HEIGHT_IN_PIXELS,
                                Tile.getTileIDFromColor(image.getRGB(i, j))
                        );
                    }
                }
                rooms[r] = new Room(tiles_);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //get list of all tiles a person could be over (1 if perfectly on tile, 2 if perfect in 1 direc only, 4 if on a corner.
    public static ArrayList<Tile> getTilesUnderPerson(int startx, int starty) {
        //tiles are nicely ordered (in array) already, it's rather easy to grab the stuffs.
        int grabx = startx/Tile.TILE_WIDTH_IN_PIXELS;
        int graby = starty/Tile.TILE_HEIGHT_IN_PIXELS;
        int grabx2 = (startx%Tile.TILE_WIDTH_IN_PIXELS==0)?-1:grabx+1;
        int graby2 = (starty%Tile.TILE_HEIGHT_IN_PIXELS==0)?-1:graby+1;
        ArrayList<Tile> toReturn = new ArrayList<Tile>();
        toReturn.add(getCurRoom().getTile(grabx,graby));//add the one it's top left corner is on
        if (grabx2!=-1) {//add the others
            toReturn.add(getCurRoom().getTile(grabx2,graby));
            if (graby2!=-1) {toReturn.add(getCurRoom().getTile(grabx2,graby2));}
        }
        if (graby2!=-1) {toReturn.add(getCurRoom().getTile(grabx,graby2));}
        return toReturn;
    }

    public static boolean isMoveableLocation(int px,int py) {
        ArrayList<Tile> tls = getTilesUnderPerson(px,py);
        boolean toReturn = true;
        for (Tile t : tls) {
            toReturn = toReturn && t.isFloor();
        }
        return toReturn;
    }

}
