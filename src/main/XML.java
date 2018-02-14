package main;

import java.io.File;
import java.util.*;

public class XML {
    private XMLNode root;
    public XML(String filepath) {
        File file = new File(filepath);
        //do some magic to read in the XML file, hehe..
        //for now, just some dumb values
        root = new XMLNode("root","");
        XMLNode dummychild;
        XMLNode dummychildcolor;
        dummychild = new XMLNode("Tile");
        dummychild.addAttribute("id","0");
        dummychildcolor = new XMLNode("color","0xff00ff00");
        dummychild.addChild(dummychildcolor);
        root.addChild(dummychild);
        XMLNode dummychild2;
        XMLNode dummychildcolor2;
        dummychild2 = new XMLNode("Tile");
        dummychild2.addAttribute("id","1");
        dummychildcolor2 = new XMLNode("color","0xffff0000");
        dummychild2.addChild(dummychildcolor2);
    }
    public XMLNode getRoot() {return root;}
    public static void main(String[] args) {
        //this is just for testing purposes...
        System.out.println("Starting test of XML Class...");
        XML xml = new XML("Resources/tiledate.xml");
        System.out.println(xml.getRoot().getChildWithKey("Tile").getChildWithKey("color").getValue());
        System.out.println("XML Class Test Over.");
    }
}

