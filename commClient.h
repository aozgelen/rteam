/**
 * commClient.h
 *
 */

#ifndef _COMM_CLIENT_H
#define _COMM_CLIENT_H

#define TERM '\n'

class commClient {

 private:
  char *host;
  int port;
  int sock;

 public:
  commClient( const char *host, int port );
  ~commClient();

  int send_msg( unsigned char len, char *p );
  int read_msg( unsigned char len, char **p );

}; // end of commClient class

#endif
