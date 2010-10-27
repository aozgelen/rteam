package metrobotics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Scrollable;

/**
 * @author Pablo Munoz - Metrobotics
 *
 */
public class Grid extends JPanel implements Scrollable, MouseListener, MouseMotionListener {
	//Path2D.Double arrow = createArrow();
	JLabel robotInUse;
	ArrayList<Robot> robots;
	ArrayList<Object> objectsInGrid;
	int numRobots;
	Path2D.Double [] arrows;
	AffineTransform [] at;
	Shape [] shapes;
	Image [] robotImages;
	private Rectangle[] rectangles;
	Graphics2D g2;
	final int ratioPixelsCentimeters = 1;
	boolean drawClick, checkTime;
	Point clicked;
	long time1, time2;
	MainFrame mf;
	
	// From map.conf
	private Dimension sizeMap;
	
	
	private boolean showid;
	private int xShowId;
	private int yShowId;
	private String showIdText;
	private VisionDisplay vision;
	private boolean showVideoInUse;
	private Color videoColor;
	private boolean showLockInUse;
	private Color lockColor;
	
	// Grid(final JLabel robotInUse, final ArrayList<Robot> robots, int width, int height, VisionDisplay vision){
	Grid(final MainFrame mf, int width, int height){
		super();
		
		addMouseListener(this);
		addMouseMotionListener(this);
		// Parsing map.conf
		MapConfParsing parser = new MapConfParsing();
		this.mf = mf;
		objectsInGrid = parser.getMapObjects();
		sizeMap = parser.getSize();
		
		this.robotInUse = mf.RobotInUseLabel;
		this.robots = mf.robots;
		this.numRobots = mf.robots.size();
		
		this.vision = mf.vision;
		videoColor = new Color(0, 120, 200, 50);
		lockColor = new Color(200, 0, 0, 50);
		
		setBackground(Color.white);		
	    Dimension d = new Dimension(width, height);
	    setPreferredSize(d);
	    setBorder(BorderFactory.createRaisedBevelBorder());
	    setFocusable(true);
	    addKeyListener(new KeyListener() {
			boolean moving = false;
			public void keyTyped(KeyEvent e){
				System.out.println("Key Typed: " + e.getKeyChar());
			}
			
			public void keyPressed(KeyEvent e) {	
				System.out.println("Key Pressed: " + (int)e.getKeyChar() + " " + e.getKeyCode());
				handleControl(e);
			}

			public void keyReleased(KeyEvent e) {
				System.out.println("Key Released: " + e.getKeyChar());
				mf.playerJoy.sendMove(mf.playerJoy.STOP);
				moving = false;
			}

			public void handleControl(KeyEvent e){
				System.out.println("In HandleControl");
				int keycode = e.getKeyCode();
				if ( keycode == KeyEvent.VK_W || keycode == KeyEvent.VK_UP){
					if(moving == false){
						System.out.println("UP");
						mf.playerJoy.sendMove(mf.playerJoy.FORWARD);
					}
					moving = true;
				}
				else if ( keycode == KeyEvent.VK_A || keycode == KeyEvent.VK_LEFT ){
					if(moving == false){
						System.out.println("LEFT");
						mf.playerJoy.sendMove(mf.playerJoy.LEFT);
					}
					moving = true;
				}
				else if ( keycode == KeyEvent.VK_D || keycode == KeyEvent.VK_RIGHT ){
					if(moving == false){
						mf.playerJoy.sendMove(mf.playerJoy.RIGHT);
						System.out.println("RIGHT");
					}
					moving = true;
				}
				else if ( keycode == KeyEvent.VK_W || keycode == KeyEvent.VK_DOWN ){
					if(moving == false){
						mf.playerJoy.sendMove(mf.playerJoy.BACK);
						System.out.println("DOWN");
					}
					moving = true;
				}
				else if ( keycode == KeyEvent.VK_ESCAPE){
					// Unlock all robots
					showLockInUse = false;
					drawClick = false;
					Robot.setRobotInUse(-1);
					for(Robot z : robots){
						Gui.serverComm.writeStream("UNLOCK " + z.getUniqueId());
					}
				}
			}
		});

	    //robotImages = new Image[robots.size()];
	    //	    for(int i = 0; i<numRobots; i++){
//	    	rectangles[i] = new Rectangle(10, 10);
//	    }
		
	}
	
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g2 = (Graphics2D)g;
        //g2.translate(this.getWidth()/2, this.getHeight()/2); // New Origin
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           // RenderingHints.VALUE_ANTIALIAS_ON);
        
        
    	
