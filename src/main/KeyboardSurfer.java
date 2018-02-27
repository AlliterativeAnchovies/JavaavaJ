package main;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import sprites.Player;

public class KeyboardSurfer implements KeyListener {
    public HashSet<Integer> currPressedKeys = new HashSet<>();

    //Handle the key typed event.
    public void keyTyped(KeyEvent e) {

    }

    /**
     * The main method for the HelloWorld program.
     *
     * @param e  used
     */
    //Handle the key pressed event.
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            Player.getPlayer().move(0,-4);
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            Player.getPlayer().move(0,4);
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            Player.getPlayer().move(-4,0);
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            Player.getPlayer().move(4,0);
        }
        currPressedKeys.add(e.getKeyCode());
    }

    //Handle the key released event.
    public void keyReleased(KeyEvent e) {
        currPressedKeys.remove(e.getKeyCode());
    }
}
