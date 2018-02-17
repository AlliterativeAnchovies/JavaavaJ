package sprites;

//import java.awt.*;
import java.awt.Image;
import java.util.List;
import java.util.HashMap;

import ai.Directive;
import ai.Routine;
import main.Pair;
import main.Main;
import main.Physics;

public class NPC extends Person {
    private Routine routine;
    private String name;
    private double destinationx;
    private double destinationy;
    private double movespeed;
    private Directive curMovingDirectiveForCallback;
    public NPC(int px,int py,HashMap<String,Image[]> tlist,String n,int room,String xmlroutinepath) {
        super(px,py,tlist);
        name = n;
        roomIn = room;
        routine = Routine.parseRoutine("Resources/"+xmlroutinepath);
    }

    @Override protected void update() {
        super.update();

        //handle following directives
        if (isMoving) {
            //TODO pathfinding
            Pair<Double,Double> movedirec = Physics.normalize(destinationx-positionX,destinationy-positionY);
            moveCapped((movespeed*movedirec.x),(movespeed*movedirec.y),movespeed);
            if (Physics.magnitude(destinationx-positionX,destinationy-positionY)<8) {
                isMoving = false;
                velocityX=0;
                velocityY=0;
                curMovingDirectiveForCallback.walkingCallback();
            }
        }

        //handle directives
        Directive r = routine.getCurrentDirective();
        if (r==null) {return;}//has no directions
        List<Pair<String,String>> jobs = r.getDirections();
        for (Pair<String,String> j : jobs) {
            //go through and perform all directions!
            if (j.x.equals("Say")&&!isSpeaking) {
                //System.out.println(j.y);
                isSpeaking = true;
                String[] info = j.y.split("\\|");
                String toWrite = "";
                for (int i = 1;i<info.length;i++) {
                    toWrite+=info[i];
                }
                Main.renderer.makeSpeechBubble(this, r, toWrite, (int) (Double.parseDouble(info[0]) * Main.UPS));
            }
            else if (j.x.equals("Move")&&!isMoving) {
                isMoving = true;
                String[] info_ = j.y.split("\\|");
                String[] info = info_[1].split(",");
                destinationx = positionX+Integer.parseInt(info[0]);
                destinationy = positionY+Integer.parseInt(info[1]);
                movespeed = Double.parseDouble(info_[0]);
                curMovingDirectiveForCallback = r;
            }
        }
    }

}
