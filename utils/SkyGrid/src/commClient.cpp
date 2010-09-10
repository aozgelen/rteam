/**
 * commClient.cpp
 *
 * original: sklar/21-june-2010
 * modified: sklar/28-june-2010
 *
 */

#include <iostream>
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

#include "commClient.h"



/**
 * commClient constructor
 *
 * creates client socket on port...
 *
 */
commClient::commClient( const char *host, int port ) {

  int optval = 1;
  struct sockaddr_in sin;
  
  // (1) create an endpoint for communication
  if (( sock = socket( PF_INET, SOCK_STREAM, 0 )) == -1) {
    perror( "commClient/socket" );
    exit( 1 );
  }

  // (1a) set socket options
  if ( setsockopt( sock,
		   SOL_SOCKET, 
		   SO_REUSEADDR, /* basically allows socket to bind */
		   &optval, sizeof(optval)) == -1 ) {
    perror( "commClient/setsockopt" );
    exit( 1 );
  }
  memset( &sin, 0, sizeof( sin ));
  sin.sin_family = AF_INET;
  sin.sin_addr.s_addr = htonl( INADDR_LOOPBACK );
  sin.sin_port = port;

  // (2) make a connection to the server socket
  if ( connect( sock,(struct sockaddr *)&sin,(socklen_t)sizeof(sin) ) == -1 ) {
    perror( "commClient/connect" );
  }

} // end of commClient constructor



/**
 * commClient destructor
 *
 */
commClient::~commClient() {
  close( sock );
} // end of commClient destructor



/**
 * send_msg()
 *
 * sends the argument message over the argument socket
 * returns 0 if message is sent okay; returns -1 otherwise
 *
 */
int commClient::send_msg( unsigned char len, char *p ) {
  int nwritten;
  if (( nwritten = write( sock, &len, sizeof( len ) )) == -1 ) {
    return( -1 );
  }
  if (( nwritten = write( sock, p, strlen(p) )) == -1 ) {
    return( -1 );
  }
  return( 0 );
} // end of send_msg()



/**
 * read_msg()
 *
 * reads a message of up to argument "len" characters over the argument socket
 * sets the argument "p" to the message read; NULL if there is nothing to read
 * returns 0 if message read okay; -1 otherwise
 *
 */
int commClient::read_msg( unsigned char len, char **p ) {
  int nread;
  char *buf;
  if (( nread = read( sock, &len, sizeof( len ))) < 0 ) {
    return( -1 );
  }
  if ( nread > 0 ) {
    buf = (char *)malloc( nread + 1 );
    if (( nread = read( sock, buf, len )) < 0 ) {
      return( -1 );
    }
    buf[nread] = '\0';
  }
  else {
    buf = NULL;
  }
  *p = buf;
  return( 0 );
} // end of read_msg()
