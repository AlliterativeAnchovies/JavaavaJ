import java.awt.Graphics;
class Room {
	public static Room[] rooms;
	public static final int ROOM_AMOUNT = 1;
	private Tile[][] tiles;
	private static int offx = 0;
	private static int offy = 0;
	public Room(Tile[][] ts) {
		tiles = ts;
	}
	public Tile getTile(int x,int y) {return tiles[x][y];}
	public void draw(int offsetx,int offsety,Graphics g) {
		for (Tile[] row : tiles) {
			for (Tile t : row) {
				t.draw(offsetx,offsety,g);
			}
		}
	}
	
	public static void drawRooms(Graphics g) {
		for (Room r : rooms) {
			r.draw(offx,offy,g);
		}
	}
	
	//does all tile class-level initialization stuff.
	//later on, this should handle reading rooms from an image file
	public static void init() {
		System.out.println("Initializing rooms...");
		rooms = new Room[ROOM_AMOUNT];
		for (int r = 0;r<ROOM_AMOUNT;r++) {
			File bmpFile = new File("Resources/Rooms/room_"+String.parseString(r)+".bmp");
			BufferedImage image = ImageIO.read(bmpFile);
			
			Tile[][] tiles_ = new Tile[16][16];
			for (int i = 0;i<16;i++) {
				for (int j = 0;j<16;j++) {
					tiles_[i][j] = new Tile(
						i*Tile.TILE_WIDTH_IN_PIXELS,
						j*Tile.TILE_HEIGHT_IN_PIXELS,
						Tile.TILE_WIDTH_IN_PIXELS,
						Tile.TILE_HEIGHT_IN_PIXELS,
						0//tile id
					);
				}
			}
			rooms[r] = new Room(tiles_);
		}
		
	}}
