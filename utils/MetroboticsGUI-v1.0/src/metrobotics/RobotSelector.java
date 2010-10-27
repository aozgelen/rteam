package metrobotics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javaclient3.PlayerClient;
import javaclient3.structures.PlayerConstants;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Scrollable;

public class RobotSelector extends JPanel implements Scrollable{
	JLabel robotInUse;
	ArrayList<Robot> robots;
	JButton [] robotButtons;
	ImageIcon [] robotIcons;
	JButton [] grabButtons;
	JButton [] ungrabButtons;
	JButton [] imageButtons;
	JButton [] videoButtons;
	JPanel buttons;
	VisionDisplay vi;
	//boolean threadVision = false;
	int Width;
	int Height;
	//boolean visionThreadStarted = false;
	
	
	
	RobotSelector(JLabel robotInUse, ArrayList<Robot> robots, VisionDisplay vision){
		super();
		this.robotInUse = robotInUse;
		this.robots = robots;
		this.vi = vision;
		setBackground(Color.gray);
		// Toolkit tk = Toolkit.getDefaultToolkit();
		Width = 130; // 120//110 (int)(tk.getScreenSize().getWidth() * 0.05);
		Height = 800; //(int)(tk.getScreenSize().getHeight() * 1);
		//Width -= 10;
	    Dimension d = new Dimension(Width, Height);
	    setPreferredSize(d);		
	    setLayout(new BorderLayout());
	    setBorder(BorderFactory.createRaisedBevelBorder());
	    
	    System.out.println(this.getPreferredSize());
	    
	    robotButtons = new JButton[robots.size()];
	    robotIcons = new ImageIcon[robots.size()];
	    grabButtons = new JButton[robots.size()];
	    ungrabButtons = new JButton[robots.size()];
		imageButtons = new JButton[robots.size()];
		videoButtons = new JButton[robots.size()];
	    int i=0;
	    buttons = new JPanel();
	    for (final Robot x : robots) {
    		//final Robot x = (Robot)e.nextElement();
    		String name = x.getName();
    		System.out.println(name);
    		robotIcons[i] = GuiUtils.resizeJP(x.getRobotIcon().getImage(), Width, Width);
    		final JLabel jl = robotInUse;
    		if(name==null){
    			name = "No Name";
    		}
    		robotButtons[i] = new JButton(name, robotIcons[i]);
    		robotButtons[i].setVerticalTextPosition(AbstractButton.TOP);
    		robotButtons[i].setHorizontalTextPosition(AbstractButton.CENTER);
    		if(!Gui.useCentralServer){
	    		robotButtons[i].addActionListener(new ActionListener() {
					//private boolean threadVision;
					//private boolean threadVisionAibo;
					
	
					public void actionPerformed(ActionEvent e) {
						// TODO Think about how to use the key in robots hashtable
	    	        	System.out.println(x.getName() + " " + Robot.getRobotInUse());
	    	        	Robot.setRobotInUse(x.getRobotKey());
	    	        	System.out.println(x.getName() + " " + Robot.getRobotInUse());
	    	        	jl.setText(x.getName());
	    	        	
	    	        	vi.showVideo(x);
	    	        	
	    	        	
					}

					
	    	    });
	    		buttons.add(robotButtons[i]);
    		}

    		else {
    		grabButtons[i] = new JButton("Lock");
    		grabButtons[i].setFont(new Font("SansSerif", Font.PLAIN, 9));
    		grabButtons[i].setPreferredSize(new Dimension(Width/3 + 15, 25));  // 3 15
    		grabButtons[i].setVerticalTextPosition(AbstractButton.TOP);
    		grabButtons[i].setHorizontalTextPosition(AbstractButton.CENTER);
    		grabButtons[i].addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				Gui.serverComm.writeStream("LOCK " + x.getUniqueId());
    				Robot.setRobotInUse(x.getRobotKey());
    				Robot.setRobotInUseVideo(x.getRobotKey());
    				vi.showVideo(x);
    				jl.setText(x.getName());
//    				if(Gui.serverComm.sendReqGrab()){
//    					System.out.println(x.getName() + " " + Robot.getRobotInUse());
//	    	        	Robot.setRobotInUse(x.getRobotKey());
//	    	        	System.out.println(x.getName() + " " + Robot.getRobotInUse());
//	    	        	jl.setText(x.getName());
//	    	        	grabButtons[x.getRobotKey()].setBackground(Color.green);
//	    	        	ungrabButtons[x.getRobotKey()].setBackground(Color.red);
//    				}
//    				else{
//    					jl.setText("Robot is Locked");
//    				}
    			}
    		});
    		
