/** 
 * server.cpp
 *
 * server skeleton with threads
 *
 * sklar/21-june-2010
 *
 */

#include <iostream>
#include <iomanip>
#include <string>
#include <cstring>
#include <queue>
#include <list>
#include <sstream>
using namespace std;

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <errno.h>
#include <ctime>
#include <sys/select.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/wait.h>
#include <netinet/in.h>
#include <pthread.h>

#include "definitions.h"
#include "commServer.h"
#include "pose.h"
#include "robot.h"

#define MAX_THREADS 10
#define MAX_BUFFER  1024
 
typedef int (*function_ptr)(char *, robot *);

// some global variables...
commServer *comm = NULL;
pthread_mutex_t robots_mutex;
typedef robot* robot_p;
list<robot_p> robots;
function_ptr function_array[255] = {NULL};
pthread_mutex_t threads_mutex;
int num_threads = 0;



/**
 * find_robot()
 *
 * Searches the robots list for the given session_id.
 * If it is found in the list, it returns true, and iter is
 * a valid iterator to the robot; otherwise it returns false.
 */
bool find_robot( int session_id, list<robot_p>::iterator& iter ) {
  bool found = false;
  pthread_mutex_lock( &robots_mutex );
  for (list<robot_p>::iterator i = robots.begin();
       !found && i != robots.end(); ++i) {
    if ( ((*i)->get_session_id()) == session_id ) {
      found = true;
      iter = i;
    }
  }
  pthread_mutex_unlock( &robots_mutex );
  return found;
} // end of find_robot()


bool send_or_push( const char *msgbuf, robot *myrobot, int recipient_id ) {
  unsigned char len = strlen(msgbuf);
  string p(msgbuf);
  list<robot_p>::iterator bot;
  if (find_robot( recipient_id, bot )) {
      if(recipient_id != myrobot->get_session_id()){
        (*bot)->push_msg(p);
      }else{
       if(comm->send_msg( myrobot->get_sock(), len, msgbuf) == -1){
        cerr << "**error> failed to send message" << endl;
        return false;
       }
      }
  }
  return true;
}


int init_client( char *msgbuf, robot *myrobot) {
  unsigned char len;
  string command;
  string type;
  string name;
  int num_provides;
  string provides;

  char *ptr = NULL;

  if (( comm->read_msg( myrobot->get_sock(), len, &ptr ) == -1 ) || (ptr == NULL)) {
    cout << "**error> reading message in client_handler" << endl;
    return STATE_QUIT;
  }
  else {
    //strcpy( msgbuf,ptr );
  }

  string p(ptr);
  stringstream istream(p);
  cout << "INIT Got string: " << p << endl;

  istream >> command;
  if(command != "INIT"){
      free(ptr);
    return STATE_QUIT;
  }else{
      istream >> type >> name >> num_provides;
      if(!istream){
	return STATE_QUIT;
	  free(ptr);
      }
  }

  cout << "Command: " << command << endl;
  cout << "Type: " << type << endl;
  cout << "Name: " << name << endl;
  cout << "Num of Provides: " << num_provides << endl;
  cout << "Provides: " << provides << endl;

  myrobot->set_type_id(type.c_str());
  myrobot->set_name(name.c_str());

  for (int i = 0; i < num_provides; ++i) {
     istream >> provides;
	  myrobot->add_to_provides(provides);
  }

  free(ptr);

  return STATE_ACK;
}

int ack_client( char *msgbuf, robot *myrobot) {
  unsigned char len;

  long session_id = myrobot->get_session_id();
  string ack = CMD_ACK;
  stringstream convert;

  convert << session_id;

  ack += " ";
  ack += convert.str();
  len = strlen(ack.c_str());
  strncpy(msgbuf, ack.c_str(), len);

  if (( comm->send_msg( myrobot->get_sock(), len, msgbuf ) == -1 )) {
    cout << "**error> reading message in client_handler" << endl;
    return STATE_QUIT;
  }

  return STATE_IDLE;
}


int client_idle( char *msgbuf, robot *myrobot) {
  int sockfd = myrobot->get_sock();
  int ret = 0;
  timeval timeout;
  fd_set rset;


  long session_id = myrobot->get_session_id();

  for(;;){
      if ( !myrobot->msg_empty()){
	return STATE_PROC_CMD;
      }
      else {
        FD_ZERO(&rset);
        FD_SET(sockfd, &rset);
        timeout.tv_sec = 0;
        timeout.tv_usec = 0;

	if( (ret = select(sockfd + 1, &rset, NULL, NULL, &timeout)) == -1){
	  perror("select: ");
	  return STATE_ERROR;
	}else if(ret > 0){
	  if(FD_ISSET(sockfd, &rset)){
	    return STATE_PROC_CMD;
	  }

	}
      }
  }

  cout << "ERROR!" << endl;
  return STATE_ERROR;
}

