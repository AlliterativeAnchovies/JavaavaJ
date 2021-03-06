package main;

import main.Renderer;
import sprites.*;
import sprites.rooms.Room;
import sprites.rooms.Tile;

import java.awt.Graphics;
import javax.swing.JFrame;

public class Main {
    //-----debug constants
    static boolean RENDER_TIME = true;	//if true, will report data on how well
                                        //the program is adhering to the frame rate.
    //-----end of debug constants
    static boolean RENDER_READY = false;//true once everything has been loaded.
                                        //is used to prevent premature rendering before
                                        //the game is ready.
    static boolean UPDATE_READY = false;//same deal as render_ready
    public static Renderer renderer;
    static int screenwidth;
    static int screenheight;
    public static final int UPS = 60;//gameloops per second
    public static final int FPS = 60;//frame refreshes per second
    public static int totalticks = 0;
    public static int totalframes = 0;
    //This is the function that the code enters
    public static void main(String[] args) {
        System.out.println("---Starting...---");

        //Initialize graphics
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        screenwidth = 32*32;
        screenheight = 32*24;
        window.setBounds(30, 30,screenwidth, screenheight);
        renderer = new Renderer();
        window.getContentPane().add(renderer);
        window.getContentPane().validate();
        window.setVisible(true);
        window.addKeyListener(new KeyboardSurfer());

        //Initialize classes
        Time.init();
        Sprite.init();
        Tile.init();
        Room.init();
        NPC.init();//NPC must be init-ed before Person b/c Person.init() does all the loading
        Person.init();//of peeps and thus needs to know about NPC's data.
        Item.init();
        Attack.init();



        //I just took this controlflow code from stack overflow hehe
        //Was too lazy to figure out how Java handles system time and whatnots.
        //Comments are my own annotations, though + slightly tweaked
        //https://stackoverflow.com/questions/18283199/java-main-game-loop
        boolean running = true;
        long initialTime = System.nanoTime();
        final double timeU = 1000000000.0 / UPS;//effectively just scalar to make deltaU==1 imply
        final double timeF = 1000000000.0 / FPS;
        //it being time for updates
        double deltaU = 0;
        double deltaF = 0;
        int ticks = 0;
        int frames = 0;
        long timer = System.currentTimeMillis();
        RENDER_READY = true;
        while (running) {
            long currentTime = System.nanoTime();
            deltaU += (currentTime - initialTime) / timeU;//calculates how many update ticks have passed
            deltaF += (currentTime - initialTime) / timeF;//calculates how many update ticks have passed
            //(whenever >=1 tick has passed, do an update)
            initialTime = currentTime;
            if (!UPDATE_READY) {
                deltaU = 0;
            }
            if (deltaU >= 1) {//If it's time for a new update, update
                Time.tick();
                renderer.update();
                update();
                ticks++;
                totalticks++;
                deltaU--;
            }
            if (deltaF >= 1) {//refresh the screen
                renderer.repaint();
                frames++;
                totalframes++;
                deltaF--;
            }

            if (RENDER_TIME) {//just debug prints
                if (System.currentTimeMillis() - timer > 1000) {
                    System.out.println(String.format("UPS: %s FPS: %s", ticks, frames));
                    ticks = 0;
                    frames = 0;
                    timer += 1000;
                }
            }
        }
        //give a clear ending message for now so we can tell if the program ended naturally
        //or if some crash prevented it from reaching here.  (usually should be obvious in the
        //context, but you never know...)
        System.out.println("---Ending...---");
    }

    public static void update() {
        //this handles game logic!
        Person.staticUpdate();
    }

    public static void rendererLoaded() {
        UPDATE_READY = true;
    }
}
