package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class XML {
    private XMLNode root;

    //this is the constructor.  It is magical, it will take in a filepath and turn it into XML data
    public XML(String filepath) {
        Scanner sc = null;
        try {
            sc = new Scanner(new File(filepath));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String theDoc = "";
        while(sc.hasNextLine()) {
            String str = sc.nextLine();
            theDoc = theDoc+str.trim();
        }
        //System.out.println(theDoc);
        XMLStringReader xmldoc = new XMLStringReader(theDoc);
        try {
            xmldoc.find("<?xml",true);
            xmldoc.find("?>",true);
        }
        catch (RuntimeException e) {
            System.out.println("ERROR - XML Doc has no <?xml version=.....?> header!");
            return;
        }
        root = xmldoc.getNextNode();

    }

    //gets the root of the tree
    public XMLNode getRoot() {return root;}
}

