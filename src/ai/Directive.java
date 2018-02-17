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
    private List<Tile> path;//list of key tiles that have to be reached
                    //(they do not have to be contiguous - the class methods will
                    //be in charge of giving contiguous directions)
    private int progress;//keeps track of how many key points in the path the npc has reached so far
                //(so it's an index for path)
    private List<String> toSay;
    private int talkingprogress;
    private List<String> talkingSpeed;
    //Note: game should calculate how fast the NPC should walk such that,
    //assuming no interruptions, they reach the end of the path right at endTime.


    public Directive(XMLNode x) {
        //parses the xml tree to create directive
        //the part of the tree this receives is  the <Directive> tags
        String s_ = x.getAttributeWithName("start");
        String e_ = x.getAttributeWithName("end");
        startTime = new Time(s_);
        endTime = new Time(e_);
        toSay = new ArrayList<String>();
        talkingSpeed = new ArrayList<>();
        List<XMLNode> speech = x.getChildrenWithKey("Say");
        for (XMLNode s : speech) {
            List<String> toAdd = Arrays.asList(s.getValue().split("\\|"));
            List<String> durs = new ArrayList<String>();
            String durtoadd = s.getAttributeWithName("seconds");
            for (String thisisdumb : toAdd) {//why doesn't java have a constructor to fill with default values?
                durs.add(durtoadd);
            }
            talkingSpeed.addAll(durs);
            toSay.addAll(toAdd);
        }
        talkingprogress = 0;
    }

    //returns a list of directions, of the form:
    // [<"Say","What to say...">,<"Move","1,2">,...]
    // So a list of pairs where the first in pair is type of direction and the second
    // is easily parseable material detailing the specifics.
    public List<Pair<String,String>> getDirections() {
        List<Pair<String,String>> toReturn = new ArrayList<Pair<String,String>>();
        if (toSay.size()>talkingprogress) {
            toReturn.add(new Pair<String,String>("Say",talkingSpeed.get(talkingprogress)+"|"+toSay.get(talkingprogress)));
        }
        return toReturn;
    }

    public void talkingCallback() {
        talkingprogress++;
    }

    public Time getStartTime() {return startTime;}
    public Time getEndTime() {return endTime;}
}
