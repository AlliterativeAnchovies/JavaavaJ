package sprites;
import java.util.List;
import java.util.ArrayList;

public class Item {
    public int itemID;
    public static List<Item> allItems;
    public Item(int id) {
        itemID = id;
    }
    public int getID() {return itemID;}
    public static void init() {
        allItems = new ArrayList<>();
    }
}
