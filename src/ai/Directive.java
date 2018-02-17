package ai;
import java.util.*;

import main.Main;
import sprites.rooms.Tile;
import main.Time;
import main.XMLNode;
import main.Pair;

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


    public Directive(XMLNode x) {
        //parses the xml tree to create directive
        //the part of the tree this receives is  the <Directive> tags
        String s_ = x.getAttributeWithName("start");
        String e_ = x.getAttributeWithName("end");
        startTime = new Time(s_);
        endTime = new Time(e_);
        curGroup = 0;
        //first handle all the talking stuffs
        toSay = new ArrayList<>();
        talkingSpeed = new ArrayList<>();
        toMove = new ArrayList<>();
        walkingSpeed = new ArrayList<>();
        List<XMLNode> groups = x.getChildrenWithKey("Group");
        for (XMLNode g : groups) {
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
            //now handle the moving stuffs
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
        }
        talkingprogress = 0;
        walkingprogress = 0;
    }

    //returns a list of directions, of the form:
    // [<"Say","What to say...">,<"Move","1,2">,...]
    // So a list of pairs where the first in pair is type of direction and the second
    // is easily parseable material detailing the specifics.
    public List<Pair<String,String>> getDirections() {
        List<Pair<String,String>> toReturn = new ArrayList<Pair<String,String>>();
        boolean needsnewgroup = true;
        if (toSay.size()>curGroup&&toSay.get(curGroup).size()>talkingprogress) {
            toReturn.add(new Pair<>("Say",talkingSpeed.get(curGroup).get(talkingprogress)+"|"+toSay.get(curGroup).get(talkingprogress)));
            needsnewgroup = false;
        }
        if (toMove.size()>curGroup&&toMove.get(curGroup).size()>walkingprogress) {
            toReturn.add(new Pair<>("Move",walkingSpeed.get(curGroup).get(walkingprogress)+"|"+toMove.get(curGroup).get(walkingprogress)));
            needsnewgroup = false;
        }
        if (needsnewgroup) {
            curGroup++;
        }
        return toReturn;
    }

    public void talkingCallback() {
        talkingprogress++;
    }

    public void walkingCallback() {
        walkingprogress++;
    }

    public Time getStartTime() {return startTime;}
    public Time getEndTime() {return endTime;}
}
