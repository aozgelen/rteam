#ifndef CONTROLLER_H
#define CONTROLLER_H

#include "robot.h"
#include "InterfaceToLocalization.h"
#include "PathPlanner.h"
#include "libplayerc++/playerc++.h"
using namespace PlayerCc;

class Controller {
public:
  //Controller(Robot * r): rbt(r){}  
  Controller(PlayerClient * pc, Robot * r, Map * m, 
	     InterfaceToLocalization * rbt, PathPlanner * pl, Graph * g) 
    : pCli(pc), robot(r), localMap(m), itl(rbt), planner(pl), navGraph(g) {}
  
  void operator()();
private:
  PlayerClient * pCli; 
  Robot * robot;
  InterfaceToLocalization * itl ; 
  Graph * navGraph; 
  PathPlanner * planner;
  Map * localMap; 

  void updateBehavior();
  bool isPlanValid(); 
  bool isTargetReached();
  bool isTargetSet();   
};

#endif /* CONTROLLER_H */
