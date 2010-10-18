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

  /*
  void setState(int i){
    cout << "\tsetting state to " << i << endl;
    *currState = i;
  }
  
  int getState() { return *currState; }
  */
  void stop(){ itl->setSpeed(0,0,0); }
  void setOpMode(Mode m) { 
    cout << "setting opMode: " << m << endl;
    opMode = m; 
  }

  PathPlanner * getPlanner() { return planner; }
  InterfaceToLocalization * getInterfaceToLocalization() { return itl; }
  Graph * getNavgraph() { return navGraph; }
  Robot * getRobot() { return robot; }
  Node getWaypoint() { return waypoint; }

  void operator()();
private:
  //int * currState; 
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
  bool isGoalReached(Node);
  bool isTargetSet();   

  // behavior vars
  Position prevPos, currPos; 
  Node waypoint; 
};

#endif /* CONTROLLER_H */
