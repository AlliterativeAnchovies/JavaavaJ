import java.awt.Graphics;

class Tile extends Sprite {
	final static int TILE_WIDTH_IN_PIXELS = 32;
	final static int TILE_HEIGHT_IN_PIXELS = 32;
	protected int tileID;
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
	//returns the game width - in pixels!
	public static int getGameWidthInPixels() {
		return 512;
	}
	//returns the game height - in pixels!
	public static int getGameHeightInPixels() {
		return 512;
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
		XML tiledata = new XML("Resources/tiledata.xml");
	}
	
}
