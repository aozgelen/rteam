#include "definitions.h"
#include "robot.h"

#include <iostream>
#include <string>
#include <sstream>
#include <cstring>
#include <vector>
#include <list>

#define ROBOT_DEBUG false

using namespace PlayerCc;
using namespace metrobotics;
using namespace std;

Robot::Robot(PlayerClient& pc, InterfaceToLocalization * i, string id, string type, Behavior* bp)
  :mPlayerClient(pc), mIOService(), mSocket(mIOService), itl(i), mBehavior(bp), mNameID(id), mTypeID(type)
{
  // setup interface to localization
  itl->setBlobFinderProxy(&pc); 
  itl->setPosition2dProxy(&pc); 

  // Initialize robot capabilities.
  init_provides();
  
  // Initialize default state.
  init_state();
}

Robot::~Robot()
{
  Disconnect();
}

bool Robot::Connect(const string& hostname, unsigned short port)
{
  // Prepend function signature to error messages.
  static const string signature = "Robot::Connect()";
  
  // Save us some typing -- I'm already feeling the effects of RSI.
  using namespace boost::system;
  using namespace boost::asio;
  
  // Make sure that the socket isn't already open.
  if (mSocket.is_open()) {
    Disconnect();
  }
  
  error_code ec; // Used to check for errors.
  
  // Get the IP address of the host.
  ip::address addr = ip::address::from_string(hostname, ec);
  if (ec) {
    if (ROBOT_DEBUG) cerr << signature << " - failed to retrieve host's IP address" << endl;
    return false;
  }
  
  // Connect to the host.
  ip::tcp::endpoint endpt(addr, port);
  mSocket.connect(endpt, ec);
  if (ec) {
    if (ROBOT_DEBUG) cerr << signature << " - failed to connect to host" << endl;
    return false;
  }
  
  // Go back to the initial state.
  init_state();
  return true;
}

void Robot::Disconnect()
{
  // Prepend function signature to error messages.
  static const string signature = "Robot::Disconnect()";
  
  // Save us some typing -- I'm already feeling the effects of RSI.
  using namespace boost::system;
  using namespace boost::asio;
  
  // Make sure that the socket is already open.
  if (!mSocket.is_open()) return;
  
  error_code ec; // Used to check for errors.
  
  // Shutdown the connection.
  mSocket.shutdown(ip::tcp::socket::shutdown_both, ec);
  if (ec) {
    if (ROBOT_DEBUG) cerr << signature << " - failed to disconnect" << endl;
  }
  
  // Close the socket.
  mSocket.close(ec);
  if (ec) {
    if (ROBOT_DEBUG) cerr << signature << " - failed to close the socket" << endl;
  }
}

void Robot::Update()
{
  // Prepend function signature to error messages.
  static const string signature = "Robot::Update()";
  
  // Save us some typing -- I'm already feeling the effects of RSI.
  using namespace boost::system;
  using namespace boost::asio;
  
  // Make sure that the socket is already open.
  if (!mSocket.is_open()) return;
  
  error_code ec; // Used to check for errors.
	
  // update the sensor readings 
  
  // Update Player interfaces.
  mPlayerClient.ReadIfWaiting(); 
  itl->update();

  
  // Maintain the state machine.
  switch (mCurrentState) {
  case STATE_INIT: {
    do_state_action_init();
  } break;
  case STATE_ACK: {
    do_state_action_ack();
  } break;
  case STATE_IDLE: {
    do_state_action_idle();
  } break;
  case STATE_PING_SEND: {
    do_state_action_ping_send();
  } break;
  case STATE_PONG_READ: {
    do_state_action_pong_read();
  } break;
  case STATE_PONG_SEND: {
    do_state_action_pong_send();
  } break;
  case STATE_CMD_PROC: {
    do_state_action_cmd_proc();
  } break;
  case STATE_MOVING: {
    do_state_action_moving();
  } break;
  case STATE_FOUND: {
    do_state_action_found(); 
  }break;
  case STATE_POSE: {
    do_state_action_pose();
  } break;
  case STATE_PLAYER: {
    do_state_action_player();
  } break;
  default: {
    if (ROBOT_DEBUG) cerr << signature << " - unrecognized state" << endl;
    do_state_change(STATE_QUIT);
  } break;
  }

  // Update behavior.
  if (mBehavior && !mPossessed) {
    mBehavior->Update();
  }
}