    		ungrabButtons[i] = new JButton("Unlock");
    		ungrabButtons[i].setFont(new Font("SansSerif", Font.PLAIN, 9));
    		ungrabButtons[i].setPreferredSize(new Dimension(Width/3 + 15, 25)); // 3 15
    		ungrabButtons[i].setVerticalTextPosition(AbstractButton.TOP);
    		ungrabButtons[i].setHorizontalTextPosition(AbstractButton.CENTER);
    		ungrabButtons[i].addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				Gui.serverComm.writeStream("UNLOCK " + x.getUniqueId());
    				Robot.setRobotInUse(-1);
    				//unlockAll();
    				//Robot.setRobotInUseVideo(-1);
    				jl.setText("NO Robot in use");
//    				if(Gui.serverComm.sendReqUnGrab()){
//    					System.out.println(x.getName() + " " + Robot.getRobotInUse());
//	    	        	Robot.setRobotInUse(x.getRobotKey());
//	    	        	System.out.println(x.getName() + " " + Robot.getRobotInUse());
//	    	        	jl.setText(x.getName());
//    				}
//    				else{
//    					jl.setText("Robot is Locked");
//    				}
    			}
    		});
    		
    		imageButtons[i] = new JButton("Image");
    		imageButtons[i].setFont(new Font("SansSerif", Font.PLAIN, 9)); // 8
    		imageButtons[i].setPreferredSize(new Dimension(Width/3 + 15, 25)); // 3 15
    		imageButtons[i].setVerticalTextPosition(AbstractButton.TOP);
    		imageButtons[i].setHorizontalTextPosition(AbstractButton.CENTER);
    		// TODO: This needs to be implemented
    		
    		videoButtons[i] = new JButton("Video");
    		videoButtons[i].setFont(new Font("SansSerif", Font.PLAIN, 9));  // 8
    		videoButtons[i].setPreferredSize(new Dimension(Width/3 + 15, 25));  // 3 15
    		videoButtons[i].setVerticalTextPosition(AbstractButton.TOP);
    		videoButtons[i].setHorizontalTextPosition(AbstractButton.CENTER);
    		videoButtons[i].addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				if(x.getHasCamera()){
    					// These two lines are so we can see the video even if it is on loider. 
    					unlockAll();
    					Robot.setRobotInUseVideo(x.getRobotKey());
        				jl.setText("Video:" + x.getName());
    					vi.showVideo(x);
    				}
    				else{
    					// Now this is done after the Robot is registered. 
    					// I have this here just in case we don't have this info for some reason.
    					Gui.serverComm.writeStream("ASKPLAYER " + x.getUniqueId());
    					// TODO: What does the GUI should do until we get the IP and port from the server? Pablo
    				}
    			}

				
    		});
    		
    		// Wire the button with action listener that contains Robot key
    		
    		buttons.add(robotButtons[i]);
    		buttons.add(grabButtons[i]);
    		buttons.add(ungrabButtons[i]);
    		buttons.add(imageButtons[i]);
    		buttons.add(videoButtons[i]);
    		}
    		
    		i++;
    		
    		
        }
	    add(buttons);
	    validate();
	    
	}

	public Dimension getPreferredScrollableViewportSize() {
		return null;
	}

	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		return 0;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		return 0;
	}
	
	
	private void unlockAll() {
		for (Robot x : robots) {
			Gui.serverComm.writeStream("UNLOCK " + x.getUniqueId());
			Robot.setRobotInUse(-1);
			Robot.setRobotInUseVideo(-1);
			// Kill Cameras
			Robot.setAiboCameraThread(false);
			Robot.setCameraThread(false);
		}
	}
	
	

}
