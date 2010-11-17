#include "PathPlanner.h"
#include <limits.h>

void PathPlanner::calcPath(){
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

    astar newsearch(navGraph, s, t, 1);
    path = newsearch.getPathToTarget();
    newsearch.printPath();
    head = path.begin();
    objectiveSet = false;
    pathCompleted = false;
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
  int proximity = navGraph.getProximity();

  if ( path.size() > 1 ) {
    // smooth the end points 
    // if getting to second node from source is shorter and not obstructed remove first node. 
    list<int>::iterator iter = path.begin(); 
    Node first = navGraph.getNode(*iter++);
    Node second = navGraph.getNode(*iter);

    cout << "source: "; 
    source.printNode(); 
    cout << " - first: " ; 
    first.printNode(); 
    cout << " - second: " ; 
    second.printNode(); 
    cout << endl ;
    
    if ( !navGraph.isPathObstructed(source.getX(), source.getY(), second.getX(), second.getY()) &&
	 get_euclidian_distance( source.getX(), source.getY(), second.getX(), second.getY() ) + proximity / 2 < 
	 ( get_euclidian_distance( source.getX(), source.getY(), first.getX(), first.getY() ) + 
	   get_euclidian_distance( first.getX(), first.getY(), second.getX(), second.getY() ) )){
      cout << "Erasing first" << endl;
      path.erase(iter);
    }
  }

  if ( path.size() > 1 ) {
    // if getting to second node from source is shorter and not obstructed remove first node. 
    list<int>::iterator iter = path.end(); 
    Node last = navGraph.getNode(*(--iter)); 
    Node onebeforelast = navGraph.getNode(*(--iter)); 

    cout << "onebeforelast: "; 
    onebeforelast.printNode(); 
    cout << " - last: " ; 
    last.printNode(); 
    cout << " - target: " ; 
    target.printNode(); 
    cout << endl;

    if ( !navGraph.isPathObstructed(onebeforelast.getX(), onebeforelast.getY(), target.getX(), target.getY()) &&
	 get_euclidian_distance( onebeforelast.getX(), onebeforelast.getY(), target.getX(), target.getY() ) + proximity / 2 < 
	 ( get_euclidian_distance( onebeforelast.getX(), onebeforelast.getY(), last.getX(), last.getY() ) + 
	   get_euclidian_distance( last.getX(), last.getY(), target.getX(), target.getY() ) )){
      cout << "Erasing last" << endl ;
      path.erase(iter);
    }
  }
  /*
  // erase nodes points, that are intermediate points on a segment of path
  if ( path.size() > 3 ) {
    list<int>::iterator start = path.begin(), head = path.begin(); 
    head++;
    while ( start != head ) {
      Node nS = navGraph.getNode(*start); 
      for ( ; head != path.end() ; head++ ){
	Node nH = navGraph.getNode(*head);
	Node nHnext = navGraph.getNode(*(head++));
	head--;
	if ( nS.getX() - nH.getX() == nH.getX() - nHnext.getX() &&
	     nS.getY() - nH.getY() == nH.getY() - nHnext.getY()){
	  path.erase(head);
	}
	else{
	  head++;
	  break;
	} 
	if ( get_euclidian_distance(start->getX(), start->getY(), head->getX(), head->getY()) > 3 * proximity ){
	  head++;
	  break;
	}
      }
      start++;
    }
  }
  */
  // erase points that are defects of path planning algorithm 
  cout << "after smoothing: " << endl ; 
  list<int>::iterator it ;
  for ( it = path.begin(); it != path.end(); it++ ){
    navGraph.getNode(*it).printNode() ; 
    cout << endl;
  }
}
