package metrobotics;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

/**
 * @author Pablo Munoz - Metrobotics
 * This class is in charge of the main Frame.
 * TODO: Improve the Layout and Scroll Panes 
 * Pablo. 
 *
 */
public class MainFrame extends JFrame{
	RobotSelector robotSel;
	Grid grid;
	volatile ArrayList<Robot> robots;
	PlayerJoy playerJoy;
	JLabel RobotInUseLabel;
	VisionDisplay vision;
	Title metroTitle;
	Behaviors behaviors;
	GridBagConstraints c;
	Container mainContent;
	JTextField userMsg;
	JLabel guiId;
	JLabel messageFromServer;
	JScrollPane scrollPane, scrollPaneGrid;
	JScrollBar bar;
	volatile boolean moving;
	
	MainFrame(String title, ArrayList<Robot> robots){
		super(title);
		Dimension d = new Dimension(1100, 800); //750 was working fine. make it bigger on 09/18/2010 to fit the goto panel;
		this.setResizable(false);
	    setMinimumSize(d);
	    this.robots = robots;
	 
	    mainContent = this.getContentPane();
	    mainContent.setLayout(new GridBagLayout());
	    
	    
	    // CLEAN THIS!!!
	    // Old Layout 
	    if(Gui.layoutSelection == GUIConstants.LAYOUT_ORIGINAL){
		    metroTitle = new Title();
		    RobotInUseLabel = new JLabel();
			vision = new VisionDisplay(robots, 400, 350); // 450, 350
			robotSel = new RobotSelector(RobotInUseLabel, robots, vision);
			behaviors = new Behaviors(robots);
			grid = new Grid(this, 750, 325);
			playerJoy = new PlayerJoy(RobotInUseLabel, robots, grid);
			guiId = new JLabel("GUI ID :" + Gui.getGUIId());
			messageFromServer = new JLabel("Last Message from Server: EMPTY");
		    originalLayout();
	    }
	    else if(Gui.layoutSelection == GUIConstants.LAYOUT_BIGMAP){
			metroTitle = new Title();
			RobotInUseLabel = new JLabel();
			vision = new VisionDisplay(robots, 400, 350);
			robotSel = new RobotSelector(RobotInUseLabel, robots, vision);
			behaviors = new Behaviors(robots);
			grid = new Grid(this, 650, this.getHeight() -150);
			System.out.println(this.getHeight());
			playerJoy = new PlayerJoy(RobotInUseLabel, robots, grid);
			guiId = new JLabel("GUI ID :" + Gui.getGUIId());
			messageFromServer = new JLabel("Last Message from Server: EMPTY");
	    	BigMapLayout();
	    }
	    
	    
		
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // JP: Before closing you should close all the connections and databases your are using
	    //setResizable(false);
	    setVisible(true);
		pack();
	}
	
	

	private void sendString(){
		Gui.serverComm.writeStream(userMsg.getText());
	}
//	private void updateRobots(){
//		robots = Gui.serverComm.requestGlobalState();
//		System.out.println("Success");
//	}
	
	
	private void BigMapLayout() {
		
		c = new GridBagConstraints();
		
			
		c.fill = GridBagConstraints.NONE; // FIRST_LINE_START; //BELOW_BASELINE_LEADING;
		
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
		mainContent.add(metroTitle, c);
		
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 1;
		mainContent.add(vision, c);
		
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 3;
		mainContent.add(playerJoy, c);
		
		c.gridx = 4;
		c.gridy = 0;
		c.gridwidth = 4;
		c.gridheight = 4;
		mainContent.add(grid, c);
	}
	
