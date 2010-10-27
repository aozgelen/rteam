package metrobotics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * @author Pablo Mu�oz - Metrobotics 
 * This class displays the arrows and textfields to send movement commands to the server,
 * Player or directly to the robots.
 * TODO: I disabled sending commands to Player and directly to the Robot. This needs to be
 * enabled and test it
 * Pablo.
 *
 */
public class PlayerJoy extends JPanel{
	JLabel robotInUse;
	ArrayList<Robot> robots;
	JButton forward, atras, rotateLeft, rotateRight, goBut, gotoGoBut, stopBut;
	Grid grid;
	JLabel currentPower;
	JScrollBar powerBar;
	JTextField linVelUs, angVelUs, durUs;
	public final int FORWARD = 1;
	public final int BACK = 2;
	public final int LEFT = 3;
	public final int RIGHT = 4;
	public final int STOP = 5;
	public final int ADVANCED = 6;

	PlayerJoy(JLabel robotInUse, ArrayList<Robot> robots, Grid grid){
		super();
		this.robotInUse = robotInUse;
		this.robots = robots;
		this.grid = grid;
		setBackground(Color.gray);
		//Toolkit tk = Toolkit.getDefaultToolkit();
		int Width = 400;//(int)(tk.getScreenSize().getWidth() * 0.15);//0.33);
		int Height = 350;//(int)(tk.getScreenSize().getHeight() * 0.15);//0.33);
	    Dimension d = new Dimension(Width, Height);
	    setPreferredSize(d);		
	    setBorder(BorderFactory.createRaisedBevelBorder());
	    setLayout(new FlowLayout());
	    
	    JPanel basic2DPanel = new JPanel();
	    Dimension basicD = new Dimension(161, 155);
	    basic2DPanel.setPreferredSize(basicD);
	    basic2DPanel.setLayout(new BorderLayout());
	    ImageIcon fwd = GuiUtils.resizeJP(new ImageIcon ("resources/arrowFwd.jpg").getImage(), Width/8, Height/8);
		forward = new JButton(fwd);
		forward.setPreferredSize(new Dimension(Width/8, Height/8));
		JPanel fwdPanel = new JPanel();
		fwdPanel.add(forward);
		basic2DPanel.add(fwdPanel, BorderLayout.NORTH);
		
		ImageIcon bk = GuiUtils.resizeJP(new ImageIcon("resources/arrowBck.jpg").getImage(), Width/8, Height/8);
		atras = new JButton(bk);
		//atras.addMouseListener(new BackButt(robots, grid));
		atras.setPreferredSize(new Dimension(Width/8, Height/8));
		JPanel atrPanel = new JPanel();
		atrPanel.add(atras);
		basic2DPanel.add(atrPanel, BorderLayout.SOUTH);
		
		ImageIcon lft = GuiUtils.resizeJP(new ImageIcon("resources/arrowRotLt.jpg").getImage(), Width/8, Height/8);
		rotateLeft = new JButton(lft);
		//rotateLeft.addMouseListener(new LeftButt(robots, grid));
		rotateLeft.setPreferredSize(new Dimension(Width/8, Height/8));
		JPanel lftPanel = new JPanel();
		lftPanel.add(rotateLeft);
		basic2DPanel.add(lftPanel, BorderLayout.WEST);
		
		ImageIcon rt = GuiUtils.resizeJP(new ImageIcon("resources/arrowRotRt.jpg").getImage(), Width/8, Height/8);
		rotateRight = new JButton(rt);
		//rotateRight.addMouseListener(new RightButt(robots, grid));
		rotateRight.setPreferredSize(new Dimension(Width/8, Height/8));
		JPanel rtPanel = new JPanel();
		rtPanel.add(rotateRight);
		basic2DPanel.add(rtPanel, BorderLayout.EAST);
	    
		ImageIcon stopImg = GuiUtils.resizeJP(new ImageIcon("resources/stop.jpeg").getImage(), Width/8, Height/8);
		stopBut = new JButton(stopImg);
		//stopBut.addMouseListener(new RightButt(robots, grid));
		stopBut.setPreferredSize(new Dimension(Width/8, Height/8));
		JPanel stopPanel = new JPanel();
		stopPanel.add(stopBut);
		basic2DPanel.add(stopPanel, BorderLayout.CENTER);
		
		add(basic2DPanel);
		
		JPanel powerPanel = new JPanel();
		Dimension powerPD = new Dimension(50, 110);
		powerPanel.setPreferredSize(powerPD);
		powerPanel.setLayout(new FlowLayout());
		
		powerBar = new JScrollBar(JScrollBar.VERTICAL, 1, 1, 1, 6);
		powerBar.setUnitIncrement(1);
		powerBar.setBlockIncrement(1);
		Dimension powerBarD = new Dimension(20, 80);
		powerBar.setPreferredSize(powerBarD);
		
		powerBar.addAdjustmentListener(new AdjustmentListener(){
			public void adjustmentValueChanged(AdjustmentEvent ae) {
				int value = ae.getValue();
			    String st = Integer.toString(value);
			    currentPower.setText(st);
			}
			
		});
		
		powerPanel.add(powerBar);
		
		
		// Listeners
		//if(Gui.useCentralServer){
			forward.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sendMove(FORWARD); 
				}
			});
			atras.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					sendMove(BACK); 
				}
			});
			rotateLeft.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					sendMove(LEFT); 
				}
			});
			rotateRight.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					sendMove(RIGHT); 
				}
			});
			stopBut.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					sendMove(STOP); 
				}
			});
		//}
			
		
