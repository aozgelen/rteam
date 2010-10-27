package metrobotics;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javaclient3.*;
import javaclient3.structures.PlayerConstants;

/**
 * @author Pablo Munoz - Metrobotics
 * This class contains the structure for the Robot object. It is worth noticing that 
 * it has fields that are used when connecting the Central Server, and other fields 
 * that are needed when connecting to Player or directly to each Robot.
 */
public class Robot {
	
	// Central Server
	private boolean lock; // This is going to be used in a future version
	private long uniqueId;
	private int ownerId; // I am not using this but I could be used later.
	
	public static double powerLevel = 0.12;
	
	private volatile static int robotInUse = -1;
	private volatile static int robotInUseVideo = -1;
	private boolean usesPlayer, hasCamera=false; //hasBlobFinder, hasLocalize, hasGripper, hasSonar;
	// Playerclient
	PlayerClient playerclient;
	private String playerIp;
	private int playerPort;	
	
	int portAssigned;
	// Position2D
	Position2DInterface p2d;

	// CameraInterface
	CameraInterface cam;
	public static volatile boolean visionThreadStarted;
	public volatile BufferedImage cameraImage;

	// PTZInterface
	private PtzInterface ptz;
	private int ptzIndex;
	
	// Robot Properties
	private ImageIcon robotIcon;
	private Image robotGridImage;
	private String name;
	private int robotKey; // Index in the ArrayList
	
	// Grid Representation
	private double gridX;
	private double gridY;
	private double gridTheta;
	private double confidence;
	
	// Is Aibo
	private String aiboHost;
	private int rawCameraPort;
	private int segCameraPort;
	private boolean hasCameraAibo;
	private boolean hasMoveAibo;
	private int walkPort;
	private int headPort;
	//AiboDirectMove aiboDirect;
	private static volatile boolean endAiboCameraThread;
	
	// This nested class is used to build the Robot. It uses a builder to reduce the lines of code when creating a Robot
	public static class Builder{
		// Required parameter
		boolean usesPlayer;
		boolean hasCamera;
		private static int robotCounter = 0;
		
		// Optional parameters
		String playerServer;
		int portAssigned;
		PlayerClient playerclient;
		Position2DInterface p2d;
		int pos2DIndex;
		CameraInterface cam;
		int camIndex;
		String name;
		private int robotKey;
		ImageIcon robotIcon;
		Image robotGridImage;
		int ptzIndex;
		private PtzInterface ptz;
		String aiboHost;
		int rawCameraPort;
		int segCameraPort;
		int walkPort;
		int headPort;
		//AiboDirectMove aiboDirect;
		private boolean hasCameraAibo;
		private boolean hasMoveAibo;
		
		public Builder(boolean usesPlayer){
			this.usesPlayer = usesPlayer;
			robotKey = robotCounter;
			System.out.println("New Robot");
			robotCounter++; 
		}
		
		// Player client
		public Builder playerclient(String playerServer, int portAssigned){
			this.playerServer = playerServer;
			this.portAssigned = portAssigned;
			if(this.usesPlayer){
				this.playerclient = new PlayerClient(playerServer, portAssigned);
			}
			return this;
		}
		
		public Builder pos2D(int pos2DIndex){
			if(this.playerclient == null){
				// TODO: Pablo: Throw an exception. FIX THIS
				System.out.println("Playerclient has not been initialized");
				return this;
			}
			this.pos2DIndex = pos2DIndex;
			this.p2d = playerclient.requestInterfacePosition2D(this.pos2DIndex, PlayerConstants.PLAYER_OPEN_MODE);
			System.out.println("Position2DInitialized");
			return this;
		}
		
		public Builder pos2DandPTZAiboTek(String aiboHost, int walkPort, int headPort){
			if(this.usesPlayer){
				return this;
			}
			this.aiboHost = aiboHost;
			this.walkPort = walkPort;
			this.headPort = headPort;
			//this.aiboDirect = new AiboDirectMove(aiboHost, walkPort, headPort);
			this.hasMoveAibo = true;
			return this;
		}
		