        // PAblo is checking the limits for the DEMO
        // Replaced with drawOutline();
//        g2.setStroke(new BasicStroke((float) 4));
//        g2.setColor(Color.green);
//        g2.drawLine(0, 0, 0, 325);  // 1 because...
//        g2.drawLine(0, 325, 500, 325);
//        g2.drawLine(500, 325, 500, 0);
//        g2.drawLine(500, 0, 0, 0);
//        g2.setStroke(new BasicStroke((float) 0.5));
        
        
        if(showid){
        	g2.drawString(showIdText, xShowId, yShowId);
        }
        
        if(drawClick){
        	if(Robot.getRobotInUse() != -1){
        		g2.fillOval((int)clicked.getX()-10, (int)clicked.getY()-10, 20, 20);
            	g2.drawString(robots.get(Robot.getRobotInUse()).getName() , (int)clicked.getX()-15, (int)clicked.getY()-15);
        	}
//        	else{
//        		g2.drawString("No Robot selected" , (int)clicked.getX()-15, (int)clicked.getY()-15);
//        	}
        }
//        time1 = System.currentTimeMillis();
//		if(time1  > time2 + GUIConstants.STATE_UPDATE_INTERVAL)
//			checkTime = true;
//		if(checkTime){
//			time2 = System.currentTimeMillis(); 
//			drawClick = false;
//			checkTime = false;
//		}
        
        g2.setColor(Color.black);
        drawGrid(g2);
        drawOutline(g2);
        drawObjectsInGrid(g2);
        
