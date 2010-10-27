package metrobotics;

/**
 * @author Pablo Munoz - Metrobotics
 *
 */
public interface GUIConstants {
	
	// Robot Unique Ids
	// Unique Client Names
	public static final String UID_AIBO_GROWL = "growl";
	public static final String UID_AIBO_BETSY = "betsy";
	public static final String UID_GUI_PABLO  = "pablo";
	
	// Client Species Types
	public static final String SID_AIBO 	 = "aibo";
	public static final String SID_SURVEYOR  = "surveyor";
	public static final String SID_SCRIBBLER = "scribbler";
	public static final String SID_NXT 		 = "nxt";
	public static final String SID_GUI 		 = "gui";
	
	// Robot sizes
	public static final int ROBOT_SURVEYOR_WIDTH = 13;
	public static final int ROBOT_SURVEYOR_HEIGHT = 10;

	// Client/Server Commands
	public static final String CMD_ERROR 	= "ERROR";   // <--> ERROR <message>
	public static final String CMD_INIT		= "INIT";    // ---> INIT <type:int> <name:char[64]> <num-provides> [<provides-list>]
	public static final String CMD_ACK 		= "ACK";     // <--- ACK <id>
	public static final String CMD_PING		= "PING";    // <--> PING
	public static final String CMD_PONG		= "PONG";    // <--> PONG
	public static final String CMD_WAIT		= "WAIT";    // <--> WAIT (maybe not used -- use ERROR instead?)
	public static final String CMD_QUIT  	= "QUIT";    // <--> QUIT
	public static final String CMD_MOVE     = "MOVE";    // <--- MOVE <id> <x-vel> <y-vel> <a-vel>
	public static final String CMD_MOVING   = "MOVING";  // ---> MOVING
	public static final String CMD_STATE    = "STATE";   // <--- STATE <id> (currently replaced by ASKPOSE?)
	public static final String CMD_ASKPOSE  = "ASKPOSE"; // <--- ASKPOSE <id>
	public static final String CMD_POSE     = "POSE";    // ---> POSE <x-pos> <y-pos> <a-pos> <confidence>
	public static final String CMD_LOCK     = "LOCK";    // <--- LOCK
	public static final String CMD_UNLOCK   = "UNLOCK";  // <--- UNLOCK
	public static final String CMD_SNAP     = "SNAP";    // <--- SNAP <id> (request an image)
	public static final String CMD_IMAGE    = "IMAGE";   // ---> IMAGE <image-data>
	public static final String CMD_IDENT    = "IDENT";   // IDENT <num-robots> [<robot_id> <name> <type> <num-provides> <provides>]

	// Client/Server States
	public static final int STATE_INIT     = 0;
	public static final int STATE_ACK 	   = 1;
	public static final int STATE_PING     = 2;
	public static final int STATE_PONG     = 3;
	public static final int STATE_QUIT     = 4;
	public static final int STATE_ERROR    = 5;
	public static final int STATE_BAD_CMD  = 6;
	public static final int STATE_MOVING   = 7;
	public static final int STATE_GUI_WAIT = 8;

	// Client Capabilities (Provides)
	public static final String CAPS_POSITION2D = "position2d";
	public static final String CAPS_CAMERA     = "camera";
	public static final String CAPS_METROCAM   = "metrocam";
	public static final String CAPS_BLOBFINDER = "blobfinder";
	public static final String CAPS_RANGER     = "ranger";
	public static final String CAPS_SONAR      = "sonar";
	

    // interface timeout/refresh intervals
    public static final int STATE_UPDATE_INTERVAL = 1000;
    public static final int STATE_UPDATE_SLEEP_TIME = 50;

    // DATABASE
    public static final String DATABASE_URL = "jdbc:mysql://localhost:3306/metroDB";
	public static final int DB_MSG_POSITION = 0;
    
    
    // GUI LAYOUTS
    public static final int LAYOUT_ORIGINAL = 0;
    public static final int LAYOUT_BIGMAP = 1;
}
