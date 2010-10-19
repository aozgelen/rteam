/*
 * Controller.cpp
 *
 *  Created on: Aug 30, 2010
 *      Author: tuna ozgelen
 */

#include "Controller.h"
#include <iostream> 
using namespace std; 

Controller::Controller(PlayerClient * pc, Robot * r, Map * m, InterfaceToLocalization * rbt) 
  : pCli(pc), robot(r), localMap(m), itl(rbt)
{
  // generate navigation graph
  navGraph = new Graph(localMap, true, 30);		
  
  // initialize path planner with the navgraph. 
  Node n; 
  planner = new PathPlanner(*navGraph,n,n); 
  waypoint = n;

  setOpMode(MANUAL);
}

void Controller::operator()() {
  // enter main loop
  //cout << "\n\nTHREAD::CONTROLLER> " << endl; 
  while (robot->GetState() != STATE_QUIT  ) {
    //cout << "NEW UPDATE CYCLE" << endl; // > current state: "  << endl; 
    // Update the robot.
    robot->Update();

    // Update behavior.
    updateBehavior();

    // Take a quick breath.
    usleep(100000);
    //cout << "END UPDATE CYCLE " << endl << endl;
  }
}

void Controller::updateBehavior() {
  bool moving = itl->isMoving();
  bool obstacleInPath = false;

  if (!( moving && obstacleInPath )){
    //cout << "the robot doesn't need to avoid collision. updating behavior opMode:" << opMode << endl;
    switch( opMode ){
    case MANUAL:
      //cout << "manual" << endl;
      updateManualBehavior(); 
      break; 
    case MIXED_INIT:
      //cout << "mixed init " << endl;
      updateMixedInitBehavior(); 
      break; 
    case AUTO: 
      //cout << "auto" << endl;
      updateAutoBehavior();
      break;
    }
  }
  else {
    stop();
    // avoid obstacles
  }
}

void Controller::updateManualBehavior(){ 
  updateMixedInitBehavior() ; 
}

/* this is the main control behavior function */
void Controller::updateMixedInitBehavior() {
  string label = "\tController::updateBehavior()> " ;  
  if ( isTargetSet() ) {
    //cout << label << "target set. updating position info" << endl; 
    prevPos = currPos; 
    currPos = itl->getPosition();
    cout << label << "currPos(" << currPos.getX() << "," << currPos.getY() << "," 
	 << currPos.getTheta() << ") and confidence: " << itl->getConfidence() << endl; 
    if ( !isGoalReached(planner->getTarget()) ) {
      //cout << label << "far away from target" << endl;
      /*if ( !isPlanValid() ){
	//cout << label << "plan is not valid. recalculating path" << endl;
	// this can only happen if there is a sudden change in position estimate. Updating the
	// starting point is necessary to get a new path
	Node n(1, currPos.getX(), currPos.getY());
	//cout << label << "new source node: " ;
	n.printNode(); 
	cout << endl; 
	planner->setSource(n); 
	planner->calcPath();
	} */ 
      
      //cout << label << "plan seems to be valid" << endl;
      // if not chosen yet or irrelevant, choose the immediate goal point from path
      list<int> pathNodeIDs = planner->getPath();
      list<int>::iterator iter; 
      if ( waypoint.getID() == Node::invalid_node_index ) {
	//cout << label << "waypoint not set. selecting one." << endl;
	for( iter = pathNodeIDs.begin(); iter != pathNodeIDs.end(); iter++ ){
	  // check if there is a direct path between currPos and this node
	  if ( !navGraph->isPathObstructed( currPos.getX(), currPos.getY(), 
					    navGraph->getNode(*iter).getX(), navGraph->getNode(*iter).getY() ) ){
	    waypoint = navGraph->getNode(*iter); 
	    waypoint.printNode();
	    break; 
	  }
	}
      }
      
      /*if ( isGoalReached(waypoint) ) {
	Node n; 
	waypoint = n;
	for ( iter = pathNodeIDs.begin(); iter != pathNodeIDs.end(); iter++ ){
	  if ( navGraph->getNode(*iter).getID() == waypoint.getID() ){
	    iter++ ; 
	    if ( iter != pathNodeIDs.end() )
	      // check if there is a direct path between currPos and this node
	      if ( !navGraph->isPathObstructed( currPos.getX(), currPos.getY(), 
						navGraph->getNode(*iter).getX(), navGraph->getNode(*iter).getY() ) ){
		waypoint = *iter;
		break; 
	      }
	  }
	}
	}*/
      
      double xdiff = currPos.getX() - planner->getTarget().getX();
      double ydiff = currPos.getY() - planner->getTarget().getY();
      
      //calculate relative position to destination
      Position dest(currPos.getX() - wp.getX(), 
		    currPos.getY() - wp.getY(),
		    0);
      // move to target      
      itl->move(dest); 
      usleep(10000); 
    }
    else {
      // target reached remove target from path
      cout << label << "target reached. setting planner target to a null-index node" << endl;
      Node n; 
      planner->setTarget(n);
      //waypoint = n;
    }
  }
}

void Controller::updateAutoBehavior(){}

// true if there is a plan and location info and the plan still makes sense. 
// false if plan is null and position estimate makes a sudden jump. 
// the threshold for jump is rather high, this is to compensate for localization errors
// during motion
bool Controller::isPlanValid(){
  double d = get_euclidian_distance( currPos.getX(), currPos.getY(), prevPos.getX(), prevPos.getY() ) ;
  return (( d < 75 ) && !planner->pathEmpty()); 
}

// true if the position estimate is rather good (?) and the robot is in the same vicinity (ft 2) with the goal point
// used both for waypoints and target point 
bool Controller::isGoalReached(Node g){
  if ( g.getID() != Node::invalid_node_index ){
    double d = get_euclidian_distance( currPos.getX(), currPos.getY(), g.getX(), g.getY() ); 
    return ( d < 30 ); 
  }
  else
    return false;
}

bool Controller::isTargetSet(){
  return ( planner->getTarget().getID() != Node::invalid_node_index ) ;
}
