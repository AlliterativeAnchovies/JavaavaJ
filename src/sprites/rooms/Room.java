package sprites.rooms;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Room {
    public static Room[] rooms;
    public static final int ROOM_AMOUNT = 1;
    private Tile[][] tiles;
    private static int offx = 0;
    private static int offy = 0;

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

    //static method that handles rendering of all rooms
    public static void drawRooms(Graphics g) {
        for (Room r : rooms) {
            r.draw(offx, offy, g);
        }
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
}
