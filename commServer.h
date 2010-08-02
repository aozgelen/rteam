/**
 * commServer.h
 *
 */

#ifndef _COMM_SERVER_H
#define _COMM_SERVER_H

#define TERM '\n'

class commServer {

 private:
  char *host;
  int port;
  int sock;

 public:
  commServer(char *, int);
  ~commServer();

  const char *get_host();
  int get_port();
  int get_sock();

  int send_msg( int sock, unsigned char len, const char *p );
  int read_msg( int sock, unsigned char len, char **p );

}; // end of commServer class

#endif
