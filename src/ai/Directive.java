package ai;
import java.util.*;

import main.Main;
import sprites.rooms.Tile;
import main.Time;
import main.XMLNode;
import main.Pair;
import sprites.NPC;

public class Directive {
    private Time startTime;
    private Time endTime;
    private int curGroup;
    private List<List<String>> toSay;
    private List<List<String>> talkingSpeed;
    private int talkingprogress;
    private List<List<String>> toMove;
    private List<List<String>> walkingSpeed;
    private int walkingprogress;
    private List<Interrupt> interrupts;
    private List<List<String>> gotos;


    public Directive(XMLNode x) {
        //parses the xml tree to create directive
        //the part of the tree this receives is the <Directive> tags
        String s_ = x.getAttributeWithName("start");
        String e_ = x.getAttributeWithName("end");
        startTime = new Time(s_);
        endTime = new Time(e_);
        curGroup = 0;
        //load all of the sequential stuffs
        toSay = new ArrayList<>();
        talkingSpeed = new ArrayList<>();
        toMove = new ArrayList<>();
        walkingSpeed = new ArrayList<>();
        gotos = new ArrayList<>();
        List<XMLNode> groups = x.getChildrenWithKey("Group");
        for (XMLNode g : groups) {
            //load <Say> tags - these are for dialogue
            ArrayList<String> toSay_ = new ArrayList<>();
            ArrayList<String> talkingSpeed_ = new ArrayList<>();
            List<XMLNode> speech = g.getChildrenWithKey("Say");
            for (XMLNode s : speech) {
                List<String> toAdd = Arrays.asList(s.getValue().split("\\|"));
                List<String> durs = new ArrayList<String>();
                String durtoadd = s.getAttributeWithName("seconds");
                for (String thisisdumb : toAdd) {//why doesn't java have a constructor to fill with default values?
                    durs.add(durtoadd);
                }
                talkingSpeed_.addAll(durs);
                toSay_.addAll(toAdd);
            }
            //load <Move> tags - these are for raw moving
            List<XMLNode> loadedmoves = g.getChildrenWithKey("Move");
            ArrayList<String> toMove_ = new ArrayList<>();
            ArrayList<String> walkingSpeed_ = new ArrayList<>();
            for (XMLNode m : loadedmoves) {
                walkingSpeed_.add(m.getAttributeWithName("speed"));
                toMove_.add(m.getAttributeWithName("deltax") + "," + m.getAttributeWithName("deltay"));
            }
            toSay.add(toSay_);
            talkingSpeed.add(talkingSpeed_);
            toMove.add(toMove_);
            walkingSpeed.add(walkingSpeed_);
            //load <Goto> tags - these are for jumping around the groups.
            List<XMLNode> loadedgotos = g.getChildrenWithKey("Goto");
            ArrayList<String> toGoTo = new ArrayList<>();
            for (XMLNode gt : loadedgotos) {
                toGoTo.add(gt.getAttributeWithName("group"));
            }
            gotos.add(toGoTo);
        }
        talkingprogress = 0;
        walkingprogress = 0;
        interrupts = new ArrayList<>();
        //load the interrupts
        List<XMLNode> loadedinterrupts = x.getChildrenWithKey("Interrupt");
        for (XMLNode interrupt : loadedinterrupts) {
            interrupts.add(new Interrupt(interrupt));
        }
    }

    //returns a list of directions, of the form:
    // [<"Say","What to say...">,<"Move","1,2">,...]
    // So a list of pairs where the first in pair is type of direction and the second
    // is easily parseable material detailing the specifics.
    public List<Pair<String,String>> getDirections() {
        List<Pair<String,String>> toReturn = new ArrayList<Pair<String,String>>();
        boolean needsnewgroup = true;
        if (gotos.size()>curGroup) {
            if (gotos.get(curGroup).size()>0) {
                int togoto = Integer.parseInt(gotos.get(curGroup).get(0));
                if (togoto == curGroup) {
                    throw new RuntimeException("ERROR!  Infinite loop detected in - be careful with GOTO!");
                }
                curGroup = togoto;
                resetProgresses();
                return getDirections();
            }
        }
        if (toSay.size()>curGroup&&toSay.get(curGroup).size()>talkingprogress) {
            toReturn.add(new Pair<>("Say",talkingSpeed.get(curGroup).get(talkingprogress)+"|"+toSay.get(curGroup).get(talkingprogress)));
            needsnewgroup = false;
        }
        if (toMove.size()>curGroup&&toMove.get(curGroup).size()>walkingprogress) {
            toReturn.add(new Pair<>("Move",walkingSpeed.get(curGroup).get(walkingprogress)+"|"+toMove.get(curGroup).get(walkingprogress)));
            needsnewgroup = false;
        }
        if (needsnewgroup) {
            if (curGroup<toSay.size()) {//don't infinitely increment curgroup...
                curGroup++;
                resetProgresses();
            }
        }
        return toReturn;
    }

    //called whenever the group changes
    public void resetProgresses() {
        talkingprogress = 0;
        walkingprogress = 0;
    }

    //the 'callback' functions are used for information to be returned to Directive about the completion
    //of actions!  Directive has no idea when a person finishes moving or talking, for example, so we need these.
    public void talkingCallback() {
        talkingprogress++;
    }

    public void walkingCallback() {
        walkingprogress++;
    }

    public Time getStartTime() {return startTime;}
    public Time getEndTime() {return endTime;}

    //this checks if an interrupt was triggered and if so returns the
    //string containing the new routine the NPC should swap to to handle
    //this routine.
    public String handleInterrupts(NPC directee) {
        for (Interrupt i : interrupts) {
            if (i.check(directee)) {
                return i.getRoutineToChangeTo();
            }
        }
        return "";
    }
}

class Interrupt {
    private List<String> viewTriggers;//if these objects enter the NPC's view, the interrupt will trigger
    private String routineToChangeTo;//once triggered, it will change the NPC's routine to this

    public Interrupt(XMLNode n) {
        viewTriggers = new ArrayList<String>();
        List<XMLNode> causes = n.getChildrenWithKey("Causes");
        //first let's get all of the triggers
        for (XMLNode c : causes) {
            //check the view causes
            List<XMLNode> viewcauses = c.getChildrenWithKey("Sees");
            for (XMLNode v : viewcauses) {
                viewTriggers.add(v.getAttributeWithName("thing"));
            }
        }
        //now let's get the new routine if it is triggered
        routineToChangeTo = n.getChildWithKey("Response").getAttributeWithName("name");
    }

    public String getRoutineToChangeTo() {return routineToChangeTo;}

    //returns if the interrupt triggered or not
    public boolean check(NPC directee) {
        for (String viewTrigger : viewTriggers) {
            if (directee.canSee(viewTrigger)) {
                return true;
            }
        }
        return false;
    }

}