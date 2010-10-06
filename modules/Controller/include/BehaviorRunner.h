#ifndef BEHAVIOR_RUNNER_H
#define BEHAVIOR_RUNNER_H

#include "robot.h"
#include "behavior.h"

class BehaviorRunner {
public:
  BehaviorRunner(Robot * r, PlayerCc::PlayerClient * p): rbt(r), pCli(p){}  
  void operator()();
private:
  Robot * rbt;
  PlayerCc::PlayerClient * pCli; 
};

#endif /* BEHAVIOR_RUNNER_H */
