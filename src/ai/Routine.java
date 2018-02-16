package ai;

import java.util.HashMap;

public class Routine {
    HashMap<String,Directive[]> routines; //this functions similar to the inner workings of Sprite in that the
                                            //array of directives is indexed by a string representing the state,
                                            //and you can swap between states to change the behavior of the ai with
                                            //this routine.
    String curState = "Default";
    public Directive getCurrentDirective() {
        Directive[] curStateDirective = routines.get(curState);
        return curStateDirective[getCurDirectiveIndex(curStateDirective)];
    }
    private static int getCurDirectiveIndex(Directive[] d) {
        //calculate which index in the input list holds the current directive.
        //this will be based on time (since all directives have a start and end time,
        //this needs to figure out where the current game time fits in this list.

        //TODO: Make this actually work.
        return 0;
    }
}