void Robot::init_state()
{
  mCurrentState = STATE_INIT;
  mSessionID    = -1;
  mPossessed    = false;
}

void Robot::init_provides()
{
  // Prepend function signature to error messages.
  static const string signature = "Robot::init_provides()";

  // Acquire list of interfaces from Player.
  static const double timeout = 5.0;
  typedef list<playerc_device_info_t> DevList;
  DevList dev;
  try {
    PosixTimer pt;
    while (dev.empty()) {
      mPlayerClient.RequestDeviceList();
      dev = mPlayerClient.GetDeviceList();
      if (pt.elapsed() >= timeout) {
	throw PlayerError();
      }
    }
  } catch (PlayerError) {
    if (ROBOT_DEBUG) cerr << signature << " - failed to acquire capabilities from Player Server" << endl;
    dev.clear();
  }
  
  // Use the interfaces to build our list of provides.
  for (DevList::iterator iter = dev.begin(); iter != dev.end(); ++iter) {
    // NOTE: we're assuming ONE index (zero) per interface.
    static const int default_index = 0;
    if (iter->addr.index == default_index) {
      switch (iter->addr.interf) {
      case PLAYER_POSITION2D_CODE: {
	if (!mPosition2D) {
	  Position2dProxy* pp = 0;
	  // Subscribe to the Position2D interface.
	  try {
	    if ((pp = new Position2dProxy(&mPlayerClient, default_index))) {
	      if ((mPosition2D = boost::shared_ptr<Position2dProxy>(pp))) {
		mProvidesList.push_back(CAPS_POSITION2D);
	      }
	    } else {
	      if (ROBOT_DEBUG) cerr << signature << " - failed to allocate memory for Position2D proxy" << endl;
	    }
	  } catch (PlayerError) {
	    if (ROBOT_DEBUG) cerr << signature << " - failed to subscribe to Position2D proxy" << endl;
	    delete pp;
	  }
	}
      } break;
      default: break;
      }
    }
  }
}

bool Robot::msg_waiting() const
{
  // Prepend function signature to error messages.
  static const string signature = "Robot::msg_waiting()";
  
  // Save us some typing -- I'm already feeling the effects of RSI.
  using namespace boost::system;
  using namespace boost::asio;
  
  // Make sure that the socket is already open.
  if (!mSocket.is_open()) return false;
  
  error_code ec; // Used to check for errors.
  
  // Check the socket.
  size_t payload = mSocket.available(ec);
  if (ec) {
    if (ROBOT_DEBUG) cerr << signature << " - failed to peek at the socket" << endl;
    return false;
  } else if (payload >= sizeof(cmd_len_t)) {
    return true;
  } else {
    return false;
  }
}

bool Robot::read(std::stringstream& ss)
{
  // Prepend function signature to error messages.
  static const string signature = "Robot::read()";
  
  // Make sure that the socket is already open.
  if (!mSocket.is_open()) return false;
  
  // Read the message.
  boost::asio::streambuf inputBuffer;
  try {
    cmd_len_t len;
    boost::asio::read(mSocket, boost::asio::buffer(&len, sizeof(cmd_len_t)));
    size_t n = boost::asio::read(mSocket, inputBuffer.prepare(len));
    inputBuffer.commit(n);
    mSilenceTimer.start();
  } catch (boost::system::system_error) {
    if (ROBOT_DEBUG) cerr << signature << " - failed to read message" << endl;
    return false;
  }
  
  // Convert the message into a workable format.
  istream is(&inputBuffer);
  // Clear the contents of the argument.
  ss.str("");
  // Fill the argument with the message that we just read.
  ss << is.rdbuf();
  
  // Spam standard out so that the user can feel like they're in the Matrix.
  if (ROBOT_DEBUG) 
    cout << mNameID << "::read = [" << ss.str() << "]" << endl;
  
  return true;
}

