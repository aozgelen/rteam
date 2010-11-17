#ifndef CONTROLLER_H
#define CONTROLLER_H

#include "definitions.h"
#include "robot.h"
#include "InterfaceToLocalization.h"
#include "PathPlanner.h"
#include "libplayerc++/playerc++.h"
using namespace PlayerCc;

class Controller {
public:
  Controller(PlayerClient*, Robot*, Map*, InterfaceToLocalization*) ;
  ~Controller(){ 
    stop(); 
  }

  void stop(){ itl->setSpeed(0,0,0); }
  void setOpMode(Mode m) { 
    opMode = m; 
  }
  void resetPathInfo();

  PathPlanner * getPlanner() { return planner; }
  InterfaceToLocalization * getInterfaceToLocalization() { return itl; }
  Graph * getNavgraph() { return navGraph; }
  Robot * getRobot() { return robot; }
  Node getWaypoint() { return waypoint; }

  void operator()();
private:
  Mode opMode;

  PlayerClient * pCli; 
  Robot * robot;
  InterfaceToLocalization * itl ; 
  Graph * navGraph; 
  PathPlanner * planner;
  Map * localMap; 

  void updateBehavior();
  void updateManualBehavior();
  void updateMixedInitBehavior();
  void updateAutoBehavior();
  bool isPlanValid(); 
  bool isTargetSet();   
  void localize();

  // behavior vars
  Position prevPos, currPos; 
  Node waypoint; 
};

#endif /* CONTROLLER_H */
