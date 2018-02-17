package main;

public class Physics {
    //this class should be for any physics related utility functions
    //such as normalizing vectors.  This class will be more useful depending
    //on how physicsy our game gets.  Since its top-down, this will probably
    //be a small class
    public static Pair<Double,Double> normalize(double a,double b) {
        double denominator = magnitude(a,b);
        if (denominator==0) {
            //special case, the zero vector is, because it can't be normalized.  However, that's
            //kinda fine because for most purposes we want it to 'normalize' to the zero vector again
            //so we'll just do that.
            return new Pair<Double,Double>(0.0,0.0);
        }
        double scalar = 1/denominator;
        return new Pair<Double,Double>(scalar*a,scalar*b);
    }

    public static double magnitude(double a,double b) {return Math.sqrt(a*a+b*b);}
}
