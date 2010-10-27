package metrobotics;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Scanner;

/**
 * @author Pablo Munoz - Metrobotics
 * This class is in charge of communicating with the Central Server.
 * It interacts with GuiState, which is the Thread that inits, acks, checks for messages, 
 * and requests positions. 
 */
public class ServerComm {
	static Socket socInitMsg = null;
	static Socket socImages = null;
	PrintWriter outInit = null; // CHANGE THIS. I NEED ANOTHER TYPE OF OUTPUT
	BufferedReader inInit = null;
	OutputStream outSt = null;
	InputStream inSt = null;
	String toServer, fromServer;
	DataOutputStream daout = null;
	DataInputStream dataIn = null;
	private MainFrame mf;
	Calendar cal;
	ArrayList<Robot> robots;
	Robot bot;
	
	ServerComm(MainFrame mf, ArrayList<Robot> robots){
		this.robots = robots;
		this.mf = mf;
		try {
			System.out.println("Sending Request");
			socInitMsg = new Socket(Gui.centralServerIP, Gui.centralServerInitMsgPort);
			//socInitMsg.setTcpNoDelay(true);
			//socInitMsg.setTrafficClass(0x10);
			if(socInitMsg.isConnected()){
				System.out.println("Aknowledgement Received");
			}
			outSt = socInitMsg.getOutputStream();
			inSt = socInitMsg.getInputStream();
			daout = new DataOutputStream(outSt);
			dataIn = new DataInputStream(inSt);
			//socImages = new Socket(Robot.centralServerIP, Robot.centralServerImagePort);

		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}		
	}
	public void init(){
		// Sending INIT
		toServer = "INIT" + " " +  GUIConstants.SID_GUI + " " +  Gui.guiName + " " + 0  + " " + 0;
		if(Gui.debug){
			System.out.println(toServer);
		}
		writeStream(toServer);
	}
	