//		else{
//			// THis is not in Use. It is meant to be used with the AIBO. 
//			forward.addMouseListener(new ForwardButt(robots, grid));
//			// TODO: Add MouseListeners for the rest.
//		}
		
		
		class PowerSlider extends JPanel{
			String s = "Power";
			int x=5; 
			int y=25;
			int v;
			PowerSlider(){
				super();
				Dimension powerD = new Dimension(18, 80);
				setPreferredSize(powerD);
				//setLayout(new BorderLayout());
			}
			public void paintComponent(Graphics g){
				super.paintComponent(g);
				g.setFont(new Font("SansSerif", Font.BOLD, 15));
				v=g.getFontMetrics(getFont()).getHeight()-4; //+1;
				System.out.println(v);
				int j =0;
				int k= s.length();
				while(j < k+1) {
					if (j == k) 
				       g.drawString(s.substring(j),x, y+(j*v));
				    else
				       g.drawString(s.substring(j,j+1),x, y+(j*v));
				    j++;
				}
			}
		}
		currentPower = new JLabel("" + powerBar.getValue());
		currentPower.setFont(new Font("SansSerif", Font.BOLD, 15));
		currentPower.setHorizontalAlignment(SwingConstants.CENTER);
		
		PowerSlider powerSlider = new PowerSlider();
		powerPanel.add(powerSlider);
		powerPanel.add(currentPower);
		add(powerPanel);
		
		JPanel adv2DPanel = new JPanel();
	    adv2DPanel.setPreferredSize(new Dimension(161, 155));
	    adv2DPanel.setLayout(new BorderLayout());
	    

		JLabel advTitle = new JLabel("ADVANCED CONTROL");
		advTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
		advTitle.setHorizontalAlignment(SwingConstants.CENTER);
	    JPanel advMainPan = new JPanel();
	    advMainPan.setPreferredSize(new Dimension(161, 100));
	    advMainPan.setLayout(new FlowLayout());
	    JLabel linVel = new JLabel("Linear Velocity");
	    linVel.setHorizontalAlignment(SwingConstants.LEFT);
	    advMainPan.add(linVel);
		linVelUs = new JTextField();
		linVelUs.setPreferredSize(new Dimension(65, 20));
		advMainPan.add(linVelUs);
		JLabel angVel = new JLabel("Angular Velocity");
	    angVel.setHorizontalAlignment(SwingConstants.LEFT);
	    advMainPan.add(angVel);
		angVelUs = new JTextField();
		angVelUs.setPreferredSize(new Dimension(65, 20));
		advMainPan.add(angVelUs);
		JLabel durL = new JLabel("Duration");
	    durL.setHorizontalAlignment(SwingConstants.CENTER);
	    durL.setPreferredSize(new Dimension(125, 13));
	    advMainPan.add(durL);
		durUs = new JTextField();
		durUs.setPreferredSize(new Dimension(40, 20));
		advMainPan.add(durUs);
		
		ImageIcon go = new ImageIcon("resources/go.jpg");
		goBut = new JButton(go);
		goBut.setPreferredSize(new Dimension(26, 26));
		goBut.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				sendMove(6);
				// TODO: Start Thread for duration
			}
			
		});
		advMainPan.add(goBut);
		
		adv2DPanel.add(advTitle, BorderLayout.NORTH);
		adv2DPanel.add(advMainPan);
	    
		add(adv2DPanel);
		
		
		JPanel advGotoPanel = new JPanel();
	    advGotoPanel.setPreferredSize(new Dimension(161, 155));
	    advGotoPanel.setLayout(new BorderLayout());

		JLabel advGotoTitle = new JLabel("GOTO");
		advGotoTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
		advGotoTitle.setHorizontalAlignment(SwingConstants.CENTER);
	    JPanel advMainGotoPan = new JPanel();
	    advMainGotoPan.setPreferredSize(new Dimension(161, 100));
	    advMainGotoPan.setLayout(new FlowLayout());
	    JLabel xL = new JLabel("X Coordinate");
	    xL.setHorizontalAlignment(SwingConstants.LEFT);
	    advMainGotoPan.add(xL);
		JTextField xUs = new JTextField();
		xUs.setPreferredSize(new Dimension(65, 21));
		advMainGotoPan.add(xUs);
		JLabel yL = new JLabel("Y Coordinate");
	    yL.setHorizontalAlignment(SwingConstants.LEFT);
	    advMainGotoPan.add(yL);
		JTextField yUs = new JTextField();
		yUs.setPreferredSize(new Dimension(65, 21));
		advMainGotoPan.add(yUs);
		JLabel thetaL = new JLabel("Theta");
	    thetaL.setHorizontalAlignment(SwingConstants.CENTER);
	    thetaL.setPreferredSize(new Dimension(70, 21));
	    advMainGotoPan.add(thetaL);
		JTextField thetaUs = new JTextField();
		thetaUs.setPreferredSize(new Dimension(65, 21));
		advMainGotoPan.add(thetaUs);
		
		ImageIcon gotoGo = new ImageIcon("resources/go.jpg");
		gotoGoBut = new JButton(gotoGo);
		gotoGoBut.setPreferredSize(new Dimension(26, 26));
		advMainGotoPan.add(gotoGoBut);
		
		advGotoPanel.add(advGotoTitle, BorderLayout.NORTH);
		advGotoPanel.add(advMainGotoPan);
	    
		add(advGotoPanel);

		// PTZ PANEL - INCOMPLETE
