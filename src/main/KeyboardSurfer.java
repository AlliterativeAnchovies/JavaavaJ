package main;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import sprites.Player;

public class KeyboardSurfer implements KeyListener {
    //Handle the key typed event.
    public void keyTyped(KeyEvent e) {

    }

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
    }

    //Handle the key released event.
    public void keyReleased(KeyEvent e) {

    }
}
