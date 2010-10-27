#ifndef _DEFINITIONS_H
#define _DEFINITIONS_H

// Command Preamble Type
typedef unsigned char cmd_len_t;


// Client/Server Commands
#define CMD_ERROR	   "ERROR"       // <--> ERROR <message>
#define CMD_INIT	   "INIT"        // ---> INIT <type> <name> <num-provides> [<provides-list>]
#define CMD_ACK		   "ACK"         // <--- ACK <id>
#define CMD_PING	   "PING"        // <--> PING
#define CMD_PONG	   "PONG"        // <--> PONG
#define CMD_WAIT	   "WAIT"        // <--> WAIT (maybe not used -- use ERROR instead?)
#define CMD_QUIT       "QUIT"        // <--> QUIT
#define CMD_MOVE       "MOVE"        // <--- MOVE <id> <x-vel> <y-vel> <a-vel>
#define CMD_MOVING     "MOVING"      // ---> MOVING
#define CMD_FOUND      "BROADCAST"   // ---> BROADCAST FOUND <color>
#define CMD_STATE      "STATE"       // <--- STATE <id> (currently replaced by ASKPOSE?)
#define CMD_ASKPOSE    "ASKPOSE"     // <--- ASKPOSE <id>
#define CMD_POSE       "POSE"        // ---> POSE <x-pos> <y-pos> <a-pos> <confidence>
#define CMD_ASKPLAYER  "ASKPLAYER"   // <--- ASKPLAYER <id>
#define CMD_PLAYER     "PLAYER"      // ---> PLAYER <port> <ip>
#define CMD_LOCK       "LOCK"        // <--- LOCK
#define CMD_UNLOCK     "UNLOCK"      // <--- UNLOCK
#define CMD_SNAP       "SNAP"        // <--- SNAP <id> (request an image)
#define CMD_IMAGE      "IMAGE"       // ---> IMAGE <image-data>
#define CMD_IDENT      "IDENT"       // IDENT <num-robots> [<robot_id> <name> <type> <num-provides> <provides>]


// Client/Server States
#define STATE_INIT           0
#define STATE_ACK            1
#define STATE_IDLE           2
#define STATE_CMD_PROC       3
#define STATE_CMD_BAD        4
#define STATE_PING_SEND      5
#define STATE_PING_READ      6
#define STATE_PONG_SEND      7
#define STATE_PONG_READ      8
#define STATE_QUIT           9
#define STATE_ERROR         10
#define STATE_MOVING        11
#define STATE_POSE          12
#define STATE_PLAYER        13
#define STATE_GUI_WAIT      14
#define STATE_FOUND         15


// Unique Client Names
#define UID_SURVEYOR_SRV10    "srv10"
#define UID_AIBO_GROWL        "growl"
#define UID_AIBO_BETSY        "betsy"
#define UID_GUI_PABLO         "pablo"


// Client Species Types
#define SID_AIBO         "aibo"
#define SID_SURVEYOR     "surveyor"
#define SID_SCRIBBLER    "scribbler"
#define SID_NXT          "nxt"
#define SID_GUI          "gui"


// Client Capabilities (Provides)
#define CAPS_POSITION2D  "position2d"
#define CAPS_CAMERA      "camera"
#define CAPS_METROCAM    "metrocam"
#define CAPS_BLOBFINDER  "blobfinder"
#define CAPS_RANGER      "ranger"
#define CAPS_SONAR       "sonar"

// Controller States
#define CTRL_HALT 0
#define CTRL_READY 1
enum Mode{ MANUAL, MIXED_INIT, AUTO };  // Modes of operation: manual, mixed initiative and auto control
#define KEY_CTRL_STEP 0          // sets the keyboard control commands the robot to move in discrete steps 
#define KEY_CTRL_CONT 1          // sets the keyboard control commands the robot to move in continuous mode

#endif
