package metrobotics;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * @author Pablo Munoz - Metrobotics
 * I am currently not using this class. I was using it when directly connecting to Player
 * or directly to the robot.
 * TODO: Fix in PlayerJoy so the mouseListener points to this class.
 * Pablo
 */
class MoveThread extends Thread{
	ArrayList<Robot> robots;
	// int robotKeyInt;
	Robot inUse;
	Grid grid;
	double dist, rotationVar;
	double tempTheta;
	double y, x;
	

	MoveThread(ArrayList<Robot> robots, Grid grid){//, int robotKey){
		// this.robotKeyInt = robotKey;
		this.robots = robots;
		this.grid = grid;
	}
	public void run(){
		if(Robot.getRobotInUse()==-1){
			System.out.println("Returning because no robot was selected");
			return;
		}
		inUse = robots.get(Robot.getRobotInUse());
		while(ForwardButt.movingForward || BackButt.movingBack || LeftButt.rotateLeft || RightButt.rotateRight){
			dist = 5.0; // We should be able to change this on runtime. 
			rotationVar = 5.0; // We should be able to change this on runtime. 
			tempTheta = inUse.getGridTheta();
			
			

			//System.out.println("Test: " + Robot.getRobotInUse());//getRobotInUse().intValue());
			if(ForwardButt.movingForward){
				if(tempTheta == 0){
					inUse.setGridX(inUse.getGridX() + dist);
				}
				else if(tempTheta > 0.0 && tempTheta < 90.0){
					y = Math.sin(Math.toRadians(tempTheta)) * dist;
					inUse.setGridY(inUse.getGridY()+y);
					x = Math.sqrt(Math.pow(dist, 2) - Math.pow(y, 2));
					inUse.setGridX(inUse.getGridX() + x);
				}
				else if(tempTheta == 90){
					inUse.setGridY(inUse.getGridY() + dist);
				}
				
				else if(tempTheta>90.0 && tempTheta <180.0){
					y = Math.sin(Math.toRadians(tempTheta)) * dist;
					inUse.setGridY(inUse.getGridY()+y);
					x = Math.sqrt(Math.pow(dist, 2) - Math.pow(y, 2));
					inUse.setGridX(inUse.getGridX() - x);
				}
				// Use negative instead
				else if(tempTheta == 180 || tempTheta == -180){
					inUse.setGridX(inUse.getGridX()-dist);
				}
				
				else if(tempTheta > -180.0 && tempTheta < -90.0){
					System.out.println("In RecordButt theta = " + tempTheta);
					y = -Math.sin(Math.toRadians(tempTheta)) * dist;
					inUse.setGridY(inUse.getGridY()-y);
					x = Math.sqrt(Math.pow(dist, 2) - Math.pow(y, 2));
					inUse.setGridX(inUse.getGridX()- x); 
				}
					
				else if(tempTheta == -90){
					inUse.setGridY(inUse.getGridY() - dist);
				}
				else if(tempTheta > -90.0 && tempTheta < 0){
					y = Math.sin(Math.toRadians(tempTheta)) * dist; 
					inUse.setGridY(inUse.getGridY() - y);
					x = Math.sqrt(Math.pow(dist, 2) - Math.pow(y, 2));
					inUse.setGridX(inUse.getGridX() + x);
				}
				
				//inUse.setGridX(inUse.getGridX()+5.0); // JP: Use a constant instead of 5.0
				if(inUse.getUsesPlayer()){
					inUse.p2d.setSpeed(0.5, 0); // TODO: We should be able to change the speed. 
					System.out.println("Send Command to Player to " + inUse.getName());
				}	
				else if(inUse.getHasMoveAibo()){
					//inUse.aiboDirect.sendCommandLegs("f", 0.5);
				}
				grid.repaint();
				System.out.println("Moving Forward: " + inUse.getName() + 
						" " + inUse.getGridX() + " " + inUse.getGridY() + 
						" " + inUse.getGridTheta());
					
				
			}
			if(BackButt.movingBack){
				System.out.println("Moving Back NOT IMPLEMENTED YET");
			}
			if(LeftButt.rotateLeft){
				System.out.println("Left");
				inUse.setGridTheta(inUse.getGridTheta() + rotationVar);
				if(inUse.getGridTheta()>180){
					inUse.setGridTheta(0-inUse.getGridTheta());
				}
				
				else if(inUse.getUsesPlayer()){
					//TODO: FIX THIS
					inUse.p2d.setSpeed(0, 0); // TODO: We should be able to change the speed. 
					System.out.println("Send Command to Player to " + inUse.getName());
				}	
				else if(inUse.getHasMoveAibo()){
					//inUse.aiboDirect.sendCommandLegs("r", 0.2);
				}
				grid.repaint();
				}
			}
			if(RightButt.rotateRight){
				System.out.println("Right");
				inUse.setGridTheta(inUse.getGridTheta() - rotationVar);
				if(inUse.getGridTheta()<-180){
					inUse.setGridTheta(180- (-(inUse.getGridTheta())+180));
				}
				if(inUse.getUsesPlayer()){
					//TODO: FIX THIS
					inUse.p2d.setSpeed(0, 0); // TODO: We should be able to change the speed. 
					System.out.println("Send Command to Player to " + inUse.getName());
				}	
				else if(inUse.getHasMoveAibo()){
					//inUse.aiboDirect.sendCommandLegs("r", -0.2);
				}
				grid.repaint();
			}
			try {
				Thread.sleep(50); // This sleep time should come also from the configuration file. 
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
			
	
		return;
	}
}
class RightButt implements MouseListener {
	static boolean rotateRight = false;
	MoveThread p;
	ArrayList<Robot> robots;
	Robot inUse;
	Grid grid;
	
