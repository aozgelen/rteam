#ifndef PATH_PLANNER_H
#define PATH_PLANNER_H

#include "astar.h"

class PathPlanner {
private: 
  Graph navGraph; 
  Node source, target; 
  list<int> path; 
  double pathCost;

  //list<int>::iterator head;
  Node waypoint; 
  bool objectiveSet;
  bool pathCompleted;

  void smoothPath();
  Node getClosestNode(Node);

  void printPath();
public: 
  PathPlanner(Graph g, Node s, Node t): navGraph(g), source(s), target(t){}
  void calcPath(); 
  list<int> getPath(){ return path; }
  void resetPath() { path.clear(); }
  Graph* getGraph(){ return &navGraph; }
  Node getSource(){ return source; }
  void setSource(Node s){ source = s; } 
  Node getTarget(){ return target; }
  void setTarget(Node t){ target = t; } 
  bool pathEmpty() { return path.empty() ; }
  
  Node getWaypoint() { 
    Node wp ; 
    ( !pathEmpty() ) ? wp = navGraph.getNode(path.front()) : wp = target; 
    return wp; 
  } 
  bool isObjectiveSet() { return objectiveSet; }
  void waypointReached() {
    if ( !pathEmpty() )
      path.pop_front(); 
    else 
      pathCompleted = true ; 
    objectiveSet = false;
  }
  void waypointSet() {
    objectiveSet = true; 
    waypoint = navGraph.getNode(path.front());
  }
  bool isPathCompleted() { 
    return pathCompleted;
  }


  /*
  Node getWaypoint() { return navGraph.getNode(*head); } 
  bool isObjectiveSet() { return objectiveSet; }
  void waypointReached() {
    head++; 
    objectiveSet = false;
  }
  void waypointSet() {
    objectiveSet = true; 
    waypoint = navGraph.getNode(*head);
  }
  bool pathCompleted() { 
    return ( navGraph.getNode(*head) == target );
    }*/
};

#endif