		public Builder camera(int camIndex){
			if(this.playerclient == null){
				// TODO: Throw an exception. Pablo. FIX THIS
				System.out.println("Playerclient has not been initialized");
				return this;
			}
			this.camIndex = camIndex;
			this.hasCamera = true;
			this.hasCameraAibo = false;
			this.cam = playerclient.requestInterfaceCamera(camIndex, PlayerConstants.PLAYER_OPEN_MODE);
			System.out.println("Player Camera Instantiated");
			return this;
		}
		public Builder cameraAiboTek(String aiboHost, int rawCameraPort, int segCameraPort){
			if(hasCamera){
				return this;
			}
			this.aiboHost = aiboHost;
			this.rawCameraPort = rawCameraPort;
			this.segCameraPort = segCameraPort;
			this.hasCameraAibo = true;
			return this;
		}
		
		public Builder ptz(int ptzIndex){
			if(this.playerclient == null){
				// TODO: Throw an exception. Pablo. FIX THIS
				System.out.println("Playerclient has not been initialized");
				this.ptzIndex = ptzIndex;
				this.ptz = playerclient.requestInterfacePtz(ptzIndex, PlayerConstants.PLAYER_OPEN_MODE);
				return this;
			}
			// INCOMPLETE. Pablo
			return this;
		}
		
		public Builder name(String name){
			this.name = name;
			return this;
		}
		
