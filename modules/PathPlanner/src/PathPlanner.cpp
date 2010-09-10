#include "PathPlanner.h"
#include <limits.h>

void PathPlanner::calcPath(){
  //cout << "in calcPath." << endl;
  source.printNode();
  cout << endl; 
  target.printNode();
  cout << endl;
  if ( source.getID() != Node::invalid_node_index && target.getID() != Node::invalid_node_index ){
    //cout << "calculating path from (" << source.getX() << "," << source.getY() << ") to (" 
    //	 << target.getX() << "," << target.getY() << ")." << endl;
    Node s, t;
    if ( navGraph.isNode(source) )
      s = source ; 
    else {
      //cout << "source is not a node in the graph" << endl;
      s = getClosestNode(source); 
      //cout << "closest node to the source is : " ; 
      //s.printNode(); 
      //cout << endl;
    }

    if ( navGraph.isNode(target) )
      t = target ; 
    else {
      //cout << "target is not a node in the graph" << endl;
      t = getClosestNode(target);
      //cout << "closest node to the target is : " ; 
      //t.printNode(); 
      //cout << endl;
    }

    astar newsearch(navGraph, s, t, 1);
    //cout << "well target found!" << endl;
    path = newsearch.getPathToTarget();
    newsearch.printPath();
    pathCost = newsearch.getCostToTarget();
    cout << "cost of path: " << pathCost << endl;
    
    // add original source and target to list
    /* if ( path.front() != source.getID() )  
      path.push_front(source.getID()); 
    if ( path.back() != target.getID() ) 
      path.push_back(target.getID());
    */

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
  //cout << "smoothing the path" << endl;
  
}
