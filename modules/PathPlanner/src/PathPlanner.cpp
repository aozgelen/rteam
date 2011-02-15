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
    printPath();
    //head = path.begin();
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
    iter--;

    cout << "source: "; 
    source.printNode(); 
    cout << " - first: " ; 
    first.printNode(); 
    cout << " - second: " ; 
    second.printNode(); 
    cout << endl ;
    
    if ( !navGraph.isPathObstructed(source.getX(), source.getY(), second.getX(), second.getY()) &&
	 get_euclidian_distance( source.getX(), source.getY(), second.getX(), second.getY() ) + proximity / 4 < 
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
    iter++;

    cout << "onebeforelast: "; 
    onebeforelast.printNode(); 
    cout << " - last: " ; 
    last.printNode(); 
    cout << " - target: " ; 
    target.printNode(); 
    cout << endl;

    if ( !navGraph.isPathObstructed(onebeforelast.getX(), onebeforelast.getY(), target.getX(), target.getY()) &&
	 get_euclidian_distance( onebeforelast.getX(), onebeforelast.getY(), target.getX(), target.getY() ) + proximity / 4 < 
	 ( get_euclidian_distance( onebeforelast.getX(), onebeforelast.getY(), last.getX(), last.getY() ) + 
	   get_euclidian_distance( last.getX(), last.getY(), target.getX(), target.getY() ) )){
      cout << "Erasing last" << endl ;
      path.erase(iter);
    }
  }

  // remove points which are unnecessary deviations from main path ( by products of astar )
  // TODO: test - seg fault
  /*
  if ( path.size() > 2 ) {
    list<int>::iterator end, head, proc, proc_end;
    Node nH, nE, nP, nPE; 
    end = path.end();
    nE = navGraph.getNode(*(--end)); 
    head = path.begin(); 
    proc = ++head; 
    head--; 
    proc_end = ++proc; 
    proc--; 

    while ( proc != proc_end ){
      if ( !navGraph.isPathObstructed(nH.getX(), nH.getY(), nPE.getX(), nPE.getY()) &&
	   get_euclidian_distance( nH.getX(), nH.getY(), nPE.getX(), nPE.getY() ) < 
	   ( get_euclidian_distance( nH.getX(), nH.getY(), nP.getX(), nP.getY() ) + 
	     get_euclidian_distance( nP.getX(), nP.getY(), nPE.getX(), nPE.getY() ) )){
	path.erase(proc);
      }
      else {
	head = proc; 
	proc++; 
      }
      if ( *proc_end != *end ) 
	proc_end++ ;
    }
  }
  
  // remove points which don't signify direction change. this is done for path segments of length < 1m.
  // TODO: test 
  if ( path.size() > 2 ) {
    list<int>::iterator end, head, proc, proc_end;
    Node nH, nE, nP, nPE; 
    end = path.end();
    nE = navGraph.getNode(*(--end)); 
    head = path.begin(); 
    proc = ++head; 
    head--; 
    proc_end = ++proc; 
    proc--; 
    
    while( proc != proc_end ){
      nH = navGraph.getNode(*head); 
      nP = navGraph.getNode(*proc); 
      nPE = navGraph.getNode(*proc_end);
      if ( get_euclidian_distance( nH.getX(), nH.getY(), nPE.getX(), nPE.getY() ) < 100 &&
	   ( nH.getX() - nP.getX() == nP.getX() - nPE.getX() && nH.getY() - nP.getY() == nP.getY() - nPE.getY() )) {
	path.erase(proc); 
      }
      else{
	head = proc; 
	proc++; 
      } 	
      if ( *proc_end != *end ) 
	proc_end++;
    }
    }*/

  cout << "after smoothing: " << endl ; 
  printPath();
}

void PathPlanner::printPath(){
  list<int>::iterator it ;
  for ( it = path.begin(); it != path.end(); it++ ){
    navGraph.getNode(*it).printNode() ; 
    cout << endl;
  }
}
