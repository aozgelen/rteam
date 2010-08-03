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
#include <fstream>
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
#include <ncurses.h>
#include <regex.h>

#include "definitions.h"
#include "commServer.h"
#include "pose.h"
#include "robot.h"

#define VERSION "1.0"
#define MAX_THREADS 10
#define MAX_BUFFER  1024

// SIMULATE spawns a phantom user for testing purposes
#define SIMULATE
 
const char server_title[] = "SkyGrid " VERSION;

typedef int (*function_ptr)(char *, robot *);

struct thread_args{
  bool interactive;
  bool log_enabled;
  char logfile[32];
  char ip[16];
  int port;
};

// some global variables...
commServer *comm = NULL;
pthread_mutex_t robots_mutex;
typedef robot* robot_p;
list<robot_p> robots;
function_ptr function_array[255] = {NULL};
pthread_mutex_t threads_mutex;
int num_threads = 0;
pthread_mutex_t paint_mutex;
queue<string> painter_queue;

// NCURSES global window declarations
WINDOW *title_bar;
WINDOW *output;
WINDOW *status_bar;
WINDOW *filter_menu;


// display_usage: Displays the program usage
// Parameters:
// name - name of running process
void display_usage(char *name){
  cerr << name << " [-i] [-l logfile] [-b ip address] [-p port]" << endl;
  exit(1);
}

// construct_msg: Constructs strings into standard format
// Parameters:
//
string construct_msg(string type, string message, long robot_id){
  time_t rawtime;
  struct tm *tm;
  time ( &rawtime );
  tm = localtime ( &rawtime );
  ostringstream buffer;
  char time_buffer[] = "00:00:00";
  string tmp;

  snprintf(time_buffer, sizeof(time_buffer),
          "%02d:%02d:%02d", tm->tm_hour, tm->tm_min, tm->tm_sec);

  buffer << time_buffer << " ";

  buffer << "[ " << type << " ]" << " ";
  buffer << message << " ";

  if(type == "SENT"){
    buffer << "[ TO ]" << " ";
  }else if(type == "RECIEVED"){
    buffer << "[ BY ]" << " ";
  }else if(type == "PUSHED"){
    buffer << "[ ONTO ]" << " ";
  }else if(type == "POPPED"){
    buffer << "[ OFF ]" << " ";
  }else{
    buffer << "[ ERROR ]" << " ";
  }

  // Find robot name and get

  buffer << "ELGUI" << " ";
  buffer << "192924812414";

  return buffer.str();

}

void push_paint(string type, string message, long robot_id){
  string msg = construct_msg(type, message, robot_id);

  pthread_mutex_lock( &paint_mutex );

  if(!msg.empty()){
    painter_queue.push( msg );
  }else{
    //cout << "Got empty string" << endl;
  }

  pthread_mutex_unlock( &paint_mutex );
  
}

const string pop_paint(){
  string msg;                                                                   
  pthread_mutex_lock( &paint_mutex );

  if ( painter_queue.empty() ) {
    msg.clear();
  }
  else {
    msg = painter_queue.front();
    painter_queue.pop();
  }

  pthread_mutex_unlock( &paint_mutex );

  return( msg );
}

bool paint_empty() {                                                       
  bool empty = false;

  pthread_mutex_lock( &paint_mutex );

  if(painter_queue.empty()){
    empty = true;
  }

  pthread_mutex_unlock( &paint_mutex );

  return empty;
}


void print_error(WINDOW *win, string err_msg){
    time_t rawtime;
    struct tm * tm;

    time ( &rawtime );
    tm = localtime ( &rawtime );
    
    wattrset(win, A_DIM | COLOR_PAIR(8));
    wprintw(win, " %02d:%02d:%02d ",
	  tm->tm_hour, tm->tm_min, tm->tm_sec);
    wattrset(win, COLOR_PAIR(3) | A_BOLD);
    wprintw(win, "[ ERROR ]");

    wattrset(win, COLOR_PAIR(9) | A_BOLD | A_DIM);
    wprintw(win, " %s\n", err_msg.c_str());

    // call general msg colorer
}