    	int i=0;
    	if(Gui.debug){
    		System.out.println("In Grid. Num of robots " + robots.size());
    	}
    	rectangles = new Rectangle[robots.size()]; // This rectangles are used to grab the robot from the map.
    	at = new AffineTransform[robots.size()];
    	for (Robot x : robots){
    		// TODO: 
    		// Rotate from center of the image. 
    		 
    		if(Gui.debug){
    			System.out.println("Repainting robots in Grid: i " + i + " x " +
    					x.getGridX() + " y " + x.getGridY() + " Theta " + x.getGridTheta() + 
    					" Confidence " + x.getConfidence());
    		}

    		at[i] = AffineTransform.getTranslateInstance(x.getGridX()*ratioPixelsCentimeters, (this.getHeight()-x.getGridY()*ratioPixelsCentimeters)); //*50);
    		at[i].rotate(-x.getGridTheta());    //Math.toRadians((-x.getGridTheta())));
    		rectangles[i] = new Rectangle((int)x.getGridX()*ratioPixelsCentimeters-40, (int) (this.getHeight()-x.getGridY())*ratioPixelsCentimeters-40, 80, 80);
    		// g2.draw(rectangles[i]);
            if(showVideoInUse && Robot.getRobotInUseVideo() == i){
            	g2.setColor(videoColor);
            	g2.fill(rectangles[i]);
            }
            if(showLockInUse && Robot.getRobotInUse() == i){
            	g2.setColor(lockColor);
            	g2.fill(rectangles[i]);
            }
            g2.drawImage(x.getRobotGridImage(), at[i], this);
   
			String name = x.getName() + " x: " + x.getGridX() + " y: " + x.getGridY() + " theta: " + x.getGridTheta() + " confidence: " + x.getConfidence();
			if(name!=null){
				//System.out.println("Printing coordinates");
				g2.drawString(name, (int)x.getGridX()*ratioPixelsCentimeters + 20, (int)(this.getHeight()-(x.getGridY()*ratioPixelsCentimeters)) + 30);  //*50;
			}
			i++;
    	}
		
    	
	}
	private void drawOutline(Graphics2D g22) {
		g2.setStroke(new BasicStroke((float) 4));
		g2.setColor(Color.green);
		g2.drawLine(0, this.getHeight()-0, 0, this.getHeight()-565);  // 1 because...
		g2.drawLine(0, this.getHeight()-565, 610, this.getHeight()-565);
		g2.drawLine(610, this.getHeight()-565, 610, this.getHeight()-0);
		g2.drawLine(610, this.getHeight()-0, 0, this.getHeight()-0);
		g2.setStroke(new BasicStroke((float) 0.5));
	}

	private void drawObjectsInGrid(Graphics2D g2) {
		for(Object o : objectsInGrid){
			if(o instanceof Marker){
				g2.setColor(((Marker) o).getPrimaryColor());
				g2.drawOval((int)((Marker) o).getxPos(), this.getHeight()-(int)((Marker) o).getyPos(), 10, 10);
				g2.setColor(((Marker) o).getSecondaryColor());
				g2.fillOval((int)((Marker) o).getxPos()+1, this.getHeight()-(int)((Marker) o).getyPos()+1, 8, 8);
				//System.out.println("Drawing Marker");
				//String coord = (int)((Marker) o).getxPos() + " " + (int)((Marker) o).getyPos();
				//g2.drawString(coord, (int)((Marker) o).getxPos(), this.getHeight()-(int)((Marker) o).getyPos());
			}
			if(o instanceof Wall){
				g2.setColor(((Wall)o).getColor());
				g2.fillRect(
						(int)((Wall)o).getRect().getX(),
						this.getHeight()-(int)((Wall)o).getRect().getY(),
						(int)((Wall)o).getRect().getWidth(),
						(int)((Wall)o).getRect().getHeight());
				//System.out.println("Drawing Wall");
				//String coord = ((Wall)o).getName() + " " + (int)((Wall) o).getRect().getX() + " " + (int)((Wall) o).getRect().getY();
				//g2.drawString(coord, (int)((Wall) o).getRect().getX(), this.getHeight()-(int)((Wall) o).getRect().getY());
			}
		}
		// TODO Auto-generated method stub
		
	}

	private void drawGrid(Graphics2D g2) {
		// TODO Auto-generated method stub
		Stroke s = g2.getStroke();
		g2.setStroke(new BasicStroke((float) 0.5));
		for(int i=0; i<this.getWidth(); i++){
			g2.setColor(Color.blue);
			if(i%40==0){
				g2.drawLine(i, 0, i, this.getHeight());
			}
		}
		for(int i=0; i<this.getHeight(); i++){
			g2.setColor(Color.blue);
			if(i%40==0){
				g2.drawLine(0, i, this.getWidth(), i);
			}
		}
		// Back to old stroke
		g2.setStroke(s);
	}

	private Path2D.Double createArrow() {
        int length = 25;
        int barb = 15;
        double angle = Math.toRadians(20);
        Path2D.Double path = new Path2D.Double();
        path.moveTo(-length/2, 0);
        path.lineTo(length/2, 0);
        double x = length/2 - barb*Math.cos(angle);
        double y = barb*Math.sin(angle);
        path.lineTo(x, y);
        x = length/2 - barb*Math.cos(-angle);
        y = barb*Math.sin(-angle);
        path.moveTo(length/2, 0);
        path.lineTo(x, y);
        return path;
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

	public void gotoPosition(int x, int y) {
		// GOTO
		clicked = new Point(x, y); 
		y = this.getHeight()- y;
		Gui.serverComm.writeStream("GOTO " + robots.get(Robot.getRobotInUse()).getUniqueId() + x + " " + y);
        drawClick = true;
        repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// Check if the user clicked on a robot or on a Goto position		
		// robot:

		if(e.getClickCount() == 1){
			// Show video.
			int i =0;
			for(Robot x : robots){
				if(i < rectangles.length && rectangles[i] != null){
					if(rectangles[i].contains(e.getX(), e.getY())){
						robotInUse.setText("Video: " + x.getName());
						vision.showVideo(x);
						System.out.println("Showing video");
						Robot.setRobotInUseVideo(i);
						Robot.setRobotInUse(-1);
						System.out.println("1 click " + Robot.getRobotInUse() + " " + Robot.getRobotInUseVideo());
						showLockInUse = false;
						showVideoInUse = true;
						drawClick = false;
						requestFocusInWindow();
						// Unlock all robots
						for(Robot z : robots){
							Gui.serverComm.writeStream("UNLOCK " + z.getUniqueId());
						}
						return;
					}
				}
				i++;
			}
//			if (Robot.getRobotInUse() != -1){
//				gotoPosition(e.getX(), e.getY());
//				return;
//			}	
//			Robot.setRobotInUse(-1);
			
		}
		if(e.getClickCount() == 2){
			int i =0;
			for(Robot x : robots){
				if(i < rectangles.length && rectangles[i] != null){
					if(rectangles[i].contains(e.getX(), e.getY())){
						robotInUse.setText(x.getName());
						Gui.serverComm.writeStream("LOCK " + x.getUniqueId());
						Robot.setRobotInUse(i);
						Robot.setRobotInUseVideo(i);
						vision.showVideo(x);
						showLockInUse = true;
						showVideoInUse = false;
						drawClick = false;
						requestFocusInWindow();
						return;
					}
				}
				i++;
			}
			if (Robot.getRobotInUse() != -1){
				drawClick = true;				
				gotoPosition(e.getX(), e.getY());
			}		
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int i =0;
		for(Robot x : robots){
			if(i < rectangles.length && rectangles[i] != null){
				if(rectangles[i].contains(e.getX(), e.getY())){
					// Print this on the screen
					// System.out.println("Robot: " + x.getName());
					showid = true;
					xShowId = e.getX();
					yShowId = e.getY() - 20;
					showIdText = x.getName();
					return;
				}
			}
			i++;
		}
		showid = false;
		requestFocusInWindow();
		
		
	}

	
}