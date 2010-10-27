package metrobotics;

import java.util.ArrayList;


/**
 * @author Pablo Mu–oz METROBOTICS <br />
 * 
 * The Gui class contains the basic information that the Gui needs to run.
 * Whether we are using a Central Server or not
 * Central Server IP, ports
 * The id that the Central Server gives to the GUI
 * The state in which the Gui is. See GuiState class, which is the thread that runs the state machine.
 */
public class Gui {
	public static boolean useCentralServer;
	public static boolean useMysql;
	public static ServerComm serverComm;
	// First database
	// public static MetroDatabase db;
	public static String centralServerIP;
	public static int centralServerImagePort;
	public static int centralServerInitMsgPort;
	public static long guiId = 0;
	//public static long guiIdPos2d;
	public static String guiName = "ELGUI";
	public static volatile int GUIState;
	public static boolean debug;
	public static int layoutSelection;
	
	public static int getGUIState(){
		return GUIState;
	}
	public static void setGUIState(int state){
		GUIState = state;
	}
	public static void setGUIId(long parseLong) {
		guiId = parseLong;
	}
	public static long getGUIId() {
		return guiId;
	}
	
}