void color_msg(WINDOW *win, string message){

  char buffer[255];
  char *tmp;
  size_t found;

  time_t rawtime;
  struct tm *tm;
  time ( &rawtime );
  tm = localtime ( &rawtime );

  strncpy(buffer, message.c_str(), 255);

  // Print time
  tmp = buffer;
  found = message.find(" ");
  buffer[found] = '\0';

  wattrset(win, A_DIM | COLOR_PAIR(8));
  wprintw(win, "%s ", tmp);

  // Print type
  ++found;
  tmp = &buffer[found];
  found = message.find("]", found);
  ++found;
  buffer[found] = '\0';

  wattrset(win, A_BOLD|COLOR_PAIR(5));
  wprintw(win, "%s ", tmp);

  // Print command
  ++found;
  tmp = &buffer[found];
  found = message.find(" ", found);
  buffer[found] = '\0';

  wattrset(win, A_BOLD|COLOR_PAIR(4));
  wprintw(win, "%s ", tmp);

  // Print rest of command
  ++found;
  tmp = &buffer[found];
  found = message.find("[", found);
  --found;
  buffer[found] = '\0';

  wattrset(win, A_BOLD | COLOR_PAIR(8));
  wprintw(win, "%s ", tmp);
  
  // Print action
  ++found;
  tmp = &buffer[found];
  found = message.find("]", found);
  ++found;
  buffer[found] = '\0';

  wattrset(win, A_BOLD|COLOR_PAIR(5));
  wprintw(win, "%s ", tmp);

  // Print username
  ++found;
  tmp = &buffer[found];
  found = message.find(" ", found);
  buffer[found] = '\0';

  wattrset(win, A_BOLD|COLOR_PAIR(10));
  wprintw(win, "%s ", tmp);

  // Print ID
  ++found;
  tmp = &buffer[found];
  wattrset(win, A_BOLD|COLOR_PAIR(2));
  wprintw(win, "%s ", tmp);

  wprintw(win, "\n\n");

}



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


bool send_or_push( char *msgbuf, robot *myrobot, long recipient_id ) {
  unsigned char len = strlen(msgbuf);
  string p(msgbuf);
  list<robot_p>::iterator bot;

  if (find_robot( recipient_id, bot )) {
      if(recipient_id != myrobot->get_session_id()){
        (*bot)->push_msg(p);
	push_paint("PUSHED", p, recipient_id);
      }else{
       if(comm->send_msg( myrobot->get_sock(), len, msgbuf) == -1){
        //cerr << "**error> failed to send message" << endl;
        push_paint("ERROR", "failed to send message", myrobot->get_session_id());
        return false;
       }else{
	push_paint("SENT", p, recipient_id);
       }
      }
  }else{
    // Robot not found
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
    //cout << "**error> reading message in client_handler" << endl;
    push_paint("ERROR", "reading message in client_handler",
	    myrobot->get_session_id());
    return STATE_QUIT;
  }
  else {
    //strcpy( msgbuf,ptr );
  }

  string p(ptr);
  stringstream istream(p);
  //cout << "INIT Got string: " << p << endl;

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

  //cout << "Command: " << command << endl;
  //cout << "Type: " << type << endl;
  //cout << "Name: " << name << endl;
  //cout << "Num of Provides: " << num_provides << endl;
  //cout << "Provides: " << provides << endl;

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
    //cout << "**error> reading message in client_handler" << endl;
    push_paint("ERROR", "reading message in client_handler",
	    myrobot->get_session_id());
    return STATE_QUIT;
  }

  return STATE_IDLE;
}


