package sprites;

//import java.awt.*;
import java.awt.Image;
import java.util.List;
import java.util.HashMap;

import ai.Directive;
import ai.Routine;
import main.Pair;

public class NPC extends Person {
    private Routine routine;
    private String name;
    public NPC(int px,int py,HashMap<String,Image[]> tlist,String n,int room,String xmlroutinepath) {
        super(px,py,tlist);
        name = n;
        roomIn = room;
        routine = Routine.parseRoutine("Resources/"+xmlroutinepath);
    }

    @Override protected void update() {
        super.update();
        Directive r = routine.getCurrentDirective();
        if (r==null) {return;}//has no directions
        List<Pair<String,String>> jobs = r.getDirections();
        for (Pair<String,String> j : jobs) {
            //go through and perform all directions!
            if (j.x.equals("Say")) {
                System.out.println(j.y);
            }
        }
    }

}