bool Robot::write(const stringstream& ss)
{
  // Prepend function signature to error messages.
  static const string signature = "Robot::write()";
  
  // Make sure that the socket is already open.
  if (!mSocket.is_open()) return false;
  
  // Compute the maximum size of a message.
  const static size_t num_bits = 8 * sizeof(cmd_len_t);
  const static string::size_type max_size = (1 << num_bits) - 1;
  
  // Make sure that the message doesn't exceed the maximum size.
  string msg = ss.str();
  if (msg.size() > max_size) {
    if (ROBOT_DEBUG) cerr << signature << " - message is too large" << endl;
    return false;
  }
  
  // Build the message.
  boost::asio::streambuf outputBuffer;
  ostream os(&outputBuffer);
  // First the preamble: size of the message.
  cmd_len_t len = static_cast<cmd_len_t>(msg.size());
  os.write(reinterpret_cast<const char *>(&len), static_cast<streamsize>(sizeof(cmd_len_t)));
  // Now the message itself.
  os << msg;
  
  // Send the message.
  try {
    size_t n = boost::asio::write(mSocket, outputBuffer.data());
    outputBuffer.consume(n);
    mSilenceTimer.start();
  } catch (boost::system::system_error) {
    if (ROBOT_DEBUG) cerr << signature << " - failed to send message" << endl;
    return false;
  }
  
  // Spam standard out so that the user can feel like they're in the Matrix.
  if (ROBOT_DEBUG) 
    cout << mNameID << "::sent = [" << msg << "]" << endl;
  
  return true;
}

void Robot::do_state_change(int state)
{
  // Don't make false changes.
  if (mCurrentState != state) {
    if (state == STATE_INIT) {
      init_state();
    } else {
      mCurrentState = state;
    }
    // Update the state timer.
    mStateTimer.start();
  }
}

void Robot::do_state_action_init()
{
  // Prepend function signature to error messages.
  static const string signature = "Robot::do_state_action_init()";
  
  // Send the INIT command.
  stringstream ss;
  ss << CMD_INIT << " " << mTypeID << " " << mNameID << " " << mProvidesList.size();
  for (vector<string>::const_iterator iter = mProvidesList.begin();
       iter != mProvidesList.end(); ++iter) {
    ss << " " << *iter;
  }
  if (write(ss)) {
    if (ROBOT_DEBUG) cerr << signature << " - success; next state: STATE_ACK" << endl;
    do_state_change(STATE_ACK);
  } else if (mStateTimer.elapsed() >= MAX_TIME_STATE) {
    if (ROBOT_DEBUG) cerr << signature << " - timeout; next state: STATE_QUIT" << endl;
    do_state_change(STATE_QUIT);
  } else {
    if (ROBOT_DEBUG) cerr << signature << " - failure; next state: STATE_INIT" << endl;
    do_state_change(STATE_INIT);
  }
}

void Robot::do_state_action_ack()
{
  // Prepend function signature to error messages.
  static const string signature = "Robot::do_state_action_ack()";
  
  // Don't wait forever.
  if (mStateTimer.elapsed() >= MAX_TIME_STATE) {
    if (ROBOT_DEBUG) cerr << signature << " - timeout; next state: STATE_INIT" << endl;
    do_state_change(STATE_INIT);
  }
  
  // Don't block while waiting for the command.
  if (!msg_waiting()) return;
  
  // Prepare to read the command.
  stringstream ss;
  string cmd;
  long session_id;
  if (read(ss) && (ss >> cmd >> session_id) && (cmd.find(CMD_ACK) != string::npos)) {
    if (session_id < 0) {
      mSessionID = -1;
      if (ROBOT_DEBUG) cerr << signature << " - rejected; next state: STATE_QUIT" << endl;
      do_state_change(STATE_QUIT);
    } else {
      mSessionID = session_id;
      if (ROBOT_DEBUG) cerr << signature << " - accepted; next state: STATE_IDLE" << endl;
      do_state_change(STATE_IDLE);
    }
  } else {
    if (ROBOT_DEBUG) cerr << signature << " - failure; next state: STATE_INIT" << endl;
    do_state_change(STATE_INIT);
  }
}

void Robot::do_state_action_idle()
{
  // Prepend function signature to error messages.
  static const string signature = "Robot::do_state_action_idle()";
  
  // Keep an eye out for new commands.
  if (msg_waiting()) {
    if (ROBOT_DEBUG) cerr << signature << " - received command; next state: STATE_CMD_PROC" << endl;
    do_state_change(STATE_CMD_PROC);
  } else if (mSilenceTimer.elapsed() >= MAX_TIME_SILENCE) {
    // Don't let the connection die.
    if (ROBOT_DEBUG) cerr << signature << " - max silence exceeded; next state: STATE_PING_SEND" << endl;
    do_state_change(STATE_PING_SEND);
  }
}