int client_idle( char *msgbuf, robot *myrobot) {
  int sockfd = myrobot->get_sock();
  int ret = 0;
  timeval timeout;
  fd_set rset;

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

  //cout << "ERROR!" << endl;
  push_paint("ERROR", "client_idle: should have not reached here",
	  myrobot->get_session_id());

  return STATE_ERROR;
}

int proc_cmd( char *msgbuf, robot *myrobot) {
  unsigned char len;
  string command;
  string que_msg = myrobot->pop_msg();
  char *ptr = NULL;

  if(que_msg != ""){
    //cout << "Got from Que" << endl;
    push_paint("POPPED", que_msg.c_str(), myrobot->get_session_id());
    strcpy( msgbuf, que_msg.c_str() );
  }
  else if (( comm->read_msg( myrobot->get_sock(), len, &ptr ) == -1 ) || (ptr == NULL)) {
    //cout << "**error> reading message in client_handler" << endl;
    push_paint("ERROR", "reading message in client_handler",
	    myrobot->get_session_id());
    return STATE_QUIT;
  }
  else{
    push_paint("RECIEVED", ptr, myrobot->get_session_id());
    strcpy( msgbuf,ptr );
  }

  /*if(ptr){
      free(ptr);
  }*/

  string p(msgbuf);
  stringstream istream(p);
  //cout << "PROC Got string: " << p << endl;

  istream >> command;
  
  if(command == CMD_PING){
    //cout << "Calling pong state";
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
  //cout << "Should not of reached this!" << endl;
  push_paint("ERROR", "proc_cmd: should have not reached here",
	  myrobot->get_session_id());
  return STATE_ERROR;
}


int client_ping( char *msgbuf, robot *myrobot) {

  return STATE_PONG;

}

int client_pong( char *msgbuf, robot *myrobot) {
  //cout << "In PONG" << endl;

  unsigned char len;
  string pong = CMD_PONG;

  len = strlen(pong.c_str());
  strncpy(msgbuf, pong.c_str(), len);

  if (( comm->send_msg( myrobot->get_sock(), len, msgbuf ) == -1 )) {
    //cout << "**error> reading message in client_handler" << endl;
    push_paint("ERROR", "reading message in client_handler",
	    myrobot->get_session_id());
    return STATE_QUIT;
  }

  return STATE_IDLE;

}

int client_lock( char *msgbuf, robot *myrobot) {
  string p(msgbuf);
  stringstream istream(p);
  string command;
  long session_id;

  //cout << "Read lock command: " << p << endl;
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

  //cout << "In IDENT" << endl;

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
	    for (unsigned int i = 0; i < provides.size(); ++i) {
	    	msg += " " + provides[i];
	    }
    }
  }
  pthread_mutex_unlock( &robots_mutex );

  len = strlen(msg.c_str());
  //cout << "Size is: " << (int) len << endl;
  //cout << "Msg is: " << msg << endl;
  strncpy(msgbuf, msg.c_str(), len);


  if (( comm->send_msg( myrobot->get_sock(), len, msgbuf ) == -1 )) {
    //cout << "**error> reading message in client_handler" << endl;
    push_paint("ERROR", "reading message in client_handler",
	    myrobot->get_session_id());
    return STATE_QUIT;
  }


  return STATE_IDLE;
}

int client_move( char *msgbuf, robot *myrobot) {
  string command;
  long session_id = -1;
  string p(msgbuf);
  stringstream istream(p);

  istream >> command >> session_id;

  if(command != CMD_MOVE){
    //cout << "This aint move!!!" << endl;
    return STATE_IDLE;
  }
  if (send_or_push(msgbuf, myrobot, session_id)) {
      return STATE_IDLE;
  } else {
      return STATE_QUIT;
  }
}

int client_error( char *msgbuf, robot *myrobot) {

  //cout << "Got error, quitting" << endl;
    push_paint("ERROR", "got error quitting ...",
	    myrobot->get_session_id());

  return STATE_QUIT;
}

