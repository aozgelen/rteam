/*
 * Controller.cpp
 *
 *  Created on: Aug 30, 2010
 *      Author: robotics
 */

#include "Controller.h"
#include <iostream> 
using namespace std; 

void Controller::operator()() {
  // enter main loop
  while (rbt->GetState() != STATE_QUIT) {
    cout << "CONTROLLER THREAD: updating state" << endl;

    // Update the robot.
    rbt->Update();

    // Take a quick breath.
    usleep(100000);
  }
}
