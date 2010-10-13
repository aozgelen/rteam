#ifndef PATH_PLANNER_H
#define PATH_PLANNER_H

#include "astar.h"

class PathPlanner {
private: 
  Graph navGraph; 
  Node source, target; 
  list<int> path; 
  double pathCost;
  
  void smoothPath();
  Node getClosestNode(Node);
public: 
  PathPlanner(Graph g, Node s, Node t): navGraph(g), source(s), target(t){}
  void calcPath(); 
  list<int> getPath(){ return path; }
  Graph* getGraph(){ return &navGraph; }
  Node getSource(){ return source; }
  void setSource(Node s){ source = s; } 
  Node getTarget(){ return target; }
  void setTarget(Node t){ target = t; } 
};

#endif
