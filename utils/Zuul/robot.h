/**
 * robot.h
 *
 * original by gonzalez/28-june-2010
 * modified by sklar/28-june-2010
 *
 */

#ifndef _ROBOT_H
#define _ROBOT_H

#include <iostream>
#include <string>
#include <cstring>
#include <queue>
#include <vector>
using namespace std;

#include <pthread.h>
#include "pose.h"

#define FIELD_SIZE 64

struct ident {
  long session_id;
  int owner_id;
  string owner;
  string type_id;
  string type;
  vector<string> provides;
  long last_msg;
  int disconnect_count;
  string name;
  string player_ip;
  int player_port;
};

class robot
{
public:
    robot();

    void set_session_id( long );
    long get_session_id();

    void set_last_msg_seen( long );
    long get_last_msg_seen();

    void set_disconnect_count( int );
    void inc_disconnect_count();
    int get_disconnect_count();

    bool msg_empty();

    void set_owner_id(int);
    int get_owner_id();
    void set_owner( string );
    const string get_owner();

    void set_type_id( string );
    const string get_type_id();
    void set_type( string );
    const string get_type();

    void set_pose( double, double, double, double );
    pose *get_pose();

    void set_name( string );
    const string get_name();

    void set_host( string );
    const string get_host();

    void set_port( int );
    int get_port();

    void set_player_ip(string);
    string get_player_ip();
    void set_player_port(int);
    int get_player_port();

    int get_num_of_provides() const { return tag.provides.size(); }

    void add_to_provides( string );
    const vector<string> get_provides() const { return tag.provides; }

    void set_sock( int );
    int get_sock();

    void set_thread_id( pthread_t );
    pthread_t get_thread_id();

    ~robot();

    void push_msg( string msg );
    const string pop_msg();


private:
    struct ident tag;
    pose *mypose;
    string host;
    int port;
    int sock;
    pthread_t thread_id;
    pthread_mutex_t mutexq;
    queue<string> msgq;
};

#endif
