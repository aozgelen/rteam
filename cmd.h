/**
 * cmd.h
 *
 * original: sklar/21-june-2010
 * modified: sklar/28-june-2010
 *
 */

#define CMD_ERROR	"ERROR"   // ERROR <text-message>
#define CMD_INIT	"INIT"    // INIT <type> <name> <num-provides> <provides>
#define CMD_ACK		"ACK"     // ACK <robot_id>
#define CMD_PING	"PING"    // PING
#define CMD_PONG	"PONG"    // PONG
#define CMD_WAIT	"WAIT"    // WAIT (maybe not used)
#define CMD_QUIT        "QUIT"    // QUIT
#define CMD_MOVE        "MOVE"    // MOVE <robot_id> <x_vel> <y_vel> <a_vel>
#define CMD_MOVING      "MOVING"  // MOVING
#define CMD_STATE       "STATE"   // STATE <robot_id> 
#define CMD_POSE        "ASKPOSE" // ASKPOSE
#define CMD_POSE        "POSE"    // POSE <x_pos> <y_pos> <a_pos> <confidence>
#define CMD_IDENT       "IDENT"   // IDENT <num-robots> [<robot_id> <name> <type> <num-provides> <provides>]
#define CMD_LOCK        "LOCK"    // LOCK
#define CMD_UNLOCK      "UNLOCK"  // UNLOCK
#define CMD_SNAP        "SNAP"    // SNAP (request an image)
#define CMD_IMAGE       "IMAGE"   // IMAGE <image-data>
