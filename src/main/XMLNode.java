package main;

import java.util.ArrayList;
import java.util.List;

public class XMLNode {
    private List<XMLNode> children;
    private List<Pair<String,String>> attributes;
    private String value;
    private String key;
    public XMLNode(String k) {
        key=k;
        value="";
        children = new ArrayList<XMLNode>();
        attributes = new ArrayList<Pair<String,String>>();
    }
    public XMLNode(String k,String v) {
        key=k;
        value=v;
        children = new ArrayList<XMLNode>();
        attributes = new ArrayList<Pair<String,String>>();
    }
    public void addAttribute(Pair<String,String> attr) {
        attributes.add(attr);
    }
    public void addAttribute(String attrkey,String attrval) {
        attributes.add(new Pair<String,String>(attrkey,attrval));
    }
    public void addChild(XMLNode child) {
        children.add(child);
    }
    public List<Pair<String,String>> getAttributes() {
        return attributes;
    }
    public List<XMLNode> getChildren() {
        return children;
    }
    public XMLNode getChild(int which) {
        return getChildren().get(which);
    }
    public XMLNode getChildWithKey(String k) {
        for (XMLNode x : children) {
            if (x.key==k) {return x;}
        }
        return null;
    }
    public List<XMLNode> getChildrenWithKey(String k) {
        List<XMLNode> toReturn = new ArrayList<XMLNode>();
        for (XMLNode x : children) {
            if (x.key==k) {toReturn.add(x);}
        }
        return toReturn;
    }
    public String getAttributeWithName(String n) {
        for (Pair<String,String> a : attributes) {
            if (a.x.equals(n)) {return a.y;}
        }
        return null;
    }
    public String getValue() {
        return value;
    }
}
