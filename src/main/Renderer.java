package main;

import sprites.Person;
import sprites.rooms.Room;

import javax.swing.JComponent;
import java.awt.*;

public class Renderer extends JComponent {

    //this is the method java calls to see what we be wanting to do
    //with our drawing stuffs.  So all the drawings happen in here, or
    //are called from here.  This method should not be called manually.
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (Main.RENDER_READY) {
            Room.drawRooms(0,0,g);
            Person.drawPeople(0,0,g);
            String timestring = Time.getTimeString();
            g.setFont(new Font("monospaced", Font.PLAIN,32));
            FontMetrics fontMetrics = g.getFontMetrics();
            g.drawString(timestring,Main.screenwidth-fontMetrics.stringWidth(timestring),Main.screenheight);
        }
    }
}
