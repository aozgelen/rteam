/**
 * comm.cpp
 *
 * sklar/21-june-2010
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

#include "comm.h"




/**
 * send_msg()
 *
 * sends the argument message over the argument socket
 * returns 0 if message is sent okay; returns -1 otherwise
 *
 */
int send_msg( int sock, unsigned char len, char *p )
{
    int nwritten;

    if (( nwritten = write( sock, &len, sizeof( len ) )) == -1 )
    {
        return( -1 );
    }

    if (( nwritten = write( sock, p, strlen(p) )) == -1 )
    {
        return( -1 );
    }

    return( 0 );
} // end of send_msg()




/**
 * receive_msg()
 *
 * reads a message of up to argument "len" characters over the argument socket
 * sets the argument "p" to the message read; NULL if there is nothing to read
 * returns 0 if message read okay; -1 otherwise
 *
 */
int receive_msg( int sock, unsigned char len, char **p )
{
    int nread;
    char *buf;
    buf = (char *)malloc( nread + 1 );

    if (( nread = read( sock, buf, len )) < 0 )
    {
        return( -1 );
    }

    buf[nread] = '\0';

    *p = buf;
    return( 0 );
} // end of receive_msg()

int read_msg(int sockfd, char *vptr, size_t n)
{
    int nleft;
    int nread;
    char *ptr;

    ptr = vptr;
    nleft = n;

    while (nleft > 0)
    {
        if( (nread = read(sockfd, ptr, nleft)) < 0 )
        {
            cout << "Error reading socket nread is: " << nread << endl;
            perror("FUU\n");

            if (errno == EINTR)
                nread = 0;
            else
                return -1;
        }
        else if (nread == 0)
            break;

        nleft -= nread;
        ptr += nread;
    }

    return (n - nleft);
}

int write_msg( int sockfd, const char *vptr, size_t n)
{
    size_t nleft;
    ssize_t nwritten;
    const char *ptr;

    ptr = vptr;
    nleft = n;

    while(nleft > 0)
    {
        if ( (nwritten = write(sockfd, ptr, nleft)) <= 0)
        {
            if (nwritten < 0 && errno == EINTR)
                nwritten = 0;
            else
                return -1;
        }

        nleft -= nwritten;
        ptr += nwritten;

    }

    return (n);
}
