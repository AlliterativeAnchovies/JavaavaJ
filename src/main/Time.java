package main;

public class Time {
    static int curhour;
    static int curminute;
    static int curticks;
    static final int ticksperminute = 3600;//60 ticks per second, 60 seconds per minute = 3600 ticks per minute
                                            //if we're doing real time.  We'll probably not want to do real time,
                                            //so instead we can adjust this value (for example, if real time 1 min =
                                            //game time 5 min, then set this equal to 3600/5 = 720.)

    //Create a timestamp
    int instancehour;
    int instanceminute;
    public Time(int h,int m) {
        instancehour = h;
        instanceminute = m;
    }

    //Sets the time to the listed inputs.
    static void setTime(int hour,int minute) {
        curhour = hour;
        curminute = minute;
        curticks = 0;
    }

    //Call this function when initializing everything while loading the game
    static void init() {
        setTime(8,30);//just a random time, seems 8:30 would be a good time to start...
    }

    //call this in the game update loop to handle time keeping up with the game properly
    static void tick() {
        curticks++;
        if (curticks>=ticksperminute) {
            curticks %= ticksperminute;
            curminute++;
            if (curminute>=60) {
                curminute%=60;
                curhour++;
            }
        }
    }

    static int getHour() {return curhour;}
    static int getCurminute() {return curminute;}

    //Returns the time in format HH:MM
    //For example, 8:07 is 08:07.
    static String getTimeString() {
        String minutestring = Integer.toString(curminute);
        if (minutestring.length()<2) {minutestring="0"+minutestring;}
        String hourstring = Integer.toString(curhour);
        if (minutestring.length()<2) {hourstring="0"+hourstring;}
        return hourstring+":"+minutestring;
    }

    //instance method to check if timestamp is the present.
    boolean isPresent() {
        return curhour==instancehour&&curminute==instanceminute;
    }

    //instance method to check if timestamp is the past.
    boolean isPast() {
        if (instancehour<curhour) {return true;}
        else if (instancehour>curhour) {return false;}
        else {return instanceminute<curminute;}
    }

    //instance method to check if timestamp is the future.
    boolean isFuture() {
        return !isPresent()&&!isPast();//I'm lazy
    }
}