int proc_cmd( char *msgbuf, robot *myrobot) {
  unsigned char len;
  string command;
  string que_msg = myrobot->pop_msg();
  char *ptr = NULL;

  if(que_msg != ""){
    cout << "Got from Que" << endl;
    strcpy( msgbuf, que_msg.c_str() );
  }
  else if (( comm->read_msg( myrobot->get_sock(), len, &ptr ) == -1 ) || (ptr == NULL)) {
    cout << "**error> reading message in client_handler" << endl;
    return STATE_QUIT;
  }
  else{
    strcpy( msgbuf,ptr );
  }

  /*if(ptr){
      free(ptr);
  }*/

  string p(msgbuf);
  stringstream istream(p);
  cout << "PROC Got string: " << p << endl;

  istream >> command;
  
  if(command == CMD_PING){
    cout << "Calling pong state";
    return STATE_PONG;

  }else if(command == CMD_PONG){
    return STATE_PING;

  }else if(command == CMD_QUIT){
    return STATE_QUIT;

  }else if(command == CMD_IDENT){
    return STATE_IDENT;

  }else if(command == CMD_LOCK){
    return STATE_LOCK;

  }else if(command == CMD_UNLOCK){
    return STATE_UNLOCK;

  }else if(command == CMD_MOVE){
    return STATE_MOVE;

  }else if(command == CMD_ASK_POSE){
    return STATE_ASK_POSE;

  }else if(command == CMD_GET_POSE){
    return STATE_GET_POSE;

  }else if(command == CMD_ASK_PLAYER){
    return STATE_ASK_PLAYER;

  }else if(command == CMD_GET_PLAYER){
    return STATE_GET_PLAYER;

  }else{
    return STATE_IDLE;
  }

  // should not reach here
  cout << "You were wrong!" << endl;
  return STATE_ERROR;
}


int client_ping( char *msgbuf, robot *myrobot) {

  return STATE_PONG;

}

int client_pong( char *msgbuf, robot *myrobot) {
  cout << "In PONG" << endl;

  unsigned char len;
  string pong = CMD_PONG;

  len = strlen(pong.c_str());
  strncpy(msgbuf, pong.c_str(), len);

  if (( comm->send_msg( myrobot->get_sock(), len, msgbuf ) == -1 )) {
    cout << "**error> reading message in client_handler" << endl;
    return STATE_QUIT;
  }

  return STATE_IDLE;

}

int client_lock( char *msgbuf, robot *myrobot) {
  string p(msgbuf);
  stringstream istream(p);
  string command;
  long session_id;
  unsigned char len = strlen(msgbuf);
  cout << "Read lock command: " << p << endl;
  istream >> command >> session_id;

  if (send_or_push(msgbuf, myrobot, session_id)) {
      return STATE_IDLE;
  } else {
      return STATE_QUIT;
  }
}

int client_unlock( char *msgbuf, robot *myrobot) {
  string p(msgbuf);
  stringstream istream(p);
  string command;
  long session_id;
  unsigned char len = strlen(msgbuf);
  cout << "Read lock command: " << p << endl;
  istream >> command >> session_id;

  if (send_or_push(msgbuf, myrobot, session_id)) {
      return STATE_IDLE;
  } else {
      return STATE_QUIT;
  }
}

//#define CMD_IDENT    "IDENT"   // IDENT <num-robots> [ <robot_id> <name> 
// <type> <num-provides> <provides>
int client_ident( char *msgbuf, robot *myrobot) {
  string msg = CMD_IDENT;
  stringstream convert;
  unsigned char len;

  cout << "In IDENT" << endl;

  pthread_mutex_lock( &robots_mutex );
  msg += " ";
  convert << robots.size() - 1;
  msg += convert.str();
  for (list<robot_p>::iterator i = robots.begin();
       i != robots.end(); ++i) {
    if((*i)->get_session_id() != myrobot->get_session_id()){
        msg += " ";
        convert.str("");
	    convert << (*i)->get_session_id();
	    msg += convert.str();
	    msg += " ";
	    msg += (*i)->get_name();
	    msg += " ";
	    msg += (*i)->get_type_id();
	    msg += " ";
        convert.str("");
	    convert << (*i)->get_num_of_provides();
	    msg += convert.str();
	    vector<string> provides = (*i)->get_provides();
	    for (int i = 0; i < provides.size(); ++i) {
	    	msg += " " + provides[i];
	    }
    }
  }
  pthread_mutex_unlock( &robots_mutex );

  len = strlen(msg.c_str());
  cout << "Size is: " << (int) len << endl;
  cout << "Msg is: " << msg << endl;
  strncpy(msgbuf, msg.c_str(), len);


  if (( comm->send_msg( myrobot->get_sock(), len, msgbuf ) == -1 )) {
    cout << "**error> reading message in client_handler" << endl;
    return STATE_QUIT;
  }


  return STATE_IDLE;
}