//		JPanel ptzPanel = new JPanel();
//	    ptzPanel.setPreferredSize(new Dimension(161, 155));
//	    ptzPanel.setLayout(new BorderLayout());
//
//		JLabel ptzTitle = new JLabel("PTZ Control");
//		ptzTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
//		ptzTitle.setHorizontalAlignment(SwingConstants.CENTER);
//	    JPanel advMainPtzPan = new JPanel();
//	    advMainPtzPan.setPreferredSize(new Dimension(161, 100));
//	    advMainPtzPan.setLayout(new FlowLayout());
//		
//		
//		ptzPanel.add(ptzTitle, BorderLayout.NORTH);
//		ptzPanel.add(advMainPtzPan);
//	    
//		add(ptzPanel);

		
		validate();
	}
	public synchronized void sendMove(int dir){		
		if(Robot.getRobotInUse() != -1){
			if(dir == FORWARD){
				if(Gui.useCentralServer){
					Gui.serverComm.writeStream("MOVE " + robots.get(Robot.getRobotInUse()).getUniqueId() + " " + powerBar.getValue()*Robot.powerLevel + " 0 0"  );
					//Gui.serverComm.writeStream("DB " + System.currentTimeMillis() + "User Clicked forward "  );
							
					
					//Gui.db.insertMetroTable(System.currentTimeMillis(), 1, "User clicked forward");
				}
				else{
				robots.get(Robot.getRobotInUse()).p2d.setSpeed(powerBar.getValue()*Robot.powerLevel, 0);
				}
			}
			else if(dir == BACK){
				if(Gui.useCentralServer)
					Gui.serverComm.writeStream("MOVE " + robots.get(Robot.getRobotInUse()).getUniqueId() + " " + -(powerBar.getValue()*Robot.powerLevel) + " 0 0"  );
				else{
					robots.get(Robot.getRobotInUse()).p2d.setSpeed(-powerBar.getValue()*Robot.powerLevel, 0);
				}
			}
			else if(dir == LEFT){
				if(Gui.useCentralServer)
					Gui.serverComm.writeStream("MOVE " + robots.get(Robot.getRobotInUse()).getUniqueId() + " 0 0 " + powerBar.getValue()*Robot.powerLevel*10.0);
				else{
					robots.get(Robot.getRobotInUse()).p2d.setSpeed(0, powerBar.getValue()*Robot.powerLevel*10);
				}
			}
			else if(dir == RIGHT){
				if(Gui.useCentralServer)
					Gui.serverComm.writeStream("MOVE " + robots.get(Robot.getRobotInUse()).getUniqueId() + " 0 0 " + -(powerBar.getValue()*Robot.powerLevel*10.0));
				else{
					robots.get(Robot.getRobotInUse()).p2d.setSpeed(0, -powerBar.getValue()*Robot.powerLevel*10);
				}
			}
			else if(dir == STOP){
				if(Gui.useCentralServer)
					Gui.serverComm.writeStream("MOVE " + robots.get(Robot.getRobotInUse()).getUniqueId() + " 0 0 0");
				else{
					robots.get(Robot.getRobotInUse()).p2d.setSpeed(0, 0);
				}
			}
			else if(dir == ADVANCED){
				if(Gui.useCentralServer)
					Gui.serverComm.writeStream("MOVE " + robots.get(Robot.getRobotInUse()).getUniqueId() + " " +  linVelUs.getText() + " 0 " + angVelUs.getText());
				else{
					robots.get(Robot.getRobotInUse()).p2d.setSpeed(powerBar.getValue()*Robot.powerLevel, 0);
				}
			}
			
			
		}
	}
	
}


