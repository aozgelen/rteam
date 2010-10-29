#ifndef _DEFINITIONS_H
#define _DEFINITIONS_H

// Command Preamble Type
typedef unsigned char cmd_len_t;


// Client/Server Commands
#define CMD_ERROR	 "ERROR"   // <--> ERROR <message>
#define CMD_INIT	 "INIT"    // ---> INIT <type> <name> <num-provides> [<provides-list>]
#define CMD_ACK		 "ACK"     // <--- ACK <id>
#define CMD_PING	 "PING"    // <--> PING
#define CMD_PONG	 "PONG"    // <--> PONG
#define CMD_WAIT	 "WAIT"    // <--> WAIT (maybe not used -- use ERROR instead?)
#define CMD_QUIT     "QUIT"    // <--> QUIT
#define CMD_MOVE     "MOVE"    // <--- MOVE <id> <x-vel> <y-vel> <a-vel>
#define CMD_MOVING   "MOVING"  // ---> MOVING
#define CMD_BROADCAST "BROADCAST" //
#define CMD_STATE    "STATE"   // <--- STATE <id> (currently replaced by ASKPOSE?)
#define CMD_ASKPOSE  "ASKPOSE" // <--- ASKPOSE <id>
#define CMD_POSE     "POSE"    // ---> POSE <x-pos> <y-pos> <a-pos> <confidence>
#define CMD_LOCK     "LOCK"    // <--- LOCK
#define CMD_UNLOCK   "UNLOCK"  // <--- UNLOCK
#define CMD_SNAP     "SNAP"    // <--- SNAP <id> (request an image)
#define CMD_IMAGE    "IMAGE"   // ---> IMAGE <image-data>
#define CMD_IDENT    "IDENT"   // IDENT <num-robots> [ <robot_id> <name> <type> <num-provides> <provides>]
#define CMD_REGISTER "REGISTER" //
#define CMD_UNREGISTER "REGISTER" //
#define CMD_ASK_POSE "ASKPOSE" //
#define CMD_GET_POSE "POSE" //
#define CMD_ASK_PLAYER "ASKPLAYER" //
#define CMD_GET_PLAYER "PLAYER" //


// Client/Server States
#define STATE_INIT      0
#define STATE_ACK       1
#define STATE_PING      2
#define STATE_PONG      3
#define STATE_QUIT      4
#define STATE_ERROR     5
#define STATE_BAD_CMD   6
#define STATE_MOVING    7
#define STATE_GUI_WAIT  8

#define STATE_IDLE	9
#define STATE_IDENT	10
#define STATE_LOCK	11
#define STATE_UNLOCK	12
#define STATE_REGISTER	13
#define STATE_UNREGISTER 14

#define STATE_PROC_CMD	15
#define STATE_MOVE	16

#define STATE_ASK_POSE	17
#define STATE_GET_POSE	18

#define STATE_ASK_PLAYER	19
#define STATE_GET_PLAYER	20
#define STATE_BROADCAST 21


// Unique Client Names
#define UID_AIBO_GROWL   "growl"
#define UID_AIBO_BETSY   "betsy"
#define UID_GUI_PABLO    "pablo"


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

#endif
