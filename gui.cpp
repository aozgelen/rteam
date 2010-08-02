/**
 * gui.cpp
 *
 * GUI client skeleton
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
 * This function handles the main processing of the skeleton user interface.
 * The program follows a simple state machine:
 *
 *                                                        
 * --start-> [INIT] --send-init-> [ACK] --read-ack-> [GUI_WAIT] --send-user-command-+
 *                                                    ^                             |
 *                                                    |                             |
 *                                                    +-----------------------------+
 *
 *
 */
int main( int argc, char *argv[] ) {

  int port, state;
  char obuf[64], *p;
  unsigned char len;
  robot *myrobot = new robot();

  // parse command-line arguments
  if ( argc < 3 ) {
    printf( "usage: ./gui <host> <port>\n" );
    exit( 1 );
  }
  myrobot->set_host( argv[1] );
  sscanf( argv[2],"%d",&port );
  myrobot->set_port( htons( port ));

  // create client socket on port...
  printf( "gui: port=%d\n",myrobot->get_port() );
  commClient *comm = new commClient( myrobot->get_host(), myrobot->get_port() );

  // loop, reading input from user and sending it to server
  state = STATE_INIT;
  while ( state != STATE_QUIT ) {
    // decide which message to send
    switch( state ) {
    case STATE_INIT:
      // initialize gui
      sprintf( obuf,"%s gui\0",CMD_INIT );
      state = STATE_ACK;
      break;
    default: // STATE_GUI_WAIT
      printf( "enter message to send (q to quit): " );
      p = obuf;
      p = fgets( p,64,stdin );
      if ( p[0] == 'q' ) {
	state = STATE_QUIT;
	sprintf( p, "%s\n", CMD_QUIT );
      }
      p = strtok( p,"\n" );
    } // end of switch
    // send message to server
    p = obuf;
    len = strlen(p);
    cout << "gui: writing message [" << p << "]\n";
    if ( comm->send_msg( len, p ) == -1 ) {
      perror( "gui/send" );
      exit( 1 );
    }
    // wait for response from server, if not quitting
    if ( state != STATE_QUIT ) {
      if ( comm->read_msg( len, &p ) == -1 ) {
	perror( "gui/receive" );
	exit( 1 );
      }
      // got message from server
      cout << "gui: read message = [" << p << "]\n";
      // process message
      char *cmd = strtok( p," " );
      char *ctmp;
      if ( ! strcmp( cmd,CMD_ACK )) {
	ctmp = strtok( NULL," " );
	long tmp;
	sscanf( ctmp,"%ld",&tmp );
	myrobot->set_session_id( tmp );
	cout << "gui: myrobot = ";
	myrobot->print_ident();
	state = STATE_GUI_WAIT;
      }
      else if ( ! strcmp( cmd,CMD_QUIT )) {
	state = STATE_QUIT;
      }
      else {
	// we don't care what the gui gets back from the server for now...
      }
    }
  } // end while
  
  // all done. pack up and go home.
  delete comm;
  return( 0 );

} // end of main()
