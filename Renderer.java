import javax.swing.JComponent;
import java.awt.Graphics;

class Renderer extends JComponent {

	//this is the method java calls to see what we be wanting to do
	//with our drawing stuffs.  So all the drawings happen in here, or
	//are called from here.  This method should not be called manually.
	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
    	Room.drawRooms(g);
  	}
	
}
