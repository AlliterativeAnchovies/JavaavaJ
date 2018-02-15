package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class XML {
    private XMLNode root;
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
        System.out.println(theDoc);
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
        System.out.println("yaya");

        /*//do some magic to read in the XML file, hehe..
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
        dummychild2.addChild(dummychildcolor2);*/
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

class XMLStringReader {
    private String instring;
    private int pointer;
    public XMLStringReader(String s) {
        instring = s;
        pointer = 0;
    }
    //moves pointer to point after next instance of input string
    //inputs are the string to search for, and a boolean dictating whether or not
    //to point at the end of the string or the start
    //So "potato".find("ot",false) would point at index 1 (before 'ot') and
    //"potato".find("ot",true) would point at index 3 (after 'ot')
    public void find(String s,boolean afterinstance) throws RuntimeException {
        //move pointer to point at next instance of input string
        pointer = instring.indexOf(s,pointer);
        if (pointer==-1) {
            throw new RuntimeException("Not found!");
        }
        if (afterinstance) {
            //now move it to point after it
            pointer += s.length();
        }
    }

    //searches for something of form <xxxx>, and returns xxxx.  Moves pointer to after the tag
    private String grabNextTag() {
        find("<",true);
        int start = pointer;
        find(">",false);
        int end = pointer;
        pointer+=1;//now point after the tag
        return instring.substring(start,end);
    }

    //returns true if there are potentially more attributes to find
    private boolean pointToNextAttribute() {
        if (pointer>=instring.length()) {
            return false;
        }
        while (instring.charAt(pointer+1)==' ') {
            pointer++;
            if (pointer>=instring.length()) {
                return false;
            }
        }
        return true;
    }

    //gets name of tag, assuming it is currently in the tag.
    private String getTagName() {
        int start = pointer;
        int end;
        pointToNextAttribute();
        try {
            find(" ",false);
            end = pointer;
        }
        catch (RuntimeException e) {//if there are no attributes, tag is just the whole thing
            end = instring.length();
            pointer = end;
        }
        return instring.substring(start,end).trim();
    }

    //gets attribute data from a tag, assuming it is currently in a tag
    private Pair<String,String> getAttributeData() {
        if (!pointToNextAttribute()) {return null;}//point to next attribute.  if no more attributes, end this process
        int start = pointer;
        find("\"",true);
        find("\"",true);//find second ", that's when you know you've found the end of the string
        int end = pointer;
        String[] rawdata = instring.substring(start,end).split("=");
        if (rawdata.length==1) {
            return null;
        }
        for (int i = 2;i<rawdata.length;i++) {//if you have attr="hi=hello", then splitting would
                                                //give [attr], ["hi], [hello"].  So we should fix this
            rawdata[1] += "="+rawdata[i];
        }
        for (int i = 0;i<2;i++) {//don't want to do foreach loop because some idiot decided strings are
                                                //immutable.
            rawdata[i] = rawdata[i].trim();
        }
        rawdata[1] = rawdata[1].substring(1,rawdata[1].length()-1);//trim first 2 characters off - this is to get rid of quotes
        return new Pair<String,String>(rawdata[0],rawdata[1]);
    }

    //points after the end of </tagname>, and returns the contents between pointer and </tagname>
    //assumes you are already inside of a tag.
    private String closeTag(String tagname) {
        int start = pointer;
        find("</"+tagname+">",false);
        int end = pointer;
        pointer+=("</"+tagname+">").length();
        return instring.substring(start,end);
    }

    //returns true if there are more tags.  has no side-effects
    private boolean moreTags() {
        for (;pointer<instring.length();pointer++) {
            if (instring.charAt(pointer)=='<') {
                if (pointer+1>=instring.length()) {
                    System.out.println("Warning!  Badly formed XML document, extraneous '<'!!!");
                    return false;
                }
                if (instring.charAt(pointer+1)=='/') {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    public XMLNode getNextNode() {
        XMLStringReader tagdata = new XMLStringReader(grabNextTag());
        int taginteriorstart = pointer;
        String tagname = tagdata.getTagName();
        XMLNode nextnode = new XMLNode(tagname);
        //get attributes
        while (true) {
            Pair<String,String> toadd = tagdata.getAttributeData();
            if (toadd == null) {break;}
            nextnode.addAttribute(toadd);
        }
        //now actually create the children/key and whatnots.
        XMLStringReader innerdata = new XMLStringReader(closeTag(tagname));
        while (innerdata.moreTags()) {
            XMLNode tagval = innerdata.getNextNode();
            nextnode.addChild(tagval);
        }
        innerdata.pointer = 0;
        int start = innerdata.pointer;
        int end;
        try {
            innerdata.find("<", false);
            end = innerdata.pointer;
        }
        catch (RuntimeException e) {
            end = innerdata.instring.length();
        }
        String value = innerdata.instring.substring(start,end);
        nextnode.giveValue(value);
        //check for
        return nextnode;
    }
}
