package sprites;

import java.awt.*;
import java.util.HashMap;
import ai.Routine;

public class NPC extends Person {
    private Routine routine;
    private String name;
    public NPC(int px,int py,HashMap<String,Image[]> tlist,String n,int room) {
        super(px,py,tlist);
        name = n;
        roomIn = room;
    }

    @Override protected void update() {
        super.update();
    }

}
