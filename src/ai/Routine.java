package ai;

import java.util.HashMap;
import main.XML;
import main.XMLNode;
import java.util.List;
import sprites.Sprite;

public class Routine {
    HashMap<String,Directive[]> routines; //this functions similar to the inner workings of Sprite in that the
                                            //array of directives is indexed by a string representing the state,
                                            //and you can swap between states to change the behavior of the ai with
                                            //this routine.
    private String curState = "Default";
    private String triggeredBy = null;

    public Directive getCurrentDirective() {
        Directive[] curStateDirective = routines.get(curState);
        int curindx = getCurDirectiveIndex(curStateDirective);
        if (curindx<0) {return null;}
        return curStateDirective[curindx];
    }
    private static int getCurDirectiveIndex(Directive[] d) {
        //calculate which index in the input list holds the current directive.
        //this will be based on time (since all directives have a start and end time,
        //this needs to figure out where the current game time fits in this list.

        for (int i = 0;i<d.length;i++) {
            if (!d[i].getStartTime().isFuture()&&!d[i].getEndTime().isPast()) {
                return i;
            }
        }
        return -1;
    }

    public Routine(HashMap<String,Directive[]> r) {
        routines = r;
    }

    //given a filepath to an xml file, it creates the routine!
    public static Routine parseRoutine(String filepath) {
        XMLNode routine = (new XML(filepath)).getRoot();
        List<XMLNode> listofroutines = routine.getChildrenWithKey("Routine");
        HashMap<String,Directive[]> toCreate = new HashMap<>();
        //go through all the routines
        for (XMLNode r : listofroutines) {
            String routinename = r.getAttributeWithName("name");
            List<XMLNode> directives = r.getChildrenWithKey("Directive");
            Directive[] ds = new Directive[directives.size()];
            //create the directives for the routine
            for (int i = 0;i<ds.length;i++) {
                XMLNode relevant = directives.get(i);
                Directive direc = new Directive(relevant);
                ds[i] = direc;
            }
            toCreate.put(routinename,ds);
        }
        Routine toReturn = new Routine(toCreate);
        return toReturn;
    }

    //changes routine state
    public void changeState(String to,String cause) {
        curState = to;
        triggeredBy = cause;
    }

    //returns what triggered the routine
    public String getCause() {
        return triggeredBy;
    }

}
