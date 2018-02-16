package ai;
import java.util.*;
import sprites.rooms.Tile;
import main.Time;

public class Directive {
    private Time startTime;
    private Time endTime;
    private String inflictedStatus;
    private List<Tile> path;//list of key tiles that have to be reached
                    //(they do not have to be contiguous - the class methods will
                    //be in charge of giving contiguous directions)
    private int progress;//keeps track of how many key points in the path the npc has reached so far
                //(so it's an index for path)
    //Note: game should calculate how fast the NPC should walk such that,
    //assuming no interuptions, they reach the end of the path right at endTime.
    //Note 2: Directives ideally should be defined in a data-driven manner (that is,
    //the code reads them in from text/xml files)

    //given the position of an npc, this will return a list of directions that the NPC should travel along (in the form
    //of relative positions.  So if this returned [(1,0),(0,3)], the NPC would try to move 1 right and then 3 up.)
    //Shouldn't give directions too far in ahead though, because it likely will have to recalculate paths.
    public List<int[]> getDirections(int x,int y) {return null;};
    /*
    <rant>
    First of all, why the hell can generics not take raw types? (but yet they can take arrays of raw types... heh)
    That is such a dumb decision that could be easily fixed without sacrificing any runtime speed.  Like, really,
    what the fudge Java.
    Secondly, why can I not declare functions without defining them? (baring making it and the class it is in 'abstract')
    If it's really that much of a problem, just make the compiler ignore function declarations without definitions.  They're
    really useful for being able to go ahead and write out everything you need for the class without implementing it.
    </rant>
     */
}
