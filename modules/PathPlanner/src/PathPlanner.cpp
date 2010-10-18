#include "PathPlanner.h"
#include <limits.h>

void PathPlanner::calcPath(){
  //cout << "in calcPath." << endl;
  //source.printNode();
  //cout << endl; 
  //target.printNode();
  //cout << endl;
  if ( source.getID() != Node::invalid_node_index && target.getID() != Node::invalid_node_index ){
    Node s, t;
    if ( navGraph.isNode(source) )
      s = source ; 
    else {
      s = getClosestNode(source); 
    }

    if ( navGraph.isNode(target) )
      t = target ; 
    else {
      t = getClosestNode(target);
    }

    //astar newsearch(navGraph, s, t, 1);
    //path = newsearch.getPathToTarget();
    path.push_back(t.getID()); 
    //newsearch.printPath();
    //pathCost = newsearch.getCostToTarget();
    
    smoothPath(); 
  }
}

Node PathPlanner::getClosestNode(Node n){
  vector<Node> nodes = navGraph.getNodes(); 
  vector<Node>::iterator iter; 
  double dist = INT_MAX;
  Node temp; 
  for( iter = nodes.begin(); iter != nodes.end(); iter++ ){
    double d = get_euclidian_distance(iter->getX(), iter->getY(), n.getX(), n.getY() );
    if ( d < dist ) {
      dist = d; 
      temp = *iter; 
    }
  }
  return temp;
}

void PathPlanner::smoothPath(){
  
}
