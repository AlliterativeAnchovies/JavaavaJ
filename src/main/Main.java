package main;

import main.Renderer;
import sprites.rooms.Room;
import sprites.rooms.Tile;

import java.awt.Graphics;
import javax.swing.JFrame;

class Main {
    //-----debug constants
    static boolean RENDER_TIME = true;	//if true, will report data on how well
    //the program is adhering to the frame rate.
    //-----end of debug constants
    static boolean RENDER_READY = false;//true once everything has been loaded.
    //is used to prevent premature rendering before
    //the game is ready.
    //This is the function that the code enters
    public static void main(String[] args) {
        System.out.println("---Starting...---");

        //Initialize graphics
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(30, 30,512, 512);
        window.getContentPane().add(new Renderer());
        window.setVisible(true);

        //Initialize tiles and whatnots
        Tile.init();
        Room.init();


        //I just took this controlflow code from stack overflow hehe
        //Was too lazy to figure out how Java handles system time and whatnots.
        //Comments are my own annotations, though + slightly tweaked
        //https://stackoverflow.com/questions/18283199/java-main-game-loop
        boolean running = true;
        final int UPS = 60;//gameloops per second
        long initialTime = System.nanoTime();
        final double timeU = 1000000000.0 / UPS;//effectively just scalar to make deltaU==1 imply
        //it being time for updates
        double deltaU = 0;
        int ticks = 0;
        long timer = System.currentTimeMillis();
        RENDER_READY = true;
        while (running) {
            long currentTime = System.nanoTime();
            deltaU += (currentTime - initialTime) / timeU;//calculates how many update ticks have passed
            //(whenever >=1 tick has passed, do an update)
            initialTime = currentTime;
            if (deltaU >= 1) {//If it's time for a new update frame, update!
                update();
                ticks++;
                deltaU--;
            }
            if (RENDER_TIME) {//just debug prints
                if (System.currentTimeMillis() - timer > 1000) {
                    System.out.println(String.format("UPS: %s", ticks));
                    ticks = 0;
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
    }
}
