/**
 * robot.cpp
 *
 * original: gonzalez/28-june-2010
 * modification: sklar/28-june-2010
 *
 */


#include "robot.h"



/**
 * robot constructor()
 *
 */
robot::robot()
{
  tag.session_id = -1;
  tag.owner_id = -1;
  tag.owner = "";
  tag.type_id = -1;
  tag.type = "";
  tag.name = "";
  tag.disconnect_count = 0;
  tag.last_msg = 0;
  mypose = new pose();
  thread_id = NULL;
  pthread_mutex_init( &mutexq,NULL );
} // end of robot constructor


/**
 * set_session_id()
 *
 * This function sets the value of the "session_id" data field.
 *
 */
void robot::set_session_id( long id ){
  tag.session_id = id;
} // end of set_session_id()

/**
 * get_session_id()
 *
 * This function returns the value of the "session_id" data field.
 *
 */
long robot::get_session_id(){
  return tag.session_id;
} // end of get_session_id()

void robot::set_last_msg_seen(long timestamp){
  tag.last_msg = timestamp;
} 

long robot::get_last_msg_seen(){
  return tag.last_msg;
}

void robot::set_disconnect_count( int num){
 tag.disconnect_count = num; 
}

void robot::inc_disconnect_count(){
  ++tag.disconnect_count;
}

int robot::get_disconnect_count(){
  return tag.disconnect_count;
}


/**
 * set_owner_id()
 *
 * This function sets the value of the "owner_id" data field.
 *
 */
void robot::set_owner_id(int id){
  tag.owner_id = id;
} // end of set_owner_id()

/**
 * get_owner_id()
 *
 * This function returns the value of the "owner_id" data field.
 *
 */
int robot::get_owner_id(){
  return tag.owner_id;
} // end of get_owner_id()


/**
 * set_owner()
 *
 * This function sets the value of the "owner" data field.
 *
 */
void robot::set_owner( string owner ){
  tag.owner = owner;
} // end of set_owner()


void robot::add_to_provides( string s )
{
	tag.provides.push_back(s);
}

/**
 * get_owner()
 *
 * This function returns the value of the "owner" data field.
 *
 */
const string robot::get_owner(){
  return( tag.owner );
} // end of get_owner()


/**
 * set_type_id()
 *
 * This function sets the value of the "type_id" data field.
 *
 */
void robot::set_type_id( string type_id ){
  tag.type_id = type_id;
} // end of set_type_id()


/**
 * get_type_id()
 *
 * This function returns the value of the "type_id" data field.
 *
 */
const string robot::get_type_id(){
  return tag.type_id;
} // end of get_type_id()


/**
 * set_type()
 *
 * This function sets the value of the "type" data field.
 *
 */
void robot::set_type( string type ){
  tag.type = type;
} // end of set_type()


/**
 * get_type()
 *
 * This function returns the value of the "type" data field.
 *
 */
const string robot::get_type(){
  return( tag.type );
} // end of get_type()


/**
 * set_pose()
 *
 * This function sets the value of the "mypose" data field.
 *
 */
void robot::set_pose( double x_coord, double y_coord, double theta_pos, double confidence ) {
  mypose->set( x_coord, y_coord, theta_pos, confidence );
} // end of set_pose()


/**
 * get_pose()
 *
 * This function returns a pointer to the "mypose" data field.
 *
 */
pose *robot::get_pose(){
  return mypose;
} // end of get_pose()


/**
 * set_name()
 *
 * This function sets the value of the "name" data field.
 *
 */
void robot::set_name( string name0 ) {
  //strncpy( tag.name,name0,FIELD_SIZE-1 );
  //tag.name[FIELD_SIZE-1] = '\0';
  tag.name = name0;
} // end of set_name()


/**
 * get_name()
 *
 * This function returns the value of the "name" data field.
 *
 */
const string robot::get_name(){
  return tag.name;
} // end of get_name()

void robot::set_player_ip(string ip){
  tag.player_ip = ip;
}

string robot::get_player_ip(){
  return tag.player_ip;
}

void robot::set_player_port(int port){
  tag.player_port = port;
}

int robot::get_player_port(){
  return tag.player_port;
}



/**
 * set_host()
 *
 * This function sets the value of the host.
 *
 */
void robot::set_host( string host0 ) {
  host = host0;
} // end of set_host()

/**
 * get_host()
 *
 * This function returns the value of the host.
 *
 */
const string robot::get_host() {
  return( host );
} // end of get_host()



/**
 * set_port()
 *
 * This function sets the value of the port number.
 *
 */
void robot::set_port( int port0 ) {
  port = port0;
} // end of set_port()




/**
 * get_port()
 *
 * This function returns the value of the port number.
 *
 */
int robot::get_port() {
  return( port );
} // end of get_port()



/**
 * set_sock()
 *
 * This function sets the value of the socket descriptor.
 *
 */
void robot::set_sock( int sock0 ) {
  sock = sock0;
} // end of set_sock();

/**
 * get_sock()
 *
 * This function returns the value of the socket descriptor.
 *
 */
int robot::get_sock() {
  return( sock );
} // end of get_sock()


/**
 * set_thread_id()
 *
 * This function sets the value of the thread_id.
 *
 */
void robot::set_thread_id( pthread_t thread_id0 ) {
  thread_id = thread_id0;
} // end of set_thread_id()

/**
 * get_thread_id()
 *
 * This function returns the value of the thread_id.
 *
 */
pthread_t robot::get_thread_id() {
  return( thread_id );
} // end of get_thread_id()



/**
 * robot destructor
 *
 */
robot::~robot()
{
  //if(provides != NULL){
  //  for( int i = 0 ; i < tag.num_of_provides ; i++ )
  //      delete [] provides[i];
  //  delete [] provides;
  //}

  delete mypose;
  pthread_mutex_destroy( &mutexq );
} // end of robot destructor


/**
 * peek_msg()
 *
 * 
 * 
 *
 */
bool robot::msg_empty() {
  bool avail = false;

  pthread_mutex_lock( &mutexq );

  if(msgq.empty()){
    avail = true;
  }

  pthread_mutex_unlock( &mutexq );

  return avail;
} // end of peek_msg()

/**
 * push_msg()
 *
 * This function adds the argument string "msg" to the back of the
 * message queue.
 *
 */
void robot::push_msg( string msg ) {
  pthread_mutex_lock( &mutexq );
  if(!msg.empty()){
    msgq.push( msg );
  }else{
    cout << "Got empty string" << endl;
  }
  pthread_mutex_unlock( &mutexq );
} // end of push_msg()



/**
 * pop_msg()
 *
 * This function removes the message from the front of the message
 * queue.  The function returns the message found, as a string.  If no
 * message is found (i.e., the queue is empty), then the function
 * returns an empty message string.
 *
 */
const string robot::pop_msg() {
  string msg;
  pthread_mutex_lock( &mutexq );
  if ( msgq.empty() ) {
    msg.clear();
  }
  else {
    msg = msgq.front();
    msgq.pop();
  }
  pthread_mutex_unlock( &mutexq );
  return( msg );
} // end of pop_msg()

