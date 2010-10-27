package metrobotics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * @author Pablo Munoz - Metrobotics
 * this class is a panel with the metrobotics icon and the name of the application.
 *
 */
public class Title extends JPanel {

	public Title() {
		super();
		//Toolkit tk = Toolkit.getDefaultToolkit();
		int Width = 360; //(int)(tk.getScreenSize().getWidth() * 0.30);//300);
		int Height = 100; // (int)(tk.getScreenSize().getHeight() * 0.10);//100);
	    Dimension d = new Dimension(Width, Height);
	    setPreferredSize(d);
	    setBorder(BorderFactory.createRaisedBevelBorder());
		setBackground(Color.gray);
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("resources/metrobotics-large-v5.gif"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		g.drawImage(img, 10, 0, 290, 50, this);
		g.setFont(new Font("SansSerif", Font.BOLD, 22));
		g.drawString("Human Robot Interface", 25, 90);
	}
}
