package sprites;

//import java.awt.*;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import ai.Directive;
import ai.Routine;
import main.Pair;
import main.Main;
import main.Physics;

public class NPC extends Person {
    private Routine routine;
    private double destinationx;
    private double destinationy;
    private double movespeed;
    private Directive curMovingDirectiveForCallback;
    private static List<NPC> allNPCs;
    public NPC(int px,int py,HashMap<String,Image[]> tlist,String n,int room,String xmlroutinepath) {
        super(px,py,tlist);
        name = n;
        roomIn = room;
        routine = Routine.parseRoutine("Resources/"+xmlroutinepath);
        allNPCs.add(this);
    }

    public static getAllNPCs() {
        return allNPCs;
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
        //handle interrupts
        Directive r = routine.getCurrentDirective();
        //get interrupt data - returns pair of form (Name of New State,Name of Cause)
        Pair<String,String> interrupted = r.handleInterrupts(this);
        if (interrupted!=null) {
            routine.changeState(interrupted.x,interrupted.y);
            r = routine.getCurrentDirective();
        }
        //handle directives
        if (r==null) {return;}//has no directions
        List<Pair<String,String>> jobs = r.getDirections();
        for (Pair<String,String> j : jobs) {
            //go through and perform all directions!
            if (j.x.equals("Say")&&!isSpeaking) {
                //Data format: timeToTalkInSeconds|whatToSay
                //example: 5|Hello will make a speech bubble 'Hello' for 5 seconds.
                isSpeaking = true;
                String[] info = j.y.split("\\|");
                String toWrite = "";
                for (int i = 1;i<info.length;i++) {
                    toWrite+=info[i];
                }
                Main.renderer.makeSpeechBubble(this, r, toWrite, (int) (Double.parseDouble(info[0]) * Main.UPS));
            }
            else if (j.x.equals("Move")&&!isMoving) {
                //Data format: walkingSpeed|∆x_coord,∆y_coord
                //Example: 5|6,7 will move (6,7) at speed of 5 whatevers/second.
                isMoving = true;
                String[] info_ = j.y.split("\\|");
                String[] info = info_[1].split(",");
                destinationx = positionX+Integer.parseInt(info[0]);
                destinationy = positionY+Integer.parseInt(info[1]);
                movespeed = Double.parseDouble(info_[0]);
                curMovingDirectiveForCallback = r;
            }
            else if (j.x.equals("Attack")&&!isAttacking) {
                //format: target|attackID
                //Example: person:Danny Skellington|Attacks/fireball.xml will make this person attack
                //Danny Skellington with the attack specified by Attacks/fireball.xml.
                //(Note: Attacks/fireball.xml, while being an xml path, does not necessarily load the xml
                //file every time - Attack will only load a file once, and then store it for future reference).
                //(Note 2: All targets are of the same form as is used for trigger checks - see Sprite.query for
                //a full explanation on the format, although its really quite simple).
                isAttacking = true;
                String[] info = j.y.split("\\|");
                if (info[0].equals("@cause")) {
                    info[0] = routine.getCause();
                }
                //System.out.println("Attacking "+info[0]);
                Attack.createAttack(positionX,positionY,info[1]);
            }
        }
    }

    public static void init() {
        allNPCs = new ArrayList<>();
    }



}