int client_send_pose( char *msgbuf, robot *myrobot) {
  string p(msgbuf);
  stringstream istream(p);
  string command;
  long session_id;
  ostringstream oss;
  pose *temp = 0;
  unsigned char len = 0;

  //cout << "Read global ask pose command: " << p << endl;

  istream >> command >> session_id;

  oss << "POSE";
  oss << " ";

  list<robot_p>::iterator i;
  if(session_id < 0) {
    ostringstream posestream;
    int num_poses = 0;
    for (i = robots.begin(); i != robots.end(); ++i) {
       if ((*i)->get_type_id() != "gui") {
          num_poses++;
          posestream << " ";
          session_id = (*i)->get_session_id();
          posestream << session_id;
          posestream << " ";
          if ((temp = (*i)->get_pose())) {
              posestream << temp->get_x() << " "
                         << temp->get_y() << " "
                         << temp->get_theta() << " "
                         << temp->get_confidence();
          } else {
              posestream << " 0 0 0 0";
          }
       }
    }
    oss << num_poses << posestream.str();
  } else if (find_robot(session_id, i) &&
            ((*i)->get_type_id() != "gui")) {
      oss << "1";
      oss << " ";
      session_id = (*i)->get_session_id();
      oss << session_id;
      oss << " ";
      if ((temp = (*i)->get_pose())) {
          oss << temp->get_x() << " "
              << temp->get_y() << " "
              << temp->get_theta() << " "
              << temp->get_confidence();
      } else {
          oss << " 0 0 0 0";
      }
  } else {
      oss << "0";
  }

  //cerr << "**sending> " << oss.str() << endl;
  strncpy( msgbuf, oss.str().c_str(), MAX_BUFFER-1 );
  len = strlen(msgbuf);
  if(comm->send_msg( myrobot->get_sock(), len, msgbuf) == -1){
      //cerr << "**error> failed to send message" << endl;
    push_paint("ERROR", "failed to send message",
	    myrobot->get_session_id());
      return STATE_QUIT;
  }

  return STATE_IDLE;
}

