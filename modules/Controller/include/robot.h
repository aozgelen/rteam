#ifndef _ROBOT_H
#define _ROBOT_H

#include "definitions.h"
#include "behavior.h"
#include "metrobotics.h"
#include "InterfaceToLocalization.h"
#include "libplayerc++/playerc++.h"
#include "boost/asio.hpp"
#include "boost/shared_ptr.hpp"
#include <stdint.h>
#include <stdlib.h>

using namespace metrobotics; 

class Robot
{
 public:
  Robot(PlayerCc::PlayerClient& pc, InterfaceToLocalization * i, string, string, Behavior* bp = 0);
  ~Robot();

  // State management.
  int  GetState() const { return mCurrentState; }
  bool IsRegistered() const { return mSessionID >= 0; }
  bool IsLocked() const { return mPossessed; }
  
  // Connect to the central server.
  bool Connect(const std::string& hostname, unsigned short port);
  void Disconnect();
		
  // Heart beat of the robot unit;
  // Updates and maintains the internal state machine.
  void Update();
		
  // Select robot behavior.
  void SetBehavior(Behavior* bp) { mBehavior = bp; }
		
 private:
  // Binding to Player server
  PlayerCc::PlayerClient& mPlayerClient;

  // Player client proxies.
  boost::shared_ptr<PlayerCc::Position2dProxy> mPosition2D;

  // Boost ASIO (for sockets)
  boost::asio::io_service mIOService;
  boost::asio::ip::tcp::socket mSocket;

  // Robot properties.
  std::string mTypeID;
  std::string mNameID;
  std::vector<std::string> mProvidesList;
  
  // Robot behavior.
  Behavior* mBehavior;

  // Interfact to Localization
  InterfaceToLocalization * itl;

  // State properties.
  int  mCurrentState;
  long mSessionID;
  bool mPossessed;

  // Internal timers.
  static const double MAX_TIME_SILENCE = 60.0;
  static const double MAX_TIME_STATE   = 10.0;
  metrobotics::PosixTimer mSilenceTimer;
  metrobotics::PosixTimer mStateTimer;

  // Internal buffers.
  std::string mStringBuffer;

  // Internal functions.
  void init_state();
  void init_provides();
  bool msg_waiting() const;
  bool read(std::stringstream& ss);
  bool write(const std::stringstream& ss);

  // State actions.
  void do_state_change(int state);
  void do_state_action_init();
  void do_state_action_ack();
  void do_state_action_idle();
  void do_state_action_ping_send();
  void do_state_action_pong_read();
  void do_state_action_pong_send();
  void do_state_action_cmd_proc();
  void do_state_action_moving();
  void do_state_action_pose();
  void do_state_action_player();
};

#endif
