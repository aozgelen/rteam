#ifndef ASTAR_H
#define ASTAR_H

#include "Graph.h"
#include "IPQ.h"
#include <list>

class astar {
private: 
  Graph searchGraph;
  int source; 
  int target; 

  int neighborhoodLevel; 

  vector<double> g_cost; 
  vector<double> f_cost; 

  vector<Edge> shortestPathTree; 
  vector<Edge> searchFrontier; 
  
  int neighborhoodHeuristic(Node, int);
public: 
  astar(Graph&, Node, Node, int);
  void search(); 
  list<int> getPathToTarget(); 
  double getCostToTarget();
  void printPath(); 
}; 

#endif