void Robot::do_state_action_ping_send()
{
  // Prepend function signature to error messages.
  static const string signature = "Robot::do_state_action_ping_send()";
  
  // Send the PING command.
  stringstream ss;
  ss << CMD_PING;
  if (write(ss)) {
    if (ROBOT_DEBUG) cerr << signature << " - success; next state: STATE_PONG_READ" << endl;
    do_state_change(STATE_PONG_READ);
  } else if (mStateTimer.elapsed() >= MAX_TIME_STATE) {
    if (ROBOT_DEBUG) cerr << signature << " - timeout; next state: STATE_INIT" << endl;
    do_state_change(STATE_INIT);
  } else {
    if (ROBOT_DEBUG) cerr << signature << " - failure; next state: STATE_PING_SEND" << endl;
    do_state_change(STATE_PING_SEND);
  }
}

void Robot::do_state_action_pong_read()
{
  // Prepend function signature to error messages.
  static const string signature = "Robot::do_state_action_pong_read()";
  
  // Don't wait forever.
  if (mStateTimer.elapsed() >= MAX_TIME_STATE) {
    if (ROBOT_DEBUG) cerr << signature << " - timeout; next state: STATE_PING_SEND" << endl;
    do_state_change(STATE_PING_SEND);
  }
  
  // Don't block while waiting for the command.
  if (!msg_waiting()) return;
  
  // Prepare to read the command.
  stringstream ss;
  string cmd;
  if (read(ss) && (ss >> cmd) && (cmd.find(CMD_PONG) != string::npos)) {
    if (ROBOT_DEBUG) cerr << signature << " - success; next state: STATE_IDLE" << endl;
    do_state_change(STATE_IDLE);
  } else {
    if (ROBOT_DEBUG) cerr << signature << " - failure; next state: STATE_PING_SEND" << endl;
    do_state_change(STATE_PING_SEND);
  }
}

void Robot::do_state_action_pong_send()
{
  // Prepend function signature to error messages.
  static const string signature = "Robot::do_state_action_pong_send()";
  
  // Send the PONG command.
  stringstream ss;
  ss << CMD_PONG;
  if (write(ss)) {
    if (ROBOT_DEBUG) cerr << signature << " - success; next state: STATE_IDLE" << endl;
    do_state_change(STATE_IDLE);
  } else if (mStateTimer.elapsed() >= MAX_TIME_STATE) {
    if (ROBOT_DEBUG) cerr << signature << " - timeout; next state: STATE_PING_SEND" << endl;
    do_state_change(STATE_PING_SEND);
  } else {
    if (ROBOT_DEBUG) cerr << signature << " - failure; next state: STATE_PONG_SEND" << endl;
    do_state_change(STATE_PONG_SEND);
  }
}

void Robot::do_state_action_cmd_proc()
{
  // Prepend function signature to error messages.
  static const string signature = "Robot::do_state_action_cmd_proc()";

  // if object we're looking for is found broadcast it to everyone
  if ( itl->isFound() ){
    do_state_action_found();
    return ;
  }
  
  // Don't block while waiting for the command.
  if (!msg_waiting()) return;
  
  // Prepare to read the command.
  stringstream ss;
  string cmd;
  if (read(ss) && (ss >> cmd)) {
    // Process the command.
    if (cmd.find(CMD_PING) != string::npos)
      {
	if (ROBOT_DEBUG) cerr << signature << " - PING; next state: STATE_PONG_SEND" << endl;
	do_state_change(STATE_PONG_SEND);
      }
    else
      if (cmd.find(CMD_UNLOCK) != string::npos)
	{
	  mPossessed = false;
	  // Continue behavior.
	  if (mBehavior) {
	    mBehavior->Restart();
	  }
	  if (ROBOT_DEBUG) cerr << signature << " - UNLOCK; next state: STATE_IDLE" << endl;
	  do_state_change(STATE_IDLE);
	}
      else
	if (cmd.find(CMD_LOCK) != string::npos)
	  {
	    mPossessed = true;
	    // Cease behavior.
	    if (mBehavior) {
	      mBehavior->Stop();
	    }
	    if (ROBOT_DEBUG) cerr << signature << " - LOCK; next state: STATE_IDLE" << endl;
	    do_state_change(STATE_IDLE);
	  }
	else
	  if (cmd.find(CMD_MOVE) != string::npos)
	    {
	      // Save the command; it will be processed in the next state.
	      mStringBuffer = ss.str();
	      if (ROBOT_DEBUG) cerr << signature << " - MOVE; next state: STATE_MOVING" << endl;
	      do_state_change(STATE_MOVING);
	    }
	  else
	    if (cmd.find(CMD_ASKPOSE) != string::npos)
	      {
		if (ROBOT_DEBUG) cerr << signature << " - ASKPOSE; next state: STATE_POSE" << endl;
		do_state_change(STATE_POSE);
	      }
	    else
	      if (cmd.find(CMD_ASKPLAYER) != string::npos)
		{
		  // Save the command; it will be used in the next state.
		  mStringBuffer = ss.str();
		  if (ROBOT_DEBUG) cerr << signature << " - ASKPLAYER; next state: STATE_PLAYER" << endl;
		  do_state_change(STATE_PLAYER);
		}
	      else
		if (cmd.find(CMD_QUIT) != string::npos)
		  {
		    if (ROBOT_DEBUG) cerr << signature << " - QUIT; next state: STATE_QUIT" << endl;
		    do_state_change(STATE_QUIT);
		  }
		else
		  {
		    if (ROBOT_DEBUG) cerr << signature << " - unrecognized command; next state: STATE_IDLE" << endl;
		    do_state_change(STATE_IDLE);
		  }
  } else {
    if (ROBOT_DEBUG) cerr << signature << " - failure; next state: STATE_IDLE" << endl;
    do_state_change(STATE_IDLE);
  }
}