int client_move( char *msgbuf, robot *myrobot) {
  int bot_location;
  string command;
  long session_id = -1;
  string p(msgbuf);
  stringstream istream(p);
  cout << "Read move command: " << p << endl;

  istream >> command >> session_id;

  if(command != CMD_MOVE){
    cout << "This aint move!!!" << endl;
    return STATE_IDLE;
  }
  if (send_or_push(msgbuf, myrobot, session_id)) {
      return STATE_IDLE;
  } else {
      return STATE_QUIT;
  }
}

int client_error( char *msgbuf, robot *myrobot) {

  cout << "Got error, quitting" << endl;

  return STATE_QUIT;
}

int client_send_pose( char *msgbuf, robot *myrobot) {
  string p(msgbuf);
  stringstream istream(p);
  string command;
  long session_id;
  ostringstream oss;
  pose *temp;

  cout << "Read global ask pose command: " << p << endl;

  istream >> command >> session_id;

  oss << "POSE";
  oss << " ";

  if(session_id < 0){
    oss << robots.size();

    for (list<robot_p>::iterator i = robots.begin();
         i != robots.end(); ++i) {
      oss << " ";
      session_id = (*i)->get_session_id();
      oss << session_id;
      oss << " ";
      temp = (*i)->get_pose();
      oss << temp->get_x();
      oss << " ";
      oss << temp->get_y();
      oss << " ";
      oss << temp->get_theta();
      oss << " ";
      oss << temp->get_confidence();
    }

  }else{
    // just one bot
      list<robot_p>::iterator i;
      if (find_robot(session_id, i)) {
	  oss << "1";
	  oss << " ";
	  session_id = (*i)->get_session_id();
	  oss << session_id;
	  oss << " ";
	  temp = (*i)->get_pose();
	  oss << temp->get_x();
	  oss << " ";
	  oss << temp->get_y();
	  oss << " ";
	  oss << temp->get_theta();
	  oss << " ";
	  oss << temp->get_confidence();
      } 
  }


  if (send_or_push(oss.str().c_str(), myrobot, session_id)) {
      return STATE_IDLE;
  } else {
      return STATE_QUIT;
  }
}

int client_ask_pose( char *msgbuf, robot *myrobot) {
  string p(msgbuf);
  stringstream istream(p);
  string command;
  long session_id;
  unsigned char len = strlen(msgbuf);
  cout << "Read ask pose command: " << p << endl;
  istream >> command >> session_id;

  if(myrobot->get_type_id() != "gui"){
      if (send_or_push(msgbuf, myrobot, session_id)) {
	  return STATE_IDLE;
      } else {
	  return STATE_QUIT;
      }
  }else{

    return client_send_pose(msgbuf, myrobot);

  }

  
}

// POSE 1278614336 0 0 2.19397 0
int client_get_pose( char *msgbuf, robot *myrobot) {
  string p(msgbuf);
  stringstream istream(p);
  string command;
  long session_id;
  double x, y, theta, con;

  istream >> command >> session_id >> x >> y >> theta >> con;

  list<robot_p>::iterator bot;
  if (find_robot(session_id, bot)) {
    if(session_id != myrobot->get_session_id()){
      (*bot)->push_msg(p);
    }else{
      myrobot->set_pose(x, y, theta);
    }
  }

  return STATE_IDLE;
}

int client_ask_player( char *msgbuf, robot *myrobot) {
  string p(msgbuf);
  stringstream istream(p);
  string command;
  long session_id;
  unsigned char len = strlen(msgbuf);
  cout << "Read ask player command: " << p << endl;
  istream >> command >> session_id;
  ostringstream oss;

  string cmd = command + " ";

  oss << myrobot->get_session_id();
  cmd += oss.str();

  strncpy(msgbuf, cmd.c_str(), MAX_BUFFER);

  if(myrobot->get_type_id() != "gui"){
      if (send_or_push(msgbuf, myrobot, session_id)) {
	  return STATE_IDLE;
      } else {
	  return STATE_QUIT;
      }
  }

  return STATE_IDLE;

}

