#ifndef BEHAVIOR_TESTER_H
#define BEHAVIOR_TESTER_H

#include "robot.h"
#include "behavior.h"

class BehaviorTester {
public:
  BehaviorTester(Robot * r, PlayerCc::PlayerClient * p): rbt(r), pCli(p){}  
  void operator()();
private:
  Robot * rbt;
  PlayerCc::PlayerClient * pCli; 
};

#endif /* BEHAVIOR_TESTER_H */
