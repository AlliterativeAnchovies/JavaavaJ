package ai;
import java.util.*;

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
    //no need for 'gotoprogress' b/c it only makes sense to have 1 goto.
    private List<List<String>>  toAttack;
    private List<List<String>> typeOfAttack;
    private int attackingprogress;


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
        toAttack = new ArrayList<>();
        typeOfAttack = new ArrayList<>();
        List<XMLNode> groups = x.getChildrenWithKey("Group");
        for (XMLNode g : groups) {
            //TODO: A lot of of this code is repetitive - maybe could condense this so that there is a function
            //TODO: for loading each of the arrays?  Maybe not because it looks like <Say> is more complicated than
            //TODO: the rest, but it was also the first one I did so maybe it can be simplified now that the system is
            //TODO: more established?
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
            //load <Goto> tags - these are for jumping around the groups.
            List<XMLNode> loadedgotos = g.getChildrenWithKey("Goto");
            ArrayList<String> toGoTo = new ArrayList<>();
            for (XMLNode gt : loadedgotos) {
                toGoTo.add(gt.getAttributeWithName("group"));
            }
            //load <Attack> tags - these are for raw attacks
            List<XMLNode> loadedattacks = g.getChildrenWithKey("Attack");
            ArrayList<String> toAttack_ = new ArrayList<>();
            ArrayList<String> typeOfAttack_ = new ArrayList<>();
            for (XMLNode a : loadedattacks) {
                toAttack_.add(a.getAttributeWithName("thing"));
                typeOfAttack_.add(a.getAttributeWithName("using"));
            }
            //Add all of the loaded datas into their correct places
            toSay.add(toSay_);
            talkingSpeed.add(talkingSpeed_);
            toMove.add(toMove_);
            walkingSpeed.add(walkingSpeed_);
            gotos.add(toGoTo);
            toAttack.add(toAttack_);
            typeOfAttack.add(typeOfAttack_);
        }
        //set the progresses to defaults
        talkingprogress = 0;
        walkingprogress = 0;
        attackingprogress = 0;
        //load the interrupts
        interrupts = new ArrayList<>();
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
        //TODO: Like what I mentioned in the constructor, this section seems a wee bit repetitive.
        //TODO: Investigate condensing parts of this into its own function.
        if (toSay.size()>curGroup&&toSay.get(curGroup).size()>talkingprogress) {
            toReturn.add(new Pair<>("Say",talkingSpeed.get(curGroup).get(talkingprogress)+"|"+toSay.get(curGroup).get(talkingprogress)));
            needsnewgroup = false;
        }
        if (toMove.size()>curGroup&&toMove.get(curGroup).size()>walkingprogress) {
            toReturn.add(new Pair<>("Move",walkingSpeed.get(curGroup).get(walkingprogress)+"|"+toMove.get(curGroup).get(walkingprogress)));
            needsnewgroup = false;
        }
        if (toAttack.size()>curGroup&&toAttack.get(curGroup).size()>attackingprogress) {
            toReturn.add(new Pair<>("Attack",toAttack.get(curGroup).get(attackingprogress)+"|"+typeOfAttack.get(curGroup).get(attackingprogress)));
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
        attackingprogress = 0;
    }

    //the 'callback' functions are used for information to be returned to Directive about the completion
    //of actions!  Directive has no idea when a person finishes moving or talking, for example, so we need these.
    public void talkingCallback() {
        talkingprogress++;
    }

    public void walkingCallback() {
        walkingprogress++;
    }

    public void attackingCallback() {
        attackingprogress++;
    }


    public Time getStartTime() {return startTime;}
    public Time getEndTime() {return endTime;}

    //this checks if an interrupt was triggered and if so returns the
    //string containing the new routine the NPC should swap to to handle
    //this routine.  Returns format (Name of New State,Name of Cause)
    public Pair<String,String> handleInterrupts(NPC directee) {
        for (Interrupt i : interrupts) {
            String checked = i.check(directee);
            if (checked!=null) {
                return new Pair<>(i.getRoutineToChangeTo(),checked);
            }
        }
        return null;
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

    //returns null if not triggered, otherwise returns what was triggered
    public String check(NPC directee) {
        //first check visibility triggers
        for (String viewTrigger : viewTriggers) {
            if (directee.canSee(viewTrigger)) {
                return viewTrigger;
            }
        }
        //then check other types of triggers if they're added.
        return null;
    }

}