// POSE 1278614336 0 0 2.19397 0
int client_get_player( char *msgbuf, robot *myrobot) {
  string p(msgbuf);
  stringstream istream(p);
  string command, ip;
  long session_id, requestor_id;
  int port;

  cout << "Sending off player info to GUI" << endl;

  istream >> command >> requestor_id >> session_id >> ip >> port;

  list<robot_p>::iterator bot;

  if (find_robot(requestor_id, bot)) {
    if(session_id != myrobot->get_session_id()){
      (*bot)->push_msg(p);
    }else{
      if (send_or_push(msgbuf, myrobot, requestor_id)) {
	  return STATE_IDLE;
      } else {
	  return STATE_QUIT;
      }
    }

  }

  return STATE_IDLE;
}


void *posebeat( void *arg ) {
  string p;
  string command = "ASKPOSE";
  long session_id;
  ostringstream oss;

  for (;;) {
    cout << "Asking for new poses ... " << endl;
    pthread_mutex_lock( &robots_mutex );

    for (list<robot_p>::iterator i = robots.begin();
         i != robots.end(); ++i) {
      if((*i)->get_type_id() != "gui"){
	  session_id = (*i)->get_session_id();
	  oss << session_id;
	  p += command;
	  p += " ";
	  p += oss.str();
	  (*i)->push_msg(p);
	  p = "";
	  oss.str("");
	}
    }

    pthread_mutex_unlock( &robots_mutex );

    sleep(10);
  }
}

/**
 * heartbeat()
 *
 * This is a little heartbeat function that is spawned by a thread in the server.
 * It provides a count to show that the server is still alive.
 *
 */
void *heartbeat( void *arg ) {
  time_t rawtime;
  struct tm * tm;

  while ( 1 ) {
    time ( &rawtime );
    tm = localtime ( &rawtime );
    cout << "["
         << setw(2) << setfill('0') << tm->tm_hour << ":"
         << setw(2) << setfill('0') << tm->tm_min << ":"
         << setw(2) << setfill('0') << tm->tm_sec << "]"
         << " i'm happy; clients connected: " << num_threads << endl;
    sleep( 2 );
  }
} // end of heartbeat()


/**
 * client_handler
 *
 * This function is the main function for each server client
 * thread. When a new connection is accepted over the server's socket,
 * a thread is spawned to handle that client's activities. We call
 * this a "server client" and this function is designed to handle its
 * execution.
 *
 * The primary activities are sending and receiving commands to/from
 * the server over the socket and responding to them accordingly.
 *
 * The server client threads follows a simple state machine:
 *
 *                                                    +--------------------send-pong-+
 *                                                    |                              |
 *                                                    v    /--read-ping---> [PONG]---+
 * --start-> [INIT] --read-init-> [ACK] --send-ack-> [PING]                  ^  ^
 *                                                    |  |                   |  |
 *                                                    |  +-read-move(not me)-+  +-----------------+
 *                                                    |                                           |
 *                                                    |                            /--read-moving-+
 *                                                    +----read-move(me)-> [MOVING]
 *                                                                          ^      \----------+
 *                                                                          |                 |
 *                                                                          +-send-moving(me)-+
 *
 */
void *client_handler( void *arg ) {

  robot *myrobot = (robot *)arg;
  int state;
  char tmp[25], *p, *cmd;
  char *msgbuf = (char *)malloc( MAX_BUFFER*sizeof(char) );
  char *pmsg   = (char *)malloc( MAX_BUFFER*sizeof(char) );
  char *pargs  = (char *)malloc( MAX_BUFFER*sizeof(char) );
  string msg;
  long robot_id;
  int i, dir, dur;
  int msg_num = 0;

  int command = -1;

  // save this thread_id
  myrobot->set_thread_id( pthread_self() );

  state = STATE_INIT;

  bool fDone = false;
  while(!fDone){
    switch( state ) {
	case STATE_INIT:
	    command = STATE_INIT;
	    break;
	case STATE_QUIT:
        fDone = true;
        command = -1;
	    break;
	case STATE_ACK:
	    command = STATE_ACK;
	    break;
	case STATE_IDLE:
	    command = STATE_IDLE;
	    break;
	case STATE_PROC_CMD:
	    command = STATE_PROC_CMD;
	    break;
	case STATE_PING:
	    command = STATE_PING;
	    break;
	case STATE_PONG:
	    command = STATE_PONG;
	    break;
	case STATE_IDENT:
	    command = STATE_IDENT;
	    break;
	case STATE_LOCK:
	    command = STATE_LOCK;
	    break;
	case STATE_UNLOCK:
	    command = STATE_UNLOCK;
	    break;
	case STATE_ASK_POSE:
	    command = STATE_ASK_POSE;
	    break;
	case STATE_GET_POSE:
	    command = STATE_GET_POSE;
	    break;
	case STATE_ASK_PLAYER:
	    command = STATE_ASK_PLAYER;
	    break;
	case STATE_GET_PLAYER:
	    command = STATE_GET_PLAYER;
	    break;
	case STATE_MOVE:
	    command = STATE_MOVE;
	    break;
	default:
	    cout << "State was: " << state << endl;
	    command = STATE_ERROR;
	    break;
    }

    if( command >= 0) {
      state = (*function_array[command])(msgbuf, myrobot);
    }else{
      //do something
    }

  }


  free(msgbuf);
  free(pmsg);
  free(pargs);
  close( myrobot->get_sock() );
  // get rid of the robot
  list<robot_p>::iterator bot;
  if (find_robot(myrobot->get_session_id(), bot)) {
      pthread_mutex_lock( &robots_mutex );
      robots.erase(bot);
      pthread_mutex_unlock( &robots_mutex );
  }
  delete myrobot;
  // adjust the thread count
  pthread_mutex_lock( &threads_mutex );
  num_threads--;
  pthread_mutex_unlock( &threads_mutex );
  pthread_exit( NULL );
} // end of client_handler()