int client_ask_pose( char *msgbuf, robot *myrobot) {
  string p(msgbuf);
  stringstream istream(p);
  string command;
  long session_id;
  
  //cout << "Read ask pose command: " << p << endl;
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

  return STATE_IDLE;
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
      myrobot->set_pose(x, y, theta, con);
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
  //cout << "Read ASK player command: " << p << endl;
  istream >> command >> session_id;
  ostringstream oss;


  list<robot_p>::iterator bot;

  if (find_robot(session_id, bot)) {
    if(session_id != myrobot->get_session_id()){
      string cmd = command + " ";
      oss << session_id;
      cmd += oss.str();
      cmd += " ";
      oss.str("");
      oss << myrobot->get_session_id();
      cmd += oss.str();
      //cout << "Pusing PLAYER CMD: " << cmd << endl;
      (*bot)->push_msg(cmd.c_str());
    }else{
      len = strlen(msgbuf);
      if(comm->send_msg( myrobot->get_sock(), len, msgbuf) == -1){
	push_paint("ERROR", "failed to send message",
		myrobot->get_session_id());
	  //cerr << "**error> failed to send message" << endl;
	  return STATE_QUIT;
      }

    }
  }else{
    //cout << "ASKPOSE: Robot not found" << endl;
    // shoud appened robot_id
    push_paint("ERROR", "client_ask_player: robot not found",
	    myrobot->get_session_id());
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
  unsigned char len = 0;


  istream >> command >> requestor_id >> session_id >> ip >> port;

  list<robot_p>::iterator bot;

  if (find_robot(requestor_id, bot)) {
    if(requestor_id != myrobot->get_session_id()){
      (*bot)->push_msg(p);
    }else{
      len = strlen(msgbuf);
      //cout << "Sending off player info to GUI: " << myrobot->get_session_id() << endl;

      if(comm->send_msg( myrobot->get_sock(), len, msgbuf) == -1){
	push_paint("ERROR", "failed to send message",
		myrobot->get_session_id());
	//      cerr << "**error> failed to send message" << endl;
	      return STATE_QUIT;
      }

      return STATE_IDLE;

    }

    push_paint("ERROR", "client_ask_player: robot not found",
	    myrobot->get_session_id());
    //cout << "Robot not found" << endl;
  }

  return STATE_IDLE;
}

void *posebeat( void *arg ) {
  string p;
  string command = "ASKPOSE";
  long session_id;
  ostringstream oss;

  for (;;) {
    //cout << "Asking for new poses ... " << endl;
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

    sleep(1);
  }
}

void *pencil( void *arg ) {
  string buffer;
  ofstream log;
  thread_args *t_args = (thread_args *)arg;

  if(t_args->log_enabled){
    log.open(t_args->logfile);
  }

  for(;;){
    if(paint_empty())
      continue;

    buffer = pop_paint();

    cout << buffer << endl;

    if(t_args->log_enabled){
      log << buffer << endl;
    }

    napms(250);
  }

}

void *pushy( void *arg ) {
  int rnd;
  string types[] = { "ERROR", "SENT", "RECIEVED", "POPPED", "PUSHED" };
  long ids[] = { 11290808, 12990123, 9812381,
    1239823, 23432434 };

  for(;;){
    rnd = rand() % 5;
    push_paint(types[rnd].c_str(), "POSE 1233442214 0 0 0 0",  ids[rnd]);
    sleep(1 + rand() % 3);
  }
}

/**
 * paintbrush()
 *
 * This is a little paintbrush function that is spawned by a thread in the server.
 * It provides a count to show that the server is still alive.
 *
 */
void *paintbrush( void *arg ) {
  time_t rawtime;
  struct tm * tm;
  char ch;
  bool paused = false;
  bool filter_visible = false;
  string buffer;
  int center;
  ofstream log;
  thread_args *t_args = (thread_args *)arg;

  if(t_args->log_enabled){
    log.open(t_args->logfile);
  }

  char paused_msg[] = "[ PAUSED ]";

  regex_t regx;
  int current_pos = 0;
  const int input_size = 40;
  char input_buffer[input_size];

  int x, y;

  getmaxyx(stdscr, y, x); 
  center = x - sizeof(server_title);
  center = center / 2;

  regcomp(&regx, ".", REG_EXTENDED);

  while ( 1 ) {
    time ( &rawtime );
    tm = localtime ( &rawtime );

    werase(status_bar);
    wprintw(status_bar, "[ %02d:%02d:%02d ]",
	  tm->tm_hour, tm->tm_min, tm->tm_sec);
    wprintw(status_bar, "%10s %d", " Clients Connected:", num_threads);
    wprintw(status_bar, "%15s %s %s %d",
	    " SERVER IP:", t_args->ip, "SERVER PORT:", t_args->port);

    ch = getch();

    if(filter_visible){
        if(ch == 27){ // Escape Key
          filter_visible = false;
          touchwin(output);
          refresh();
        }else if(ch == 10){ // Enter Key
          filter_visible = false;
          input_buffer[current_pos] = '\0';

          if(current_pos == 0){
              regcomp(&regx, ".", REG_EXTENDED | REG_ICASE);
          }else{
              regcomp(&regx, input_buffer, REG_EXTENDED | REG_ICASE);
          }

          touchwin(output);
          refresh();
        }else if(ch == 127){ // Backspace Key
          if(current_pos > 0){
              wmove(filter_menu, 1, 9 + current_pos);
              waddch(filter_menu, ' ');
              input_buffer[current_pos] = '\0';
              --current_pos;
          }else{
	    flash();
	  }
        }else if(ch != ERR){
          if(current_pos < input_size){
              wmove(filter_menu, 1, 10 + current_pos);
              waddch(filter_menu,ch);
              input_buffer[current_pos] = (char) ch;
              ++current_pos;
          }else{
            flash();
          }
        }

      }else{
          if(ch == ' '){

            paused = !paused;
	    if(!paused){
	      werase(title_bar);
	      mvwprintw(title_bar, 0, center,  "%s", server_title);
	    }else{
	      werase(title_bar);
	      mvwprintw(title_bar, 0, center - sizeof(paused_msg),
		      "%s", paused_msg);

	      mvwprintw(title_bar, 0, center, "%s %s",
		      server_title, paused_msg);
	    }

          }else if(ch == 'f' || ch == 'F'){
            filter_visible = !filter_visible;
            werase(filter_menu);
            mvwprintw(filter_menu, 1, 2,  "%s", "Filter: ");
            box(filter_menu, 0, 0);
            current_pos = 0;

          }else if(ch == 'q' || ch == 'Q'){
            endwin();
	    exit(0);
          }
      }
    
    wnoutrefresh(status_bar);
    touchwin(status_bar);
    touchwin(title_bar);
    touchwin(filter_menu);

    if(!paused){
      wnoutrefresh(output);
    }

    if(filter_visible){
      wnoutrefresh(filter_menu);
    }

    wnoutrefresh(title_bar);
    wnoutrefresh(status_bar);

    doupdate();

    if(paint_empty())
      continue;

    buffer = pop_paint();

    if(t_args->log_enabled){
      log << buffer << endl;
    }
    
    if(regexec(&regx, buffer.c_str(), 0, 0, 0) == 0){
	  color_msg(output, buffer);
    }

  }


} // end of paintbrush()



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
  char *msgbuf = (char *)malloc( MAX_BUFFER*sizeof(char) );
  char *pmsg   = (char *)malloc( MAX_BUFFER*sizeof(char) );
  char *pargs  = (char *)malloc( MAX_BUFFER*sizeof(char) );
  string msg;

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
	    //cout << "State was: " << state << endl;
	    push_paint("ERROR", "Invalid State", myrobot->get_session_id());
	    command = STATE_ERROR;
	    break;
    }

    if( command >= 0) {
      state = (*function_array[command])(msgbuf, myrobot);
    }else{
      //do something
    }

    usleep(1); // give up some cpu time
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
  pthread_t thread_id_x, thread_id0, thread_id1, thread_id[MAX_THREADS]; // array of thread id's
  struct sockaddr_in sin;
  socklen_t slen = sizeof( sin );

  int x, y;
  int center;
  int ch;

  thread_args *t_args = new thread_args;

  bool ip_given = false;
  bool port_given = false;
  t_args->log_enabled = false;

  if(argc < 2)
    display_usage(argv[0]);

  while ((ch = getopt (argc, argv, "il:b:p:")) != -1){
    switch(ch){
	case 'i':
		t_args->interactive = true;
		break;
	    case 'l':
		strncpy(t_args->logfile, optarg, sizeof(t_args->logfile));
		t_args->log_enabled = true;
		break;
	    case 'b':
		strncpy(t_args->ip, optarg, sizeof(t_args->ip));
		ip_given = true;
		break;
	    case 'p':
		t_args->port = atoi(optarg);
		port_given = true;
		break;
	    default:
		display_usage(argv[0]);
		exit(1);
		break;
    }
  }

  if(t_args->ip[0] == '\0'){
    cerr << "error: ip address required" << endl;
    display_usage(argv[0]);
  }else if(t_args->port < 0){
    cerr << "error: port required" << endl;
    display_usage(argv[0]);
  }


  // initialize list of robots
  robots.clear();

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
  
  // NCURSES setup
  if(t_args->interactive){
      initscr();
      start_color();

      getmaxyx(stdscr, y, x); 

      center = x - sizeof(server_title);
      center = center / 2;

      title_bar = newwin(1, x, 0, 0); 
      output = newwin(y - 1, x, 2, 0); 
      status_bar = newwin(1, x, y - 1, 0); 
      filter_menu =
	  newwin(3, x - x / 4 , (y - 3) / 2, ( x - (x - x / 4)) / 2 );

      init_pair(1, COLOR_BLACK, COLOR_WHITE);
      init_pair(2, COLOR_GREEN, COLOR_BLACK);
      init_pair(3, COLOR_BLACK, COLOR_RED);
      init_pair(4, COLOR_CYAN, COLOR_BLACK);
      init_pair(5, COLOR_MAGENTA, COLOR_BLACK);
      init_pair(6, COLOR_WHITE, COLOR_BLACK);
      init_pair(7, COLOR_YELLOW, COLOR_BLACK);
      init_pair(8, COLOR_WHITE, COLOR_BLACK);
      init_pair(9, COLOR_RED, COLOR_BLACK);
      init_pair(10, COLOR_BLUE, COLOR_BLACK);
      init_pair(11, COLOR_WHITE, COLOR_BLUE);

      scrollok(output, TRUE);
      nodelay(output, TRUE);
      noecho();
      nodelay(stdscr, TRUE);
      curs_set(0);

      wbkgd(title_bar, COLOR_PAIR(11) | A_BOLD | A_DIM );
      wbkgd(status_bar, COLOR_PAIR(1) | A_BOLD | A_DIM );
      wbkgd(filter_menu, COLOR_PAIR(11) | A_BOLD | A_DIM );

      mvwprintw(title_bar, 0, center,  "%s", server_title);

      wprintw(status_bar, "[ 00:00:00 ]");
      wprintw(status_bar, "%50s %d", "Clients Connected:", 0);
      wnoutrefresh(title_bar);
      wnoutrefresh(status_bar);
      doupdate();
  } // end NCURSES setup
  
  // create server socket
  comm = new commServer(t_args->ip, t_args->port);
  
  //wprintw(output, "clients connect using: host=[%s] port=[%d]\n", comm->get_host(), comm->get_port() );
  
  if(t_args->interactive){
      if ( pthread_create( &thread_id0, NULL, &paintbrush, (void *)t_args )) {
	perror( "**error> server/basic thread" );
	exit( 1 );
      }
#ifdef SIMULATE
      if ( pthread_create( &thread_id_x, NULL, &pushy, (void *)t_args )) {
	perror( "**error> server/basic thread" );
	exit( 1 );
      }
#endif
  }else{
      if ( pthread_create( &thread_id0, NULL, &pencil, (void *)t_args )) {
	perror( "**error> server/basic thread" );
	exit( 1 );
      }

#ifdef SIMULATE
      if ( pthread_create( &thread_id_x, NULL, &pushy, (void *)t_args )) {
	perror( "**error> server/basic thread" );
	exit( 1 );
      }
#endif
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

    //fprintf( stdout,"server: socket created/listen/accepted\n" );
    //fflush( stdout );
    
    wprintw( output,"server status: socket created/listen/accepted\n" );

    // create an entry in the robots list for this client
    robot_p rp = new robot();
    if (rp == 0) {
        //cerr << "**error> failed to create new robot" << endl;
	print_error(output, "Failed to created new robot");
    } else {
        rp->set_session_id(time(0));
        rp->set_sock(csock);
        //cout << "created new robot, id=" << rp->get_session_id() << endl;
	wprintw( output,"created new robot, id=%d\n",
		rp->get_session_id() );

        // create a new thread for this client
        pthread_mutex_lock( &threads_mutex );
        if ( pthread_create( &thread_id[num_threads], 
	    		 NULL, 
	    		 &client_handler, 
	    		 (void *)rp )) {
          //printf( "**error> could not create thread for client_handler\n" );
          print_error(output, "could not create thread for client_handler");
          delete rp;
        }
        else {
          if (pthread_detach(thread_id[num_threads]) != 0) {
             //cerr << "**error> failed to detach thread" << endl;
             print_error(output, "**error> failed to detach thread");
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