	public boolean ack(){
		// Reading ACK
		String regex = "[\\d]";
		String ackString = readStream();
		Scanner parse = new Scanner(ackString);
		parse.next();
		String guiToParse = parse.next();
		if(guiToParse.isEmpty()){  // TODO || !guiToParse.matches(regex)){
			return false;
		}
		Gui.setGUIId(Long.parseLong(guiToParse));
		System.out.println(Gui.getGUIId());
		mf.guiId.setText("GUI ID: " + Gui.getGUIId());
		if(Gui.debug){
			System.out.println("ACK received and processed");
		}
		return true;
	}
	public void check4Msg() {
		cal = Calendar.getInstance();
		try {
			if(dataIn.available()>0){
				String message = readStream();
				mf.messageFromServer.setText(" From Server " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + " " + message);
				Scanner msgScan = new Scanner(message);
				String messageToken = msgScan.next();
				if((messageToken.compareTo("IDENT")) == 0){
					if(Gui.debug){
						System.out.println("Check4Msg: IDENT received and calling identProc()");
					}
					identProc(msgScan);
				}
				else if((messageToken.compareTo("POSE")) ==0){
					aksPoseProc(msgScan);
				}
				else if((messageToken.compareTo("PLAYER")) == 0){
					askPlayerProc(msgScan);
				}
				else if((messageToken.compareTo("FOUND")) == 0){
					foundObjectProc(msgScan);
				}
				else {
					System.out.println("Message Unknown");
				}
				if(Gui.debug){
					System.out.println(message);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void foundObjectProc(Scanner msgScan){
		if(Gui.debug){
			System.out.println();
		}
		System.out.println("FOUND OBJECT " + msgScan.toString());
		// FOUND ID COLOR
		// PRINT A DIALOG SAYING "Object found " CLICK OK AND LEAVE. 
		// COMPLETE THIS WITH FORMAT JOEL
	}
	public void askPose(long UniqueId) {
		// Sending ASKPOSE
		toServer = "ASKPOSE " + UniqueId;
		writeStream(toServer);
		if(Gui.debug){
			System.out.println("Requesting ASKPOSE");
		}
	}
	
	public void aksPoseProc(Scanner msgScan){
		if(Gui.debug){
			System.out.println("askPoseProc() Processing ASKPOSE");
			System.out.println("askPoseProc() Robot num = " + robots.size());
		}
		
		long []robotsId = new long[robots.size()];
		int []robotsKey = new int[robots.size()];
		boolean [] checkDrop = new boolean[robots.size()];
		boolean callIdent = false;
		int r = 0;
		for(Robot x : robots){
			robotsId[r] = x.getUniqueId();
			robotsKey[r] = x.getRobotKey();
			r++;
		}
		// ASKPOSE nBots UniqueID x y theta conf. 
		int size = Integer.parseInt(msgScan.next());
		for(int i=0; i<size; i++){
			long unique_id = Long.parseLong(msgScan.next());
			if(Gui.debug){
				System.out.println("Unique ID: " + unique_id);
			}
			double x = Double.parseDouble(msgScan.next());
			double y = Double.parseDouble(msgScan.next());
			double t = Double.parseDouble(msgScan.next());
			double conf = Double.parseDouble(msgScan.next());
			
			int robotInListIndex = -1;
			int k = 0;
			// Check if the robot is already the list
			for(long id : robotsId){
				if(id == unique_id){
					robotInListIndex = k;
					break;
				}
				k++;
			}
			
			if(robotInListIndex == -1){
				callIdent = true;
			}
			else if(robotInListIndex<robots.size()){
				checkDrop[robotInListIndex] = true;
				//bot = new Robot.Builder(false).build();
				// Change this to:
				robots.get(robotsKey[robotInListIndex]).setGridX(x); 
				robots.get(robotsKey[robotInListIndex]).setGridY(y); 
				robots.get(robotsKey[robotInListIndex]).setGridTheta(t);
				robots.get(robotsKey[robotInListIndex]).setGridConfidence(conf);
				// etc... and test
				// Old code DATABASE
//				if(Gui.useMysql){
//					String msgDB = unique_id + " " + x + " " + y + " " + t + " "; 
//					Gui.db.insertMetroTable(System.currentTimeMillis(), GUIConstants.DB_MSG_POSITION, msgDB);
//				}
				
//				bot = robots.get(robotsKey[robotInListIndex]);
//				bot.setGridX(x);
//				bot.setGridY(y);
//				bot.setGridTheta(t);
//				bot.setGridConfidence(conf);
				if(Gui.debug){
					System.out.println("Updated position for Robot " + robots.get(robotsKey[robotInListIndex]).getUniqueId());
				}
				mf.grid.repaint();
				
			}
		}
		// Check for decrease in number of robots
		int i=0;
		boolean repaint = false;
		for(boolean b : checkDrop){
			if(b==false){
				robots.remove(i);
				Robot.Builder.setRobotCounter(robots.size());
				int j=0;
				for(Robot x : robots){
					x.setRobotKey(j);
					j++;
				}
				repaint = true;
			}
			i++;
		}
		if(repaint){
			if(Gui.debug){
				System.out.println("Creating new RobotSel and Grid to reflect changes in robot list");
			}
			if(Gui.layoutSelection == GUIConstants.LAYOUT_ORIGINAL){
				mf.newRobotSelAndGrid();
			}
		}
		
		if(callIdent){
			this.writeStream("IDENT");
			if(Gui.debug){
				System.out.println("Sending IDENT to update robots list");
			}
		}
		
	}
	
	public void askPlayerProc(Scanner msgScan){
		System.out.println("PLAYER Info received");
		if(Gui.debug){
			System.out.println("askPlayerProc() Processing ASKPLAYER");
			System.out.println("askPlayerProc() Robot num = " + robots.size());
		}
		// TODO: This code is incomplete
		long []robotsId = new long[robots.size()];
		int []robotsKey = new int[robots.size()];
		// boolean [] checkDrop = new boolean[robots.size()];
		// boolean callIdent = false;
		int r = 0;
		for(Robot x : robots){
			robotsId[r] = x.getUniqueId();
			robotsKey[r] = x.getRobotKey();
			r++;
		}
		// ASKPLAYER nBots UniqueID and add PlayerClient if it hasn't been added yet.
		// The server is sending the GUIID for no reason, so we are just disregarding it
		long GUI_id = Long.parseLong(msgScan.next()); // This is disregarded. FIX THIS ON THE SERVER SIDE.
		
		long unique_id = Long.parseLong(msgScan.next());
		if(Gui.debug){
			System.out.println("Unique ID: " + unique_id);
		}
		
		String playerIP = msgScan.next();
		int port = Integer.parseInt(msgScan.next());
		
		int k = 0;
		// Check if the robot is already the list
		for(long id : robotsId){
			if(id == unique_id){
				System.out.println(robots.get(robotsKey[k]).getHasCamera());
				if(!robots.get(robotsKey[k]).getHasCamera()){
					// TODO: Create PlayerClient and request CameraProxy 
					robots.get(robotsKey[k]).setPlayerClientAndCamera(playerIP, port);
					System.out.println("This is the Player IP received: " + playerIP);
					
				}
			//	robotInListIndex = k;
			//	break;
			}
			k++;
		}
	}
	
	
	public void identProc(Scanner msgScan){
		//System.out.println("Ident receiced: " + msgScan.toString());
		long [] robotsId = new long[robots.size()]; 
		boolean robotExists = false;
		int r = 0;
		for(Robot x : robots){
			robotsId[r] = x.getUniqueId();
			r++;
		}
		Arrays.sort(robotsId);
		int size = Integer.parseInt(msgScan.next());
		if(Gui.debug){
			System.out.println("Number of robots: " + size);
		}
		for(int i=0; i<size; i++){
			// Unique_id
			long unique_id = Long.parseLong(msgScan.next());
			if(Gui.debug){
				System.out.println("Unique ID: " + unique_id);
			}
			if(Arrays.binarySearch(robotsId, unique_id)>=0){
				robotExists = true;
			}
			// String clientUniqueName 
			String name = msgScan.next();
			
			// STring clientType
			String type = msgScan.next();
			
			// int num_provides
			int num_prov = Integer.parseInt(msgScan.next());
			for(int j=0; j<num_prov; j++){
				// n strings
				// TODO: Handle proxies. 
				msgScan.next();
			}
			if(!robotExists){
				bot = new Robot.Builder(false).build();
				bot.setUniqueId(unique_id);
				bot.setName(name);
				bot.setRobotIcon(type);
				robots.add(bot);
				
				// Get Player info for Camera
				Gui.serverComm.writeStream("ASKPLAYER " + unique_id);
				System.out.println("Requesting player for " + unique_id); 
				
			}
			robotExists = false;
		}
		if(Gui.layoutSelection == GUIConstants.LAYOUT_ORIGINAL){
			mf.newRobotSelAndGrid();
		}

	}
	
	public String readStream() {
		StringBuffer sb = new StringBuffer();
		int size;
		try {
			size = dataIn.read(); // First byte is size
			for(int i=0; i<size; i++){
				int incoming = dataIn.read();
				sb.append((char)incoming);
			}
			fromServer = sb.toString();
			if(Gui.debug){
				System.out.println("readStream() Read from server: " + fromServer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fromServer;
	}
	
	public synchronized void writeStream(String message){
		message = message + '\0';
		System.out.println("Message sent: " + message + " " + System.currentTimeMillis());
		try {
			daout.writeByte(message.length());
			daout.write(message.getBytes(), 0, message.length()); 
			daout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} 	
	}

	
	// OLD METHODS TO CLEAN
	// Please disregard this methods. I used them with my testing server.
	// I will delete them soon. Pablo
	
	public long init(DataOutputStream outData, DataInputStream inData, String name) {

		// Sending INIT
		toServer = "INIT" + " " +  GUIConstants.SID_GUI + " " +  name + " " + 0  + " " + 0 + '\0';
		
		try {
			outData.writeByte(toServer.length());
			outData.write(toServer.getBytes(), 0, toServer.length()); 
			outData.flush();
			
			// Reading ACK
			StringBuffer sb = new StringBuffer();
			int size = inData.read(); // First byte is size.
			System.out.println(size);
			for(int i=0; i<size; i++){
				int incoming = inData.read();
				sb.append((char)incoming);
			}
			fromServer = sb.toString();
			System.out.println(fromServer);
			
//			byte [] buf = new byte[4]; 
//			int charsRead = inData.read(buf, 0, 4);
//			System.out.println(charsRead);
//			fromServer = new String(buf); 
//			System.out.println(fromServer);
			
			// Get guiID; // This Works but I am trying to implement one that reads until the buffer is empty. See below.
//			byte [] guiId = new byte[11];
//			charsRead = inSt.read(guiId, 0, 11); 
//			System.out.println(charsRead); 
//			fromServer = new String(guiId);
//			for(int i=0; i<fromServer.length(); i++){
//				if(!Character.isDigit(fromServer.charAt(i)))
//					fromServer.replace(fromServer.charAt(i), (char) 32);
//			}
//			fromServer = fromServer.trim();
//			long gui_id = Long.parseLong(fromServer);
//			System.out.println(fromServer);
//			Robot.setGuiId((int)gui_id);  
			
			//sb = new StringBuffer();
			//int incomingChar = inData.read(); // dataIn.read();
			//do{
				//incomingChar = inData.read(); //dataIn.read();
				//sb.append((char)incomingChar);
				//System.out.print(incomingChar);
			//}while(inData.available() > 0);
			//System.out.println();
			//fromServer = sb.toString();
			//for(int i=0; i<fromServer.length(); i++){
				//if(!Character.isDigit(fromServer.charAt(i)))
					//fromServer.replace(fromServer.charAt(i), (char)32);
			//}
			//fromServer = fromServer.trim();
			//gui_id = Long.parseLong(fromServer);
			//System.out.println(fromServer);
			
			Scanner parse = new Scanner(fromServer);
			parse.next();
			//gui_id = Long.parseLong(parse.next());
			//System.out.println(gui_id);
			
			 
			
		} catch (IOException e) {
			e.printStackTrace();
		} 		
		//return gui_id;
		return 0;
	}
	
	
	
	public ArrayList<Robot> identRequest(){
		ArrayList<Robot> robotList = new ArrayList<Robot>();
		// Request IDENT for the first time (Request the Global State)
		toServer = " IDENT" + '\0';
		try {
			daout.write(toServer.getBytes(), 0, toServer.length());
			daout.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return robotList;
	}
	
	// This should be used with ControlServerMain 
	public ArrayList<Robot> initSimulation() {
		// TODO: This should be deleted when my fake server can run simulations. ControlServerMain...
		ArrayList<Robot> robotList = new ArrayList<Robot>();;
		
		// Sending INIT
		toServer = " INIT" + " " +  GUIConstants.SID_GUI + " ELGUI " + 0 + " " + 0 + "\0";
		
		try {
			daout.write(toServer.getBytes(), 0, toServer.length()); 
			daout.flush();
			
			System.out.println("Init sent: " + toServer);
			
			// Reading ACK
			byte [] buf = new byte[4]; 
			int charsRead = inSt.read(buf, 0, 4);
			System.out.println(charsRead);
			fromServer = new String(buf); 
			System.out.println(fromServer);
			
			// Get guiID; // This Works but I am trying to implement one that reads until the buffer is empty. See below.
//			byte [] guiId = new byte[11];
//			charsRead = inSt.read(guiId, 0, 11); 
//			System.out.println(charsRead); 
//			fromServer = new String(guiId);
//			for(int i=0; i<fromServer.length(); i++){
//				if(!Character.isDigit(fromServer.charAt(i)))
//					fromServer.replace(fromServer.charAt(i), (char) 32);
//			}
//			fromServer = fromServer.trim();
//			long gui_id = Long.parseLong(fromServer);
//			System.out.println(fromServer);
//			Robot.setGuiId((int)gui_id);  
			

			StringBuffer sb = new StringBuffer();
			int incomingChar = dataIn.read();
			do{
				sb.append((char)incomingChar);
				incomingChar = dataIn.read();
			}while(incomingChar != '\0');
			fromServer = sb.toString();
			for(int i=0; i<fromServer.length(); i++){
				if(!Character.isDigit(fromServer.charAt(i)))
					fromServer.replace(fromServer.charAt(i), (char) 32);
			}
			fromServer = fromServer.trim();
			long gui_id = Long.parseLong(fromServer);
			System.out.println(fromServer);
			//Gui.setGuiId(gui_id);  
			
			
			// Request IDENT (Request the Global State)
			
			toServer = " IDENT";
			daout.write(toServer.getBytes(), 0, toServer.length()); 
			daout.flush();
			
			// Loop for reading Initial Configuration
			
			// This is the structure that the Central Server is going to send for the config.
			//char init = (char)inSt.read();
			//init = swapByteOrderInt(init);
			//System.out.println((int)init);
			
			int FIELD_SIZE = 64;
			char command_id; // Using charInUse
			//int session_id; // Using intInUse
		    int robotUnique_id; // name should be derived from here
		    int owner_id; // Using intInUse
		    int species_id; // type_id; // Using intInUse 
		    // State:
		    int x; // Using intInUse
		    int y = 0; // Using intInUse
		    int theta; // Using intInUse
		    int speed_x;
		    int speed_y;
		    int speed_theta;
		    // int num_of_provides; // Using intInUse
		    int provides; // String [] provides; //char [] name = new char[FIELD_SIZE]; //String name;
		    // so the formula so far for number of bytes is: 7 x 4 (bytes) +
		    // num_of_provides x 128 + 128bytes. 
		    
			Robot botToAdd = new Robot.Builder(false).build();
		    
			// For testing with my server
			//if(true)
			//return robotList;
			
			//c_id = new byte[1];
			// charsRead2 = inSt.read(c_id, 0, 1); 
			//System.out.println(charsRead2);
			// fromServer = new String(c_id);
			command_id = (char)inSt.read(); //(char)(c_id[0] & 0xFF);
			
			do{ // if command_id = 3 the config is done. ACK is 2
				System.out.println("Command ID" + (int)command_id);
				
				// command_id 
				if(command_id!=10)
				botToAdd = new Robot.Builder(false).build();
				
				// unique_id
				robotUnique_id = dataIn.readInt();
				//robotUnique_id = swapByteOrderInt(robotUnique_id);
				botToAdd.setUniqueId(robotUnique_id);
				System.out.println("UniqueId" + robotUnique_id);
				
				//if(command_id!=10)
				//botToAdd.setSessionId(session_id);
				//System.out.println("Session ID " + session_id);// session_id);
				
				// owner_id
				owner_id = dataIn.readInt();
				//owner_id = swapByteOrderInt(owner_id);
				System.out.println("Owner Id  " + owner_id);
				botToAdd.setOwnerId(owner_id);
				
				
				// type_id;
				species_id = dataIn.readInt();
				//species_id = swapByteOrderInt(species_id);
				if(command_id!=10)
				// botToAdd.setRobotIcon(species_id);
				System.out.println("Type " + species_id);
				
				// x;
				x = dataIn.readInt();
				//x = swapByteOrderInt(x);
				System.out.println(x);
	
				if(command_id!=10)
				botToAdd.setGridX(x);
				
				// y;
				y = dataIn.readInt();
				//y = swapByteOrderInt(y);
				if(command_id!=10)
				botToAdd.setGridY(y);
				System.out.println(" y " + y);
				
				// theta;
				theta = dataIn.readInt();
				//theta = swapByteOrderInt(theta);
				if(command_id!=10)
				botToAdd.setGridTheta(theta);
				System.out.println("theta " + theta);
				
				/* Commented to test with my server
				speed_x = dataIn.readInt();
				speed_x = swapByteOrderInt(speed_x);
				System.out.println(speed_x);
				//speed_x = swapByteOrderDouble(speed_x);
				System.out.println("speed_x " + speed_x);
				
				speed_y = dataIn.readInt();
				speed_y = swapByteOrderInt(speed_y);
				System.out.println(speed_y);
				// speed_x = swapByteOrder(speed);
				System.out.println("speed_y" + speed_y);
				
				speed_theta = dataIn.readInt();
				speed_theta = swapByteOrderInt(speed_theta);
				System.out.println(speed_theta);
				// speed_x = swapByteOrder(speed);
				System.out.println("speed_theta " + speed_theta);
				
				provides = dataIn.readInt();
				provides = swapByteOrderInt(provides);
				System.out.println("Provides " + provides);
				
				// Fix Name
				//byte [] nameByte = new byte[FIELD_SIZE];
				//int charsReadName = inSt.read(nameByte, 0, FIELD_SIZE); 
				//name = new String(nameByte);
				//if(command_id!=10)
				//botToAdd.setName(name);
				//System.out.println("Name " + name + " " + charsReadName);
				
				//int charNull;
				//while((charNull = inSt.read())== '\0'){
					
				//}
				
				if(command_id!=10)
				robotList.add(botToAdd);
				//command_id = (char)charNull; //(char)inSt.read();
				 * 
				 * 
				 */ //END OF COMMENT TO TEST WITH MY SERVER
				if(command_id!=10)
					robotList.add(botToAdd);
				
				command_id = (char)inSt.read();
	
			}while(command_id != 10);
			// END OF CONFIGURATION LOOP 
			// Read death Monkey and ignore
			
			//toServer = "QUIT\0";
			//outSt.write(toServer.getBytes(), 0, 5);
			//outSt.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}  

		
		//outInit.write(toServer);
		//DataOutputStream daout = new DataOutputStream(outSt);
		
		// Testing Joel Server
		//try {
			//fromServer = inInit.readLine();
			//System.out.println(fromServer);
			
			// Closing connection with Joel's server
			//System.out.println("Sending QUIT");
			//toServer = "QUIT";
			//outInit.print(toServer);
			
		//} catch (IOException e) {
			//e.printStackTrace();
		//}
		// End of testing


		
//		try {
//			while((fromServer = inInit.readLine()) != null){
//				System.out.println(fromServer);
//				toServer = "PING";
//				outInit.println(toServer);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
			
//		
		
//		try {
//			while((fromServer = inInit.readLine()) != null){
//				if(fromServer.compareTo("start") == 0){
//					System.out.println("Building new list of Robots");
//				}
//				if(fromServer.compareTo("Robot") == 0){
//					botToAdd = new Robot.Builder(false).build();
//					fromServer = inInit.readLine();
//					botToAdd.setRobotIcon(Integer.parseInt(fromServer));
//					fromServer = inInit.readLine();
//					botToAdd.setName(fromServer);
//					fromServer = inInit.readLine();
//					botToAdd.setGridX(Double.parseDouble(fromServer));
//					fromServer = inInit.readLine();
//					botToAdd.setGridY(Double.parseDouble(fromServer));
//					fromServer = inInit.readLine();
//					botToAdd.setGridTheta(Double.parseDouble(fromServer));
//					robotList.add(botToAdd);
//				}
//				if(fromServer.compareTo("end") == 0){
//					break;
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		System.out.println("Finished Initialization");
		return robotList;
	}

	public ArrayList<Robot> requestGlobalState(){ // IDENT
		ArrayList<Robot> robotsnewState = new ArrayList<Robot>();
		try {
			// Request IDENT (Request the Global State)
			toServer = " IDENT";
			daout.write(toServer.getBytes(), 0, toServer.length()); 
			daout.flush();
			
			Robot newBots = new Robot.Builder(false).build();
			char command_id = (char)inSt.read(); //(char)(c_id[0] & 0xFF);
			do{ // if command_id = 3 the config is done. ACK is 2
				System.out.println("Command ID" + (int)command_id);
				
				// command_id 
				if(command_id!=10)
				newBots = new Robot.Builder(false).build();
				
				// unique_id
				int robotUnique_id = dataIn.readInt();
				//robotUnique_id = swapByteOrderInt(robotUnique_id);
				newBots.setUniqueId(robotUnique_id);
				System.out.println("UniqueId" + robotUnique_id);
				
				// owner_id
				int owner_id = dataIn.readInt();
				//owner_id = swapByteOrderInt(owner_id);
				System.out.println("Owner Id  " + owner_id);
				newBots.setOwnerId(owner_id);
				
				
				// type_id;
				int species_id = dataIn.readInt();
				//species_id = swapByteOrderInt(species_id);
				if(command_id!=10)
				//newBots.setRobotIcon(species_id);
				System.out.println("Type " + species_id);
				
				// x;
				int x = dataIn.readInt();
				//x = swapByteOrderInt(x);
				System.out.println(x);

				if(command_id!=10)
				newBots.setGridX(x);
				
				// y;
				int y = dataIn.readInt();
				//y = swapByteOrderInt(y);
				if(command_id!=10)
				newBots.setGridY(y);
				System.out.println(" y " + y);
				
				// theta;
				int theta = dataIn.readInt();
				//theta = swapByteOrderInt(theta);
				if(command_id!=10)
				newBots.setGridTheta(theta);
				System.out.println("theta " + theta);
				
				/* Commented to test with my server
				speed_x = dataIn.readInt();
				speed_x = swapByteOrderInt(speed_x);
				System.out.println(speed_x);
				//speed_x = swapByteOrderDouble(speed_x);
				System.out.println("speed_x " + speed_x);
				
				speed_y = dataIn.readInt();
				speed_y = swapByteOrderInt(speed_y);
				System.out.println(speed_y);
				// speed_x = swapByteOrder(speed);
				System.out.println("speed_y" + speed_y);
				
				speed_theta = dataIn.readInt();
				speed_theta = swapByteOrderInt(speed_theta);
				System.out.println(speed_theta);
				// speed_x = swapByteOrder(speed);
				System.out.println("speed_theta " + speed_theta);
				
				provides = dataIn.readInt();
				provides = swapByteOrderInt(provides);
				System.out.println("Provides " + provides);
				
				// Fix Name
				//byte [] nameByte = new byte[FIELD_SIZE];
				//int charsReadName = inSt.read(nameByte, 0, FIELD_SIZE); 
				//name = new String(nameByte);
				//if(command_id!=10)
				//botToAdd.setName(name);
				//System.out.println("Name " + name + " " + charsReadName);
				
				//int charNull;
				//while((charNull = inSt.read())== '\0'){
					
				//}
				
				if(command_id!=10)
				robotList.add(botToAdd);
				//command_id = (char)charNull; //(char)inSt.read();
				 * 
				 * 
				 */ //END OF COMMENT TO TEST WITH MY SERVER
				if(command_id!=10)
					robotsnewState.add(newBots);
				
				command_id = (char)inSt.read();

			}while(command_id != 10);
		} catch (IOException e1) {
			e1.printStackTrace();
		}  
		System.out.println("Finished New State");
		return robotsnewState;
	}


	public boolean sendReqGrab() {
		return true;
	}

	public boolean sendReqUnGrab() {
		return false;
	}



	
	
	
}