		public Builder icon(String icon){
			if(icon == GUIConstants.SID_AIBO){
				this.robotIcon = new ImageIcon ("resources/aiboIcon.jpg");
				try {
					this.robotGridImage = ImageIO.read(new File("resources/aiboGrid.jpg"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			else if(icon == GUIConstants.SID_SURVEYOR){
				this.robotIcon = new ImageIcon ("resources/surveyorIcon.jpg");
				try {
					this.robotGridImage = ImageIO.read(new File("resources/surveyorGrid.jpg"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			return this;
		}
		public static void setRobotCounter(int i){
			robotCounter = i;
		}
		
		public Robot build(){
			return new Robot(this);
		}	
	
	}
	
	// Old Constructor Delete when cleaning
	//public Robot(boolean hasPos2D, boolean ptz, boolean hasCamera, boolean b, boolean c, boolean d, boolean e, 
		//	String robotIP, int bluetoothPort, String playerServer, int portAssigned, int index) throws InterruptedException{
		// This is now working for testing on the aibo. It needs more work to 
		//this.robotIP = robotIP;
	//}
	
	private Robot(Builder builder){
		// Required parameter
		this.usesPlayer = builder.usesPlayer;
		this.robotKey = builder.robotKey;
		
		// Optional parameters
		//this.playerServer = builder.playerServer;
		this.portAssigned = builder.portAssigned;
		this.playerclient = builder.playerclient;
		this.p2d = builder.p2d;
		//this.pos2DIndex = builder.pos2DIndex;
		this.cam = builder.cam;
		//this.camIndex = builder.camIndex;
		this.name = builder.name;
		this.robotIcon = builder.robotIcon;
		this.hasCamera = builder.hasCamera;
		this.robotGridImage = builder.robotGridImage;
		this.aiboHost = builder.aiboHost;
		this.rawCameraPort = builder.rawCameraPort;
		this.segCameraPort = builder.segCameraPort;
		this.hasCameraAibo = builder.hasCameraAibo;	
		//this.aiboDirect = builder.aiboDirect;
		this.hasMoveAibo = builder.hasMoveAibo;
	}
	
	public boolean getUsesPlayer(){
		return usesPlayer;
	}
	public boolean getHasCamera(){
		return hasCamera;
	}
	public static boolean getCameraThread(){
		return visionThreadStarted;
	}
	public static void setCameraThread(boolean end){
		visionThreadStarted = end;
	}
	public boolean getHasCameraAibo(){
		return hasCameraAibo;
	}
	public boolean getHasMoveAibo(){
		return hasMoveAibo;
	}
	public String getAiboHost(){
		return aiboHost;
	}
	public int getRawCameraPort(){
		return rawCameraPort;
	}
	
	public int getSegCameraPort(){
		return segCameraPort;
	}
	
	public static boolean getAiboCameraThread(){
		return endAiboCameraThread;
	}
	public static void setAiboCameraThread(boolean end){
		endAiboCameraThread = end;
	}
	public String getName(){
		return name;
	}
	public ImageIcon getRobotIcon(){
		return robotIcon;
	}
	public void setRobotIcon(String icon){
		
		// TODO: Delete this. I am using it only as a place holder until we get images
		// from the Central Server.
		// Pablo
		cameraImage = CameraThread.toBufferedImage(new ImageIcon("resources/visionPlaceHolder.jpg").getImage());
		
		if(icon.compareTo(GUIConstants.SID_AIBO) == 0){
			System.out.println("Aibo Icon");
			this.robotIcon = new ImageIcon ("resources/aiboIcon.jpg");
			try {
				this.robotGridImage = ImageIO.read(new File("resources/aiboGrid.jpg"));
				//this.robotGridImage = JPImageLib.resizeJP(this.robotGridImage, 10, 10);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		else if(icon.compareTo(GUIConstants.SID_SURVEYOR) == 0){
			this.robotIcon = new ImageIcon ("resources/surveyorIcon.jpg");
			System.out.println("Surveyor Icon");

			try {
				this.robotGridImage = ImageIO.read(new File("resources/surveyorGrid.jpg"));
				this.robotGridImage = this.robotGridImage.getScaledInstance(GUIConstants.ROBOT_SURVEYOR_WIDTH, -1, Image.SCALE_FAST);
				//this.robotGridImage = GuiUtils.resizeJP(this.robotGridImage, 10, 10);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		else if(icon.compareTo(GUIConstants.SID_NXT) == 0){
			this.robotIcon = new ImageIcon ("resources/nxtIcon.jpg");
			System.out.println("NXT Icon");

			try {
				this.robotGridImage = ImageIO.read(new File("resources/nxtGrid.jpg"));
				//this.robotGridImage = JPImageLib.resizeJP(this.robotGridImage, 10, 10);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(icon.compareTo(GUIConstants.SID_SCRIBBLER) == 0){
			this.robotIcon = new ImageIcon ("resources/scribblerIcon.jpg");
			System.out.println("Scribbler Icon");

			try {
				this.robotGridImage = ImageIO.read(new File("resources/scribblerGrid.jpg"));
				//this.robotGridImage = JPImageLib.resizeJP(this.robotGridImage, 10, 10);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		else{
			this.robotIcon = new ImageIcon ("resources/terminator.jpg");
			try {
				this.robotGridImage = ImageIO.read(new File("resources/terminatorGrid.jpg"));
				//this.robotGridImage = JPImageLib.resizeJP(this.robotGridImage, 10, 10);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public Image getRobotGridImage(){
		return robotGridImage;
	}
	public double getGridX(){
		return gridX;
	}
	public void setGridX(double gridX){
		this.gridX = gridX;
	}
	public double getGridY(){
		return gridY;
	}
	public void setGridY(double gridY){
		this.gridY = gridY;
	}
	public double getGridTheta(){
		return gridTheta;
	}
	public void setGridTheta(double gridTheta){
		this.gridTheta  = gridTheta;
	}
	
	public static int getRobotInUse(){
		return robotInUse;
	}
	public static int getRobotInUseVideo(){
		return robotInUseVideo;
	}
	public static void setRobotInUse(int x){
		robotInUse = x;
	}
	public static void setRobotInUseVideo(int robotKey2) {
		robotInUseVideo = robotKey2;
	}

	
	public int getRobotKey(){
		return this.robotKey;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public long getUniqueId(){
		return this.uniqueId;
	}
	
	public void setUniqueId(long uniqueId2) {
		this.uniqueId = uniqueId2;
		
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
		
	}

	public void setGridConfidence(double conf) {
		this.confidence = conf;
	}
	
	public void setRobotKey(int key){
		this.robotKey = key;
	}

	public double getConfidence() {
		return this.confidence;
	}
	
	public void setPlayerClientAndCamera(String playerIP, int port) {
		// Now I am doing this on the vision Thread.
		//this.playerclient = new PlayerClient(playerIP, port);
		this.playerIp = playerIP;
		this.playerPort = port;
		this.usesPlayer = true;
		this.hasCamera = true;
		this.hasCameraAibo = false;
		
		// Now I am doing this on the vision Thread.
		// this.cam = playerclient.requestInterfaceCamera(0, PlayerConstants.PLAYER_OPEN_MODE); // We are not using indexes.
		// System.out.println("Player Camera Instantiated");
	}

	public String getPlayerIP() {
		return this.playerIp;
	}

	public int getPlayerPort() {
		return this.playerPort;
	}

	
	
	
}