	private void originalLayout() {
		c = new GridBagConstraints();
	    
		// Title
	    c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
	    mainContent.add(metroTitle, c);
	    
	    c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 1;
	    Dimension dim  = new Dimension(200, 40);
	    RobotInUseLabel.setPreferredSize(dim);
	    RobotInUseLabel.setHorizontalAlignment(SwingConstants.CENTER);
		RobotInUseLabel.setText("No Robot In Use");
		
		mainContent.add(RobotInUseLabel, c);
		
		scrollPane = new JScrollPane(robotSel);
		scrollPane.setPreferredSize(new Dimension(120, 600));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getViewport().setPreferredSize(new Dimension(130, 1000));
//		bar = scrollPane.getVerticalScrollBar();
//		bar.addAdjustmentListener(new AdjustmentListener(){
//			public void adjustmentValueChanged(AdjustmentEvent e) {
//				scrollPane.repaint();
//			}
//			
//		});
//		scrollPane.validate();
		
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.gridheight = 5;
		c.gridx = 2;
		c.gridy = 1;
		mainContent.add(scrollPane, c); // scrollPane, c); // robotSel, c); // robotSel, c);
		
		

		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 2;
		c.gridheight = 4;
		c.gridx = 0;
		c.gridy = 2;
		mainContent.add(behaviors, c);
		
		// GRID		
		scrollPaneGrid = new JScrollPane(grid);
		scrollPaneGrid.setPreferredSize(new Dimension(750, 325));
		scrollPaneGrid.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneGrid.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPaneGrid.getViewport().setPreferredSize(new Dimension(1000, 500));
		
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 4;
		c.gridheight = 4;
		c.gridx = 3;
		c.gridy = 0;
		mainContent.add(scrollPaneGrid, c);
	
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridheight = 2;
		c.gridx = 3;
		c.gridy = 4;
	    mainContent.add(vision, c);
	    
	    c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridheight = 2;
		c.gridx = 5;
		c.gridy = 4;
		mainContent.add(playerJoy, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 7;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 7;
		JPanel message = new JPanel();
		Dimension dimen = new Dimension(1100, 40);
		message.setPreferredSize(dimen);
		message.setBackground(Color.white);
		if(Gui.useCentralServer){
			userMsg = new JTextField();
			userMsg.setText("Message to Central Server");
			userMsg.setPreferredSize(new Dimension(100, 25));
			message.add(userMsg);
			JButton sendServer = new JButton("Send to Server");
			sendServer.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					sendString(); 
				}
				
			});
	
			JButton reqGlobalState = new JButton("Request Global State");
			reqGlobalState.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String ident = "IDENT";
					Gui.serverComm.writeStream(ident);
					
					// This is working
					//updateRobots();
					//mainContent.remove(robotSel);
					//mainContent.remove(grid);
					//mainContent.validate();
					//newRobotSelAndGrid();
				}
			});
			
			
			
			message.add(sendServer);
			message.add(reqGlobalState);
			message.add(guiId);
			message.add(messageFromServer);
		}
		mainContent.add(message, c);
		
		mainContent.validate();
		
		setFocusable(true);
		addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {
				requestFocusInWindow();	
				System.out.println("Focus on component");
			}
			public void mouseEntered(MouseEvent e) {
			}
			public void mouseExited(MouseEvent e) {
			}
			public void mousePressed(MouseEvent e) {
			}
			public void mouseReleased(MouseEvent e) {
			}
		});
		addKeyListener(new KeyListener() {
			// boolean moving = false;
			public void keyTyped(KeyEvent e){
				System.out.println("Key Typed: " + e.getKeyChar());
			}
			
			public void keyPressed(KeyEvent e) {	
				System.out.println("Key Pressed: " + (int)e.getKeyChar() + " " + e.getKeyCode());
				handleControl(e);
			}

			public void keyReleased(KeyEvent e) {
				System.out.println("Key Released: " + e.getKeyChar());
				moving = false;
				playerJoy.sendMove(playerJoy.STOP);
			}

			public void handleControl(KeyEvent e){
				System.out.println("In HandleControl");
				int keycode = e.getKeyCode();
				if ( keycode == KeyEvent.VK_W || keycode == KeyEvent.VK_UP){
					if(moving == false){
						System.out.println("UP");
						playerJoy.sendMove(playerJoy.FORWARD);
					}
					moving = true;
				}
				else if ( keycode == KeyEvent.VK_A || keycode == KeyEvent.VK_LEFT ){
					if(moving == false){
						System.out.println("LEFT");
						playerJoy.sendMove(playerJoy.LEFT);
					}
					moving = true;
				}
				else if ( keycode == KeyEvent.VK_D || keycode == KeyEvent.VK_RIGHT ){
					if(moving == false){
						playerJoy.sendMove(playerJoy.RIGHT);
						System.out.println("RIGHT");
					}
					moving = true;
				}
				else if ( keycode == KeyEvent.VK_W || keycode == KeyEvent.VK_DOWN ){
					if(moving == false){
						playerJoy.sendMove(playerJoy.BACK);
						System.out.println("DOWN");
					}
					moving = true;
				}
			}
		});
	}
	public void newRobotSelAndGrid(){
		this.remove(scrollPane);
		scrollPane.removeAll();
		
		robotSel = new RobotSelector(RobotInUseLabel, robots, vision);
		scrollPane = new JScrollPane(robotSel);
		scrollPane.setPreferredSize(new Dimension(120, 600));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setViewportView(robotSel);
		scrollPane.getViewport().setPreferredSize(new Dimension(130, 900));
		scrollPane.validate();
		
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.gridheight = 5;
		c.gridx = 2;
		c.gridy = 1;
		mainContent.add(scrollPane, c);
		

//		c.fill = GridBagConstraints.BOTH;
//		c.gridwidth = 4;
//		c.gridheight = 4;
//		c.gridx = 3;
//		c.gridy = 0;
//		grid = new Grid(RobotInUseLabel, robots);
//		mainContent.add(grid, c);
		
		mainContent.validate();
	}
}
