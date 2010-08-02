/**
 * foo.cpp
 *
 * foo client skeleton
 *
 * sklar/28-june-2010
 *
 */

#include <iostream>
#include <string>
#include <queue>
using namespace std;

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/wait.h>
#include <netinet/in.h>

#include "state.h"
#include "cmd.h"
#include "commClient.h"
#include "pose.h"
#include "robot.h"




/**
 * main()
 *
 * This function handles the main processing of the skeleton foo client.
 * The program follows this state machine:
 *                                                    +---------------------------------------+
 *                                                    |                                       |
 *                                                    v                         /--read-pong--+
 * --start-> [INIT] --send-init-> [ACK] --read-ack-> [PING] --send-ping-> [PONG]
 *                                                    ^                         \--read-move--+
 *                                                    |                                       |
 *                                                    +--send-moving----- [MOVING] <----------+
 *
 *
 */
int main( int argc, char *argv[] ) {

  int port, num_ping, max_ping, state, msg_num=0;
  char *host, obuf[64], *p;
  unsigned char len;
  robot *myrobot = new robot();

  // parse command-line arguments
  if ( argc < 6 ) {
    printf( "usage: ./foo <host> <port> <owner> <type> <name> [max_ping]\n" );
    exit( 1 );
  }
  myrobot->set_host( argv[1] );
  sscanf( argv[2],"%d",&port );
  myrobot->set_port( htons( port ));
  myrobot->set_owner( argv[3] );
  myrobot->set_type( argv[4] );
  myrobot->set_name( argv[5] );
  if ( argc > 6 ) {
    sscanf( argv[6],"%d",&max_ping ); // terminate after max_ping pings have been sent (for debugging)
  }
  else {
    max_ping = -1; // only terminate when program is killed or crashes
  }

  // create client socket on port...
  printf( "foo: port=%d\n",myrobot->get_port() );
  commClient *comm = new commClient( myrobot->get_host(), myrobot->get_port() );

  // loop, reading input from client and sending it to server
  state = STATE_INIT;
  num_ping = 0;
  while ( state != STATE_QUIT ) {
    // decide which message to send
    if ( num_ping == max_ping ) {
      state = STATE_QUIT;
    }
    switch ( state ) {
    case STATE_INIT:
      sprintf( obuf,"%s\0",CMD_INIT );
      state = STATE_ACK;
      break;
    case STATE_PING:
      sprintf( obuf,"%s\0",CMD_PING );
      num_ping++;
      state = STATE_PONG;
      break;
    case STATE_MOVING:
      sprintf( obuf,"%s\0",CMD_MOVING );
      state = STATE_PONG;
      break;
    case STATE_QUIT:
      sprintf( obuf,"%s\0",CMD_QUIT );
      break;
    default: // STATE ERROR
      // we'll probably never get here, but just in case...
      cout << "**error> invalid state\n";
      break;
    } // end of switch
    // send message to server
    p = obuf;
    len = strlen( p );
    cout << "foo: state=" << state << " sending message #" << msg_num++ << " = [" << p << "]\n";
    if ( comm->send_msg( len, p ) == -1 ) {
      perror( "**error> foo/send" );
      exit( 1 );
    }
    // wait for response from server, if not quitting
    if ( state != STATE_QUIT ) {
      if ( comm->read_msg( len, &p ) == -1 ) {
	perror( "**error> foo/receive" );
	exit( 1 );
      }
      // got message from server
      cout << "foo: read message #" << msg_num++ << " = [" << p << "]\n";
      // process message
      char *cmd = strtok( p," " );
      char *ctmp;
      if ( ! strcmp( cmd,CMD_ACK )) {
	ctmp = strtok( NULL," " );
	long tmp;
	sscanf( ctmp,"%ld",&tmp );
	myrobot->set_session_id( tmp );
	cout << "foo: myrobot = ";
	myrobot->print_ident();
	state = STATE_PING;
      }
      else if ( ! strcmp( cmd,CMD_PONG )) {
	state = STATE_PING;
      }
      else if ( ! strcmp( cmd,CMD_ERROR )) {
	cout << "**error> command error\n";
      }
      else if ( ! strcmp( cmd,CMD_MOVE )) {
	state = STATE_MOVING;
      }
      else {
	cout << "**error> invalid cmd\n";
      }
      // sleep for a second and then go back for more...
      sleep( 1 );
    }
  } // end while

  // all done. pack up and go home.
  delete comm;
  return( 0 );

} // end of main()
