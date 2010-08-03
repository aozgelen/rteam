/**
 * comm.h
 *
 */

#define TERM '\n'

int send_msg( int sock, unsigned char len, char *p );
int receive_msg( int sock, unsigned char len, char **p );
int write_msg( int sockfd, const char *vptr, size_t n);
int read_msg( int sockfd, char *vptr, size_t n);
