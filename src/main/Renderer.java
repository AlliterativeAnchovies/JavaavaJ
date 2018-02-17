package main;

import ai.Directive;
import ai.Routine;
import sprites.Person;
import sprites.rooms.Room;

import javax.swing.JComponent;
import java.util.List;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Font;

public class Renderer extends JComponent {
    List<SpeechBubble> bubbles = new ArrayList<SpeechBubble>();

    //this is the method java calls to see what we be wanting to do
    //with our drawing stuffs.  So all the drawings happen in here, or
    //are called from here.  This method should not be called manually.
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (Main.RENDER_READY) {
            Main.rendererLoaded();
            int offsetx = 0;
            int offsety = 0;
            Room.drawRooms(offsetx,offsety,g);
            Person.drawPeople(offsetx,offsety,g);
            String timestring = Time.getTimeString();
            g.setFont(new Font("monospaced", Font.PLAIN,32));
            FontMetrics fontMetrics = g.getFontMetrics();
            g.drawString(timestring,Main.screenwidth-fontMetrics.stringWidth(timestring),Main.screenheight);
            for (SpeechBubble s : bubbles) {
                s.draw(offsetx,offsety,g);
            }
        }
    }

    public void update() {
        //update speech bubbles
        List<SpeechBubble> toRemove = new ArrayList<>();
        for (SpeechBubble s : bubbles) {
            if (s.tick()) {
                toRemove.add(s);
            }
        }
        //remove speech bubbles that have been completed
        for (SpeechBubble s: toRemove) {
            bubbles.remove(s);
        }
    }

    //Obvious parameters: A person and a string.  This method will display a speech bubble over the heads
    //of the talker, containing toSay.
    //The speech bubble will remain for duration game ticks (note: not frame ticks)
    //and once that duration is over, it will send a message back to the talker and the callback telling them
    //that the talking is over.
    public void makeSpeechBubble(Person talker, Directive callback, String toSay, int duration) {
        bubbles.add(new SpeechBubble(talker,callback,toSay,duration));
    }
}

class SpeechBubble {
    private Person callbackperson;
    private Directive callbackroutine;
    private String contents;
    private int duration;

    public SpeechBubble(Person p,Directive r,String s,int i) {
        callbackperson = p;
        callbackroutine = r;
        contents = s;
        duration = i;
    }

    public boolean tick() {
        duration--;
        if (duration<=0) {
            callbackroutine.talkingCallback();
            callbackperson.stopSpeaking();
            return true;
        }
        return false;
    }

    public void draw(int offsetx,int offsety,Graphics g) {
        g.setFont(new Font("monospaced", Font.PLAIN,16));
        g.drawString(contents,offsetx+callbackperson.getPositionX(),offsety+callbackperson.getPositionY());
    }
}