	public RightButt(ArrayList<Robot> robots, Grid grid) {
		this.robots = robots;
		this.grid = grid;
		System.out.println("In RightButt");
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("Rotate Right");
		rotateRight = true;
		p = new MoveThread(robots, grid);
		p.start();
		//ControlPanel.MoveForward();		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		System.out.println("Stop Rotating");
		rotateRight = false;
		while(p.isAlive()){
			System.out.println("Waiting for thread to end");
		}
		if(Robot.getRobotInUse()!=-1){
			inUse = robots.get(Robot.getRobotInUse());
			if(Gui.useCentralServer){
				Gui.serverComm.writeStream("MOVE " + " " + inUse.getUniqueId() + " 0 0 0");
			}
			else if(inUse.getUsesPlayer()){
				inUse.p2d.setSpeed(0.0, 0.0);
				System.out.println("Stop Rotating Right: " + inUse.getName());
			}
		}
	}
	
}

class LeftButt implements MouseListener {
	static boolean rotateLeft = false;
	MoveThread p;
	ArrayList<Robot> robots;
	Robot inUse;
	Grid grid;
	
	public LeftButt(ArrayList<Robot>robots, Grid grid) {
		this.robots = robots;
		this.grid = grid;
		System.out.println("In LeftButt");
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("Rotate Left");
		rotateLeft = true;
		p = new MoveThread(robots, grid);
		p.start();
		//ControlPanel.MoveForward();
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		System.out.println("Stop Rotating");
		rotateLeft = false;
		while(p.isAlive()){
			System.out.println("Waiting for thread to end");
		}
		if(Robot.getRobotInUse()!=-1){
			inUse = robots.get(Robot.getRobotInUse());
			if(Gui.useCentralServer){
				Gui.serverComm.writeStream("MOVE " + " " + inUse.getUniqueId() + " 0 0 0");
			}
			else if(inUse.getUsesPlayer()){
				inUse.p2d.setSpeed(0.0, 0.0);
				System.out.println("Stop Rotating Left: " + inUse.getName());
			}
		}
	}
}

class ForwardButt implements MouseListener {
	static boolean movingForward = false;
	MoveThread p;
	ArrayList<Robot> robots;
	Robot inUse;
	Grid grid;
	
	public ForwardButt(ArrayList<Robot> robots, Grid grid) {
		this.robots = robots;
		this.grid = grid;
		System.out.println("In ForwardButton");
	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("Moving Forward");
		movingForward = true;
		p = new MoveThread(robots, grid);
		p.start();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		System.out.println("Stop Moving Forward");
		movingForward = false;
		while(p.isAlive()){
			System.out.println("Waiting for thread to end");
		}
		if(Robot.getRobotInUse()!=-1){
			inUse = robots.get(Robot.getRobotInUse());
			if(Gui.useCentralServer){
				Gui.serverComm.writeStream("MOVE " + " " + inUse.getUniqueId() + " 0 0 0");
			}
			else if(inUse.getUsesPlayer()){
				inUse.p2d.setSpeed(0.0, 0.0);
				System.out.println("Stop Moving Forward: " + inUse.getName());
			}
		}
	}
}

class BackButt implements MouseListener {
	static boolean movingBack = false;
	MoveThread p;
	ArrayList<Robot> robots;
	Robot inUse;
	Grid grid;
	
	public BackButt(ArrayList<Robot> robots, Grid grid) {
		this.robots = robots;
		this.grid = grid;
		System.out.println("In BackButt");
	}
	
	public void mousePressed(MouseEvent e) {
		System.out.println("Moving Back");
		movingBack = true;
		p = new MoveThread(robots, grid);
		p.start();
	}
	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		System.out.println("Stop Moving Forward");
		movingBack = false;
		while(p.isAlive()){
			System.out.println("Waiting for thread to end");
		}
		if(Robot.getRobotInUse()!=-1){
			inUse = robots.get(Robot.getRobotInUse());
			if(Gui.useCentralServer){
				Gui.serverComm.writeStream("MOVE " + " " + inUse.getUniqueId() + " 0 0 0");
			}
			else if(inUse.getUsesPlayer()){
				inUse.p2d.setSpeed(0.0, 0.0);
				System.out.println("Stop Moving Back: " + inUse.getName());
			}
		}		
	}
}