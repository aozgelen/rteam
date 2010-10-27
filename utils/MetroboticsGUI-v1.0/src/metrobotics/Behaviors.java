package metrobotics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;


/**
 * @author Pablo Munoz - Metrobotics
 * This class contains the buttons that trigger behaviors. 
 * TODO: Only one behavior is enabled, SQUARE but it only works when the GUI is talking
 * directly to Player. FIX THIS. 
 *
 */
public class Behaviors extends JPanel {
	ArrayList<Robot> robots;
	Robot inUse;
	private JLabel titleBehavior, squareBehavior, swarmBehavior, 
	findObjectBehavior, gotoBehavior, localizeBehavior, objectRecogNN;
	Behaviors(final ArrayList<Robot> robots){
		super();
		this.robots = robots;
		setBackground(Color.gray);
		// Toolkit tk = Toolkit.getDefaultToolkit();
		int Width = 200; //(int)(tk.getScreenSize().getWidth() * 0.05);
		int Height = 350; //(int)(tk.getScreenSize().getHeight() * 1);
	    Dimension d = new Dimension(Width, Height);
	    setPreferredSize(d);		
	    setBorder(BorderFactory.createRaisedBevelBorder());
	    setLayout(new FlowLayout());
	    
	    titleBehavior = new JLabel();
	    Dimension dim  = new Dimension(Width, 30);
	    titleBehavior.setPreferredSize(dim);
	    titleBehavior.setHorizontalAlignment(SwingConstants.CENTER);
	    titleBehavior.setFont(new Font("SansSerif", Font.BOLD, 26));
	    titleBehavior.setText("BEHAVIORS");
	    add(titleBehavior);
	    
	    Color backGBeh = new Color(255, 255, 255);
	    
	    squareBehavior = new JLabel("SQUARE");
	    squareBehavior.setPreferredSize(dim);
	    squareBehavior.setHorizontalAlignment(SwingConstants.CENTER);
	    squareBehavior.setOpaque(true);
	    squareBehavior.setBackground(backGBeh);
	    squareBehavior.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	    //squareBehavior.addMouseListener(new doSquare(robots));
	    add(squareBehavior);
	    
	    swarmBehavior = new JLabel("SWARM");
	    swarmBehavior.setPreferredSize(dim);
	    swarmBehavior.setHorizontalAlignment(SwingConstants.CENTER);
	    swarmBehavior.setOpaque(true);
	    swarmBehavior.setBackground(backGBeh);
	    swarmBehavior.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	    //swarmBehavior.addMouseListener(null);
	    add(swarmBehavior);
	    
	    findObjectBehavior = new JLabel("FIND OBJECT");
	    findObjectBehavior.setPreferredSize(dim);
	    findObjectBehavior.setHorizontalAlignment(SwingConstants.CENTER);
	    findObjectBehavior.setOpaque(true);
	    findObjectBehavior.setBackground(backGBeh);
	    findObjectBehavior.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	    //findObjectBehavior.addMouseListener(null);
	    add(findObjectBehavior);
	    
	    gotoBehavior = new JLabel("GO TO LOCATION");
	    gotoBehavior.setPreferredSize(dim);
	    gotoBehavior.setHorizontalAlignment(SwingConstants.CENTER);
	    gotoBehavior.setOpaque(true);
	    gotoBehavior.setBackground(backGBeh);
	    gotoBehavior.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	    //gotoBehavior.addMouseListener(null);
	    add(gotoBehavior);

	    localizeBehavior = new JLabel("LOCALIZE");
	    localizeBehavior.setPreferredSize(dim);
	    localizeBehavior.setHorizontalAlignment(SwingConstants.CENTER);
	    localizeBehavior.setOpaque(true);
	    localizeBehavior.setBackground(backGBeh);
	    localizeBehavior.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	    //localizeBehavior.addMouseListener(null);
	    add(localizeBehavior);
	    
	    objectRecogNN = new JLabel("OBJECT RECOG. NEURAL NET");
	    objectRecogNN.setPreferredSize(dim);
	    objectRecogNN.setHorizontalAlignment(SwingConstants.CENTER);
	    objectRecogNN.setOpaque(true);
	    objectRecogNN.setBackground(backGBeh);
	    objectRecogNN.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	    //objectRecogNN.addMouseListener(null);
	    add(objectRecogNN);
	}
}
