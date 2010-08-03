/**
 * commServer.cpp
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

#include "commServer.h"

#define TCP_PORT 6667



/**
 * commServer constructor
 *
 * creates server socket on port
 *
 */
commServer::commServer(char *ip, int port) {

  int optval = 1;
  struct sockaddr_in sin;
  
  // (1) create an endpoint for communication
  if (( sock = socket( PF_INET, SOCK_STREAM, 0 )) == -1) {
    perror( "commServer/socket" );
    exit( 1 );
  }

  // (1a) set socket options
  if ( setsockopt( sock,
		   SOL_SOCKET, 
		   SO_REUSEADDR, /* basically allows socket to bind */
		   (const char *)&optval, sizeof(optval)) == -1 ) {
    perror( "commServer/setsockopt" );
    exit( 1 );
  }
  memset( &sin, 0, sizeof( sin ));
  sin.sin_family = AF_INET;
  sin.sin_addr.s_addr = htonl(INADDR_ANY);
  sin.sin_port = htons(port);

  // (2) bind the socket
  if ( bind( sock,(struct sockaddr *)&sin,(socklen_t)sizeof(sin) ) == -1 ) {
    perror( "commServer/bind" );
    exit( 1 );
  }

  // (3) listen for connections
  listen( sock,2 );

  // (4) get and set name of host
  int hostlen = 1024;
  host = (char *)malloc( hostlen*sizeof( char ));
  gethostname( host,hostlen );

  // (5) get and set port number
  socklen_t slen = sizeof( sin );
  if ( getsockname( sock, (struct sockaddr *)&sin, &slen ) == -1 ) {
    perror( "commServer/getsockname" );
    exit( 1 );
  }

  port = ntohs( sin.sin_port );

} // end of commServer constructor



/**
 * commServer destructor
 *
 */
commServer::~commServer() {
  close( sock );
} // end of commServer destructor



/**
 * get_host()
 *
 * This function returns the value of the host.
 *
 */
const char *commServer::get_host() {
  return( host );
} // end of get_host()



/**
 * get_port()
 *
 * This function returns the value of the port number.
 *
 */
int commServer::get_port() {
  return( port );
} // end of get_port()



/**
 * get_sock()
 *
 * This function returns the value of the socket descriptor.
 *
 */
int commServer::get_sock() {
  return( sock );
} // end of get_sock()



/**
 * send_msg()
 *
 * sends the argument message over the argument socket
 * returns 0 if message is sent okay; returns -1 otherwise
 *
 */
int commServer::send_msg( int sock, unsigned char len, const char *p ) {
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
int commServer::read_msg( int sock, unsigned char len, char **p ) {
  int nread;
  char *buf;
  if (( nread = read( sock, &len, sizeof( len ))) < 0 ) {
    return( -1 );
  }
  if ( nread > 0 ) {
    buf = (char *)malloc( len + 1 );
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