void Robot::do_state_action_moving()
{
  // Prepend function signature to error messages.
  static const string signature = "Robot::do_state_action_moving()";
  
  // Make sure that we're locked.
  if (!mPossessed) {
    stringstream oss;
    oss << CMD_ERROR << " " << CMD_MOVE << " failed: not locked";
    write(oss);
    if (ROBOT_DEBUG) cerr << signature << " - failure; next state: STATE_IDLE" << endl;
    do_state_change(STATE_IDLE);
    return;
  }
  
  // Make sure that we're providing Position2D.
  if (!mPosition2D) {
    stringstream oss;
    oss << CMD_ERROR << " " << CMD_MOVE << " failed: not providing " << CAPS_POSITION2D;
    write(oss);
    if (ROBOT_DEBUG) cerr << signature << " - failure; next state: STATE_IDLE" << endl;
    do_state_change(STATE_IDLE);
    return;
  }
  
  // Make sure that we have the correct arguments.
  string command;
  long id = -1;
  double xv = 0, yv = 0, av = 0;
  stringstream iss(mStringBuffer);
  if (!(iss >> command >> id >> xv >> yv >> av) ||
      (command.find(CMD_MOVE) == string::npos) || (mSessionID != id)) {
    stringstream oss;
    oss << CMD_ERROR << " " << CMD_MOVE << " failed: invalid arguments";
    write(oss);
    if (ROBOT_DEBUG) cerr << signature << " - failure; next state: STATE_IDLE" << endl;
    do_state_change(STATE_IDLE);
    return;
  }
  
  // Send the command to Player.
  try {
    // a bad hack to turn robot in discrete intervals to prevent disorienting the user
    // due to lag in communication. Also checks to see if the current speed is already set. 
    // no need to send the same command twice
    if ( av != 0 ){ 
      // if yawSpeed not 0 turn for 22.5 degrees 
      mPosition2D->ResetOdometry();
      mPosition2D->GoTo(0, 0, av < 0 ? -M_PI / 8 : M_PI / 8); 
    }
    else {
      // set xspeed
      cout << "xSpeed: " << mPosition2D->GetXSpeed() << ", ySpeed: " << mPosition2D->GetYSpeed() 
	   << ", xv: " << xv << ", yv: " << yv << endl;
      if ( !(xv == mPosition2D->GetXSpeed() &&  mPosition2D->GetYSpeed() ))
	mPosition2D->SetSpeed(xv, yv, 0);
      else
	cout << signature << " redundant " << command << " command. Ignoring message" << endl;
    }
     
    // it is supposed to be this way
    //mPosition2D->SetSpeed(xv, yv, av);
  } catch (PlayerError) {
    stringstream oss;
    oss << CMD_ERROR << " " << CMD_MOVE << " failed: Player error";
    write(oss);
    if (ROBOT_DEBUG) cerr << signature << " - failure; next state: STATE_IDLE" << endl;
    do_state_change(STATE_IDLE);
    return;
  }
  
  // Report success.
  stringstream oss;
  oss << CMD_MOVING;
  if (write(oss)) {
    if (ROBOT_DEBUG) cerr << signature << " - success; next state: STATE_IDLE" << endl;
  } else {
    if (ROBOT_DEBUG) cerr << signature << " - failure; next state: STATE_IDLE" << endl;
  }
  do_state_change(STATE_IDLE);
}

