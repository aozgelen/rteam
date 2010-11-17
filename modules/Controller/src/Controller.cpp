/*
 * Controller.cpp
 *
 */

#include "Controller.h"
#include <iostream> 
using namespace std; 

Controller::Controller(PlayerClient * pc, Robot * r, Map * m, InterfaceToLocalization * rbt) 
  : pCli(pc), robot(r), localMap(m), itl(rbt), opMode(MANUAL)
{
  // generate navigation graph
  navGraph = new Graph(localMap, true, 30);		
  
  // initialize path planner with the navgraph. 
  Node n; 
  planner = new PathPlanner(*navGraph,n,n); 
  //waypoint = n;

  robot->SetPlanner(planner);
  //setOpMode(MANUAL);
}

void Controller::operator()() {
  // enter main loop
  while (robot->GetState() != STATE_QUIT  ) {
    // Update the robot.
    robot->Update();

    // Update behavior.
    updateBehavior();

    // Take a quick breath.
    usleep(100000);
  }
}

void Controller::updateBehavior() {
  //bool moving = itl->isMoving();
  //bool obstacleInPath = false;

  //if (!( moving && obstacleInPath )){
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
    //}
    //else {
    //stop();
    // avoid obstacles
    //}
}

void Controller::updateManualBehavior(){ 
  if ( isTargetSet() ) {
    if ( !itl->isDestinationSet() ){
      if ( !planner->pathEmpty() ) {
	cout << "target is set but destination ( first waypoint ) is not passed on to itl" << endl;
	Node waypoint = planner->getWaypoint(); 
	cout << "New waypoint: " ; 
	waypoint.printNode();
	cout << endl; 
	itl->moveToMapPosition( planner->getWaypoint().getX(), planner->getWaypoint().getY() );
	planner->waypointSet(); 
      }
    }
  }
  
  if ( itl->isDestinationReached() ){
    if ( isTargetSet() ) {
      if ( planner->isObjectiveSet() ){
	planner->waypointReached(); 
	cout << "waypoint reached" << endl;
	itl->resetDestinationInfo();
	if ( planner->isPathCompleted() ) {
	  cout << "target reached" << endl;
	  resetPathInfo();
	}
	//localize();
      }
    }
    else {
      cout << "command completed" << endl; 
      itl->resetDestinationInfo();
    }
  }
}

void Controller::localize() {
  for( int i = 0; i < 16 ; i++ ){
    itl->move(Position(0, 0, Utils::toRadians(22.5)));
    while( !itl->isDestinationReached() )
      usleep(100000); 
  }
}

void Controller::resetPathInfo() {
  Node n; 
  planner->setTarget(n);
  planner->setSource(n);
  planner->resetPath();
  itl->resetDestinationInfo();
}

/* this is the main control behavior function */
void Controller::updateMixedInitBehavior() {
  /*  string label = "\tController::updateBehavior()> " ;  
  if ( isTargetSet() ) {
    //cout << label << "target set. updating position info" << endl; 
    prevPos = currPos; 
    currPos = itl->getPosition();
    cout << label << "currPos(" 
	 << currPos.getX() << ","
	 << currPos.getY() << "," 
	 << currPos.getTheta() 
	 << ") and confidence: " << itl->getConfidence() << endl; 
    if ( !isGoalReached(planner->getTarget()) ) {
      //cout << label << "far away from target" << endl;
      if ( !isPlanValid() ){
	//cout << label << "plan is not valid. recalculating path" << endl;
	// this can only happen if there is a sudden change in position estimate. Updating the
	// starting point is necessary to get a new path
	Node n(1, currPos.getX(), currPos.getY());
	//cout << label << "new source node: " ;
	n.printNode(); 
	cout << endl; 
	planner->setSource(n); 
	planner->calcPath();
      } 
      
      cout << label << "plan seems to be valid" << endl;
      // if not chosen yet or irrelevant, choose the immediate goal point from path
      list<int> pathNodeIDs = planner->getPath();
      list<int>::iterator iter; 
      if ( waypoint.getID() == Node::invalid_node_index ) {
	cout << label << "waypoint not set. selecting one." << endl;
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
      
      if ( isGoalReached(waypoint) ) {
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
      }
      
      double xdiff = currPos.getX() - planner->getTarget().getX();
      double ydiff = currPos.getY() - planner->getTarget().getY();
      
      //calculate relative position to destination
      Position dest(currPos.getX() - waypoint.getX(), 
		    currPos.getY() - waypoint.getY(),
		    0);
      // move to target      
      itl->move(dest); 
      usleep(10000); 
    }
    else {
      // target reached remove target from path
      cout << label << "target reached. setting planner target to a null-index node" << endl;
      resetPathInfo();
    }
  }
  */
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

bool Controller::isTargetSet(){
  return ( planner->getTarget().getID() != Node::invalid_node_index ) ;
}
