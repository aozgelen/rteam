package metrobotics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javaclient3.PlayerClient;
import javaclient3.structures.PlayerConstants;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * @author Pablo Munoz
 * This class displays the images received from the Robots. 
 * It is also used to display the result from the Image Recognition using 
 * Neural Networks.
 */
public class VisionDisplay extends JPanel {
	ArrayList<Robot> robots;
	int i = 0;
	BufferedImage img;
	Robot bot;
	String message = "";
	
	
	private static boolean threadVision;
	private static boolean threadVisionAibo;
	int lastVideo = -1;
	static CameraThread cameraThread;
	//static CameraAiboTekThread cameraAiboThread;
	int aiboPortInUse;



	
	public VisionDisplay(ArrayList<Robot> robots, int width, int height) {
		super();
		setBackground(Color.gray);
		//int Width = 450;
		//int Height = 350;
	    Dimension d = new Dimension(width, height);
	    setPreferredSize(d);	
	    setBorder(BorderFactory.createRaisedBevelBorder());

		this.robots = robots;
		
		// This needs to be in a Thread
		VisionDisplayThread thread = new VisionDisplayThread(this);
		thread.start();
		//CameraThread cameraThread = new CameraThread(this);
		//cameraThread.start();
		//System.out.println("Vision started");
	}
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.yellow);
    	i++;
		if(img == null){
			g2.setFont(new Font("SansSerif", Font.BOLD, 36));
			g2.drawString("NO IMAGE" + i, 50, 100);
		}
		else{
			g2.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
			if(message != ""){
				g2.drawString(message, this.getWidth()/2 - 50, this.getHeight()/2);
			}
		}
	}
	public void showVideo(Robot x) {
		// Start Thread for Vision for this Robot. Kill all the other Threads for Camera.
    	// TODO: It is missing the implementation of switching between the Segmented and the Raw Camera.
    	if(x.getHasCamera()){
    		System.out.println("In Robot Selects getHasCamera");
//    		Robot.setAiboCameraThread(false); // End all AiboCameraThreads
//    		if(threadVisionAibo){
//    			while(cameraAiboThread.isAlive()){
//        			//System.out.println("Old Thread is still alive");
//        		}
//    		}
    		Robot.setCameraThread(false); // End all other CameraThreads;
    		if(threadVision){
    			while(cameraThread.isAlive()){
    				System.out.println("Waiting for old thread to end");
    				System.out.println(Robot.getCameraThread());
        			//System.out.println("Old Thread is still alive");
        		}
    		}
    		// Create cameraProxy
    		System.out.println(x.getPlayerIP() + " " + x.getPlayerPort());
    		if(lastVideo!=-1)
    			robots.get(lastVideo).playerclient.close();
    		System.out.println("THIS IS THE PLAYER IP " + x.getPlayerIP());
    		x.playerclient = new PlayerClient(x.getPlayerIP(), x.getPlayerPort());
    		x.cam = x.playerclient.requestInterfaceCamera(0, PlayerConstants.PLAYER_OPEN_MODE); // We are not using indexes.
    		Robot.setCameraThread(true);
    		cameraThread = new CameraThread(x);
    		lastVideo = x.getRobotKey();
    		cameraThread.start();
    		System.out.println("Vision Thread started");
    		threadVision = true;
    	}
//    	else if(x.getHasCameraAibo()){
//    		if(Gui.debug){
//    			System.out.println("In Switch");
//    		}
//    		// If we have only one port, we shouldn't enter this statement. 
//    		Robot.setAiboCameraThread(false); // End Previous thread; // This inside while below????
//    		// This should be done in a different way. For instance checking that the thread has ended.
//    		if(threadVisionAibo){
//    			while(cameraAiboThread.isAlive()){
//        			//System.out.println("Old Thread is still alive");
//        		}
//    		}
//    		Robot.setCameraThread(false); // End all other CameraThreads;
//    		if(threadVision){
//        		while(cameraThread.isAlive()){
//        			//System.out.println("Old Thread is still alive");
//        		}
//    		}
//    		aiboPortInUse = x.getRawCameraPort();
//    		// This was for switching from Seg to Raw, or Raw to Seg. 
//    		//aiboPortInUse = (aiboPortInUse == x.getRawCameraPort())? x.getSegCameraPort() : x.getRawCameraPort();
//    		//if(aiboPortInUse == 0)aiboPortInUse = x.getRawCameraPort();
//            Robot.setAiboCameraThread(true); // So the new Thread can run
//            cameraAiboThread = new CameraAiboTekThread(x, aiboPortInUse);
//    		System.out.println("Aibo Vision Thread started " + aiboPortInUse);
//    		cameraAiboThread.start();
//    		threadVisionAibo = true;
//    	}		
	}
//	public void setImage(BufferedImage img) {
//		this.img = img;
//		repaint();
//	}
}

class VisionDisplayThread extends Thread{
	Robot bot;
	VisionDisplay vi;
	VisionDisplayThread(VisionDisplay vi){
		this.vi = vi;
	}
	
	public void run(){
		while(true){
			if(Robot.getRobotInUseVideo()!=-1){
				bot = vi.robots.get(Robot.getRobotInUseVideo());
				vi.img = bot.cameraImage;
				vi.repaint();
			}
		}
	}
	
}