void Robot::do_state_action_found(){
  // Prepend function signature to error messages.
  static const string signature = "Robot::do_state_action_found()";
  
  if ( itl->isFound() ) {
    stringstream oss; 
    oss << CMD_FOUND << " FOUND green " << mSessionID ;
    if (write(oss)) {
      if (ROBOT_DEBUG) cerr << signature << " - success; next state: STATE_IDLE" << endl;
    }
    else{
      if (ROBOT_DEBUG) cerr << signature << " - failure; next state: STATE_IDLE" << endl;
    }
  }
  do_state_change(STATE_IDLE); 
}

void Robot::do_state_action_pose()
{
  // Prepend function signature to error messages.
  static const string signature = "Robot::do_state_action_pose()";

  // Make sure that we're providing Position2D.
  if (!mPosition2D) {
    stringstream oss;
    oss << CMD_ERROR << " " << CMD_ASKPOSE << " failed: not providing " << CAPS_POSITION2D;
    write(oss);
    if (ROBOT_DEBUG) cerr << signature << " - failure; next state: STATE_IDLE" << endl;
    do_state_change(STATE_IDLE);
    return;
  }
  
  // Grab the pose data from the //Player server.
  // Not anymore it gets it directly from InterfaceToLocalization
  double xp = 0, yp = 0, ap = 0, confidence = 0;
  try {
    /*xp = mPosition2D->GetXPos();
    yp = mPosition2D->GetYPos();
    ap = mPosition2D->GetYaw();
    */
    Position p = itl->getPosition();  
    double conf = itl->getConfidence();
    xp = p.getX(); 
    yp = p.getY(); 
    ap = p.getTheta();
    // TODO: fill in confidence value.
  } catch (PlayerError) {
    stringstream oss;
    oss << CMD_ERROR << " " << CMD_ASKPOSE << " failed: Player error";
    write(oss);
    if (ROBOT_DEBUG) cerr << signature << " - failure; next state: STATE_IDLE" << endl;
    do_state_change(STATE_IDLE);
    return;
  }
  
  // Send the pose data to the Central server.
  stringstream oss;
  oss << CMD_POSE << " " << mSessionID
      << " "<< xp << " " << yp << " " << ap << " " << confidence;
  if (write(oss)) {
    if (ROBOT_DEBUG) cerr << signature << " - success; next state: STATE_IDLE" << endl;
  } else {
    if (ROBOT_DEBUG) cerr << signature << " - failure; next state: STATE_IDLE" << endl;
  }
  do_state_change(STATE_IDLE);
}

void Robot::do_state_action_player()
{
  // Prepend function signature to error messages.
  static const string signature = "Robot::do_state_action_player()";
  
  // Make sure that we have the correct arguments.
  string command;
  uint32_t id = -1;
  uint32_t guiid = -1; 
  stringstream iss(mStringBuffer);
  if (!(iss >> command >> id >> guiid) || (command.find(CMD_ASKPLAYER) == string::npos)) {
    stringstream oss;
    oss << CMD_ERROR << " " << CMD_ASKPLAYER << " failed: invalid arguments";
    write(oss);
    if (ROBOT_DEBUG) cerr << signature << " - failure; next state: STATE_IDLE" << endl;
    do_state_change(STATE_IDLE);
    return;
  }
  
  // Grab the port number and ip address of the Player server.
  uint32_t port;
  string ip;
  try {
    port = mPlayerClient.GetPort();
    // sends localhost
    ip = mPlayerClient.GetHostname();
  } catch (PlayerError) {
    stringstream oss;
    oss << CMD_ERROR << " " << CMD_ASKPLAYER << " failed: Player error";
    write(oss);
    if (ROBOT_DEBUG) cerr << signature << " - failure; next state: STATE_IDLE" << endl;
    do_state_change(STATE_IDLE);
    return;
  }
  
  // Send the Player data to the Central server.
  stringstream oss;
  oss << CMD_PLAYER << " " << guiid << " " << mSessionID << " "<< ip << " " << port;
  if (write(oss)) {
    if (ROBOT_DEBUG) cerr << signature << " - success; next state: STATE_IDLE" << endl;
  } else {
    if (ROBOT_DEBUG) cerr << signature << " - failure; next state: STATE_IDLE" << endl;
  }
  do_state_change(STATE_IDLE);
}