/**
 * main()
 *
 */
int main( int argc, char *argv[] ) {

  int ssock, csock;
  pid_t pid;
  pthread_t thread_id0, thread_id1, thread_id[MAX_THREADS]; // array of thread id's
  struct sockaddr_in sin;
  socklen_t slen = sizeof( sin );

  function_array[STATE_INIT] = &init_client;
  function_array[STATE_ACK] = &ack_client;
  function_array[STATE_IDLE] = &client_idle;
  function_array[STATE_PONG] = &client_pong;
  function_array[STATE_PING] = &client_ping;
  function_array[STATE_PROC_CMD] = &proc_cmd;
  function_array[STATE_IDENT] = &client_ident;
  function_array[STATE_MOVE] = &client_move;
  function_array[STATE_ERROR] = &client_error;
  function_array[STATE_LOCK] = &client_lock;
  function_array[STATE_UNLOCK] = &client_unlock;
  function_array[STATE_ASK_POSE] = &client_ask_pose;
  function_array[STATE_GET_POSE] = &client_get_pose;
  function_array[STATE_ASK_PLAYER] = &client_ask_player;
  function_array[STATE_GET_PLAYER] = &client_get_player;

  // initialize list of robots
  robots.clear();

  // create server socket
  comm = new commServer();
  printf( "clients connect using: host=[%s] port=[%d]\n", comm->get_host(), comm->get_port() );
  if ( pthread_create( &thread_id0, NULL, &heartbeat, NULL )) {
    perror( "**error> server/basic thread" );
    exit( 1 );
  }
  if ( pthread_create( &thread_id1, NULL, &posebeat, NULL )) {
    perror( "**error> server/basic thread" );
    exit( 1 );
  }
  ssock = comm->get_sock();

  // initialize mutex for shared robots array
  pthread_mutex_init( &robots_mutex,NULL );
  pthread_mutex_init( &threads_mutex,NULL );

  while ( num_threads < MAX_THREADS ) {
    // accept returns a socket descriptor for the accepted socket
    if (( csock = accept( ssock,(struct sockaddr *)&sin, &slen )) == -1 ) {
      perror( "**error> server/accept" );
      exit( 1 );
    }
    fprintf( stdout,"server: socket created/listen/accepted\n" );
    fflush( stdout );

    // create an entry in the robots list for this client
    robot_p rp = new robot();
    if (rp == 0) {
        cerr << "**error> failed to create new robot" << endl;
    } else {
        rp->set_session_id(time(0));
        rp->set_sock(csock);
        cout << "created new robot, id=" << rp->get_session_id() << endl;
        // create a new thread for this client
        pthread_mutex_lock( &threads_mutex );
        if ( pthread_create( &thread_id[num_threads], 
	    		 NULL, 
	    		 &client_handler, 
	    		 (void *)rp )) {
          printf( "**error> could not create thread for client_handler\n" );
          delete rp;
        }
        else {
          if (pthread_detach(thread_id[num_threads]) != 0) {
             cerr << "**error> failed to detach thread" << endl;
          }
          num_threads++;
          pthread_mutex_lock( &robots_mutex );
          robots.push_back(rp);
          pthread_mutex_unlock( &robots_mutex );
        }
        pthread_mutex_unlock( &threads_mutex );
    }
  } // end while

  // all done. pack up and go home.
  delete comm;
  return( 0 );

} // end of main()
