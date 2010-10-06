/*
 * BehaviorRunner.cpp
 *
 *  Created on: Aug 30, 2010
 *      Author: robotics
 */

#include "BehaviorRunner.h"
#include <iostream> 
using namespace std; 

void BehaviorRunner::operator()() {
  // enter main loop
  while (rbt->GetState() != STATE_QUIT) {

    // Update Player interfaces.
    pCli->ReadIfWaiting();
    
    // Update the robot.
    rbt->Update();
      
    // Take a quick breath.
    usleep(1);
  }
}