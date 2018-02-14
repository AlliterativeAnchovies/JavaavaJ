package main;

import sprites.rooms.Room;

import javax.swing.JComponent;
import java.awt.Graphics;

public class Renderer extends JComponent {

    //this is the method java calls to see what we be wanting to do
    //with our drawing stuffs.  So all the drawings happen in here, or
    //are called from here.  This method should not be called manually.
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println(Main.RENDER_READY);
        if (Main.RENDER_READY) {
            Room.drawRooms(g);
        }
    }
}
