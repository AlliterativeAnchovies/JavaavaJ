package ai;
import java.util.*;
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
    //Note: game should calculate how fast the NPC should walk such that,
    //assuming no interruptions, they reach the end of the path right at endTime.
    //Note 2: Directives ideally should be defined in a data-driven manner (that is,
    //the code reads them in from text/xml files)

    //given the position of an npc, this will return a list of directions that the NPC should travel along (in the form
    //of relative positions.  So if this returned [(1,0),(0,3)], the NPC would try to move 1 right and then 3 up.)
    //Shouldn't give directions too far in ahead though, because it likely will have to recalculate paths.
    //public List<int[]> getDirections(int x,int y) {return null;};

    public Directive(XMLNode x) {
        //parses the xml tree to create directive
        //the part of the tree this receives is  the <Directive> tags
        String s_ = x.getAttributeWithName("start");
        String e_ = x.getAttributeWithName("end");
        startTime = new Time(s_);
        endTime = new Time(e_);
        toSay = new ArrayList<String>();
        List<XMLNode> speech = x.getChildrenWithKey("Say");
        for (XMLNode s : speech) {
            toSay.addAll(Arrays.asList(s.getValue().split("\\|")));
        }
        talkingprogress = 0;
    }

    public List<Pair<String,String>> getDirections() {
        List<Pair<String,String>> toReturn = new ArrayList<Pair<String,String>>();
        if (toSay.size()>talkingprogress) {
            toReturn.add(new Pair<String,String>("Say",toSay.get(talkingprogress)));
            talkingprogress++;
        }
        return toReturn;
    }

    public Time getStartTime() {return startTime;}
    public Time getEndTime() {return endTime;}
}
