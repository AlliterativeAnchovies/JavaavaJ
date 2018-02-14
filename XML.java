import java.io.File;
import java.util.*;
class XML {
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

class Pair<X,Y> {//wtf why does Java not have pair?
	public X x;
	public Y y;
	public Pair(X x_,Y y_) {
		x = x_;
		y = y_;
	}
}

class XMLNode {
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
	List<Pair<String,String>> getAttributes() {
		return attributes;
	}
	List<XMLNode> getChildren() {
		return children;
	}
	XMLNode getChild(int which) {
		return getChildren().get(which);
	}
	XMLNode getChildWithKey(String k) {
		for (XMLNode x : children) {
			if (x.key==k) {return x;}
		}
		return null;
	}
	String getValue() {
		return value;
	}
}
