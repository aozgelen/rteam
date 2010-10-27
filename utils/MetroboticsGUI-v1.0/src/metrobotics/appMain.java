package metrobotics;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Pablo Munoz - Metrobotics
 * This class is the point of entrance of the GUI Application. 
 * Currently, only working with the server is enabled.
 * I am working on reading a configuration file from Player, so there is no need to hard
 * code the use of Player or directly controlling the robots.
 * TODO: Comment all the methods. Pablo.
 */
public class appMain {
	// In case we want to directly connect to Player 
	static String playerServer;
	static int portAssigned;
	
	public static void main(String[] args) throws InterruptedException {
		ArrayList<Robot> robots = new ArrayList<Robot>();
		//Gui.db = null;
		Gui.debug = false;
		if(Gui.debug){
			for(int i = 0; i<args.length; i++){
				System.out.println(i + ": " + args[i]);
			}
		}
		if(args.length<1){
			System.out.println("Usage: appMain [option] \nOptions: \n" + 
					"-s Using Central Server [host] [port]\n-p Using Player [host] [config file]\n" + "" +
							"-c Using Hybrid Config File (Player + Direct control of Robot) [file] NOT IMPLEMENTED YET"); 
			return;
		}
		
		// Using Central Server
		else if(args[0].compareTo("-s") == 0){
			Gui.useCentralServer = true;
			Gui.centralServerIP = args[1]; 
			Gui.centralServerInitMsgPort = Integer.parseInt(args[2]);

			// We are not going to use a database connected to the GUI for now. All database queries are done by the server.
			// Gui.useMysql = true;
			
			// For now, I am deciding here the layout. Conf file!!
			Gui.layoutSelection = GUIConstants.LAYOUT_BIGMAP; // ORIGINAL; // LAYOUT_BIGMAP;
			
			
		}
		else if(args[0].compareTo("-p") == 0){
			System.out.println("Connecting directly through Player");
			playerServer = args[1];
			portAssigned = Integer.parseInt(args[2]);
			// Read configuration file (.cfg)
			robots = readConfigFile(args[3]);
		}
		// Connecting to the database. 
//		if(Gui.useMysql = true){
//			Gui.db = new MetroDatabase();
//			// Check if database is working
//			try {
//				if(Gui.db.getConnection().isValid(500)){
//					System.out.println("Database connected...");
//				}
//				else{
//					Gui.useMysql = false;
//				}
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		// Creating the mainFrame;
		MainFrame mf = new MainFrame("Metrobotics", robots);
		if(Gui.useCentralServer){
			Gui.serverComm = new ServerComm(mf, robots);
			try {
				Thread.sleep(1000); 
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
			GuiState guiState = new GuiState();
			guiState.start();
		}
	}
	
	/**
	 * THIS METHOD NEEDS TO BE COMPLETED. 
	 * Returns an ArrayList<Robot> with all the robots that are in the configuration file.
	 * Currently, the robots are hard coded in this method if one wants to directly use them
	 * with Player. 
	 * It takes a String with the name of the file as a parameter.
	 * <p>
	 * The format of the configuration file has not been determined, yet. 
	 * @author Pablo Munoz
	 * @param  fileName  a String with the name of the configuration file
	 * @param  name the location of the image, relative to the url argument
	 * @return the image at the specified URL
	 */
	private static ArrayList<Robot> readConfigFile(String fileName) {
		ArrayList<Robot> robotList = new ArrayList<Robot>();
		System.out.println("This needs to be implemented");
		
		// File file = new File(fileName);
		
		// HARD CODE EACH ROBOT.
		// portAssigned = 6665;
		// set of parameters for Aibo1 - Bet
		//Robot aibo = new Robot.Builder(true).icon(GUIConstants.SID_AIBO).name("Aibo One").playerclient(playerServer, portAssigned).pos2D(0).camera(0).build(); //.build(); // cameraAiboTek("192.168.2.155", 10011, 0).pos2DandPTZAiboTek("192.168.2.155", 10050, 10052).
		
		//aibo.setGridX(30);
		//aibo.setGridY(100);
		//aibo.setGridTheta(90);
		
		// Aibo 2 - Rachel (160) - Other? (157)
//		portAssigned++; // = 6666;
		//Robot aibo2 = new Robot.Builder(true).icon(GUIConstants.SID_AIBO).name("Aibo Two").playerclient(playerServer, portAssigned).pos2D(0).camera(0).build(); // cameraAiboTek("192.168.2.160", 10211, 0).pos2DandPTZAiboTek("192.168.2.160", 10150, 10152).build()
		//aibo2.setGridX(160);
		//aibo2.setGridY(160);
		//aibo2.setGridTheta(45);
		
		
		// set of parameters for Surveyor
//		portAssigned = 6667;
		Robot surveyor = new Robot.Builder(true).icon(GUIConstants.SID_SURVEYOR).name("Surveyor One").playerclient(playerServer, portAssigned).pos2D(0).camera(0).build(); // build(); //(true).playerclient(playerServer, portAssigned).pos2D(0).camera(0).build();
		surveyor.setGridX(260);
		surveyor.setGridY(160);
		surveyor.setGridTheta(45);
		
		// Running player in multiple computers. 
		playerServer = "146.245.176.113";
		// portAssigned = 6666;
		Robot surveyor2 = new Robot.Builder(true).icon(GUIConstants.SID_SURVEYOR).name("Surveyor Two").playerclient(playerServer, portAssigned).pos2D(0).camera(0).build(); // build(); //(true).playerclient(playerServer, portAssigned).pos2D(0).camera(0).build();
		surveyor.setGridX(160);
		surveyor.setGridY(60);
		surveyor.setGridTheta(90);
				
//		portAssigned = 6668;
//		Robot surveyor2 = new Robot.Builder(false).icon(ICON_SURVEYOR).name("Surveyor 15").build(); // playerclient(playerServer, portAssigned).pos2D(0).camera(0).build();

//		robotList.add(aibo);
		//robots.add(aibo2);
		robotList.add(surveyor);
		robotList.add(surveyor2);

		return robotList;
	}
}
