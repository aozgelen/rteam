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
  while (robot->GetState() != STATE_QUIT) {
    // Update the robot.
    robot->Update();

    // Update behavior.
    updateBehavior();

    // Take a quick breath.
    usleep(100000);
  }
}

/* this is the main control behavior function */
void Controller::updateBehavior() {
  if ( isTargetSet() ) {
    cout << "target set" << endl; 
    if ( !isTargetReached() ) {
      cout << "far away from target" << endl;
      if ( isPlanValid() ){
	cout << "plan seems to be valid" << endl;
	// choose the immediate goal point from path
      }
      else {
	cout << "plan is not valid. recalculating path" << endl;
	// this can only happen if there is a sudden change in position estimate. Updating the
	// starting point is necessary to get a new path
	Position p = itl->getPosition();
	Node n(1, p.getX(), p.getY());
	cout << "new source node: " ;
	n.printNode(); 
	planner->setSource(n); 
	planner->calcPath();
      }  
    }
  }
}

// true if there is a plan and location info and the plan still makes sense. 
// false if plan is null and position estimate makes a sudden jump.
bool Controller::isPlanValid(){
  return true; 
}

// true if the position estimate is rather good (?) and the robot is in the same vicinity (ft 2) with the goal point
bool Controller::isTargetReached(){
  return false; 
}

bool Controller::isTargetSet(){
  return ( planner->getTarget().getID() != Node::invalid_node_index ) ;
}
