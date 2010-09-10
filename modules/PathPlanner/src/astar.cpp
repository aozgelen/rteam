#include <iostream> 
#include <list>
#include <set>
#include "astar.h"
using namespace std; 

astar::astar(Graph& g, Node src, Node trg, int l = 0): 
  searchGraph(g), 
  source(src.getID()), 
  target(trg.getID()),
  g_cost(g.numNodes(), 0.0),
  f_cost(g.numNodes(), 0.0),
  shortestPathTree(g.numNodes()),
  searchFrontier(g.numNodes()),
  neighborhoodLevel(l)
{
  //cout << "starting a-star search" << endl;
  search();
}

void astar::search(){
  IndexedPriorityQLow<double> frontierNodes(f_cost,searchGraph.numNodes()); 

  frontierNodes.insert(source); 

  while( !frontierNodes.empty() ) {
    int nextCloseNode = frontierNodes.Pop();
    shortestPathTree[nextCloseNode] = searchFrontier[nextCloseNode];

    /* debugging. delete when done.
    cout << "nextCloseNode: ";
    searchGraph.getNode(nextCloseNode).printNode(); 
    cout << endl;
    cout << "added to SPT[nextCloseNode] : ";
    searchGraph.printEdge(shortestPathTree[nextCloseNode]);
    cout << endl; 
    */
    if ( nextCloseNode == target ) {
      return ; 
    }
    
    vector<Edge> edges = searchGraph.getEdges(); 
    vector<Edge>::iterator iter; 
    for( iter = edges.begin(); iter != edges.end(); iter++ ){
      if ( iter->getTo() == nextCloseNode || ( iter->getFrom() == nextCloseNode && searchGraph.isdigraph()) ) {
	int nextNode; 
	if ( iter->getTo() == nextCloseNode ){
	  nextNode = iter->getFrom(); 
	  //cout << "To node is the nextCloseNode. assigning nextNode as From." << endl;
	}
	else
	  nextNode = iter->getTo();

	double hCost = get_euclidian_distance( searchGraph.getNode(iter->getTo()).getX(), 
					       searchGraph.getNode(iter->getTo()).getY(), 
					       searchGraph.getNode(target).getX(), 
					       searchGraph.getNode(target).getY() ) 
	  - ( neighborhoodHeuristic(searchGraph.getNode(nextNode), neighborhoodLevel) * (searchGraph.getProximity()) );

	//cout << "num neighbors of nextNode: " << numNeighbors(searchGraph.getNode(nextNode), neighborhoodLevel) << endl; 

	double gCost = g_cost[nextCloseNode] + iter->getCost() ;
	//cout << "calculating edge costs linked to nextCloseNode: " ; 
	//searchGraph.printEdge(*iter); 
	//cout << " hCost=" << hCost << ", gCost=" << gCost << endl;
	
	if ( searchFrontier[iter->getTo()].getTo() == Node::invalid_node_index && iter->getTo() != nextCloseNode ) {
	  //cout << "To node of the edge is not in search frontier, adding." << endl;
	  f_cost[iter->getTo()] = gCost + hCost; 
	  g_cost[iter->getTo()] = gCost; 
	  frontierNodes.insert(iter->getTo());
	  searchFrontier[iter->getTo()] = *iter;
	}
	else if ( gCost < g_cost[iter->getTo()] &&
		  searchFrontier[iter->getTo()].getTo() == Node::invalid_node_index ) {
	  //cout << "To node of the edge is in search frontier, found a shorter path to it." << endl;
	  f_cost[iter->getTo()] = gCost + hCost; 
	  g_cost[iter->getTo()] = gCost; 
	  frontierNodes.ChangePriority(iter->getTo());
	  searchFrontier[iter->getTo()] = *iter;	
	}
	// if the graph is a digraph then From nodes have to be considered as well
	if ( searchGraph.isdigraph() ) {
	  if ( searchFrontier[iter->getFrom()].getFrom() == Node::invalid_node_index && iter->getFrom() != nextCloseNode ) {
	    //cout << "From node of the edge is not in search frontier, adding." << endl;
	    f_cost[iter->getFrom()] = gCost + hCost; 
	    g_cost[iter->getFrom()] = gCost; 
	    frontierNodes.insert(iter->getFrom());
	    searchFrontier[iter->getFrom()] = *iter;
	  }
	  else if ( gCost < g_cost[iter->getFrom()] &&
		    searchFrontier[iter->getFrom()].getFrom() == Node::invalid_node_index ) {
	    //cout << "From node of the edge is in search frontier, found a shorter path to it." << endl;
	    f_cost[iter->getFrom()] = gCost + hCost; 
	    g_cost[iter->getFrom()] = gCost; 
	    frontierNodes.ChangePriority(iter->getFrom());
	    searchFrontier[iter->getFrom()] = *iter;	
	  }
	}
      }
    }
  }
  //cout << "target not found. frontier list is empty." << endl;
}

// this doesn't get exact number of neighbors, some nodes are counted multiple times but
// there is no need to be specific. 
int astar::neighborhoodHeuristic(Node n, int level) {
  /*int num = 0;
  list<Node> nodes; 
  nodes.push_back(n); 
  for ( int i = 0 ; i < level ; i++ ){
    list<Node>::iterator iter; 
    for( iter = nodes.begin(); iter != nodes.end(); iter++ ){
      num += iter->numNeighbors();
      if ( i+1 < level ) {
	vector<Node> newNodes = iter->getNeighbors();
	vector<Node>::iterator it;
	for( it = newNodes.begin(); it != newNodes.end(); it++ )
	  nodes.push_back(*it); 
      }
      nodes.pop_front();
    }
  }
  return num;
  */ 
  //works but very slow
  set<Node> neighbors;
  neighbors.insert(n);
  for ( int i = 0; i < level; i++ ){
    set<Node>::iterator iter; 
    set<Node> newNodes;
    for( iter = neighbors.begin(); iter !=neighbors.end(); iter++ ){
      vector<Node> newn = searchGraph.getNeighbors(*iter);
      vector<Node>::iterator it; 
      for( it = newn.begin(); it != newn.end(); it++ ){
	newNodes.insert(*it); 
      }
    }
    for( iter = newNodes.begin(); iter != newNodes.end(); iter++ )
      neighbors.insert(*iter);
    newNodes.clear(); 
  }
  return neighbors.size();
}

list<int> astar::getPathToTarget(){
  list<int> path; 
  path.push_front(target);
  int parent = target; 
  //cout << "getting path to target" << endl;

  do { 
    if ( parent == shortestPathTree[parent].getFrom() )
      parent = shortestPathTree[parent].getTo();
    else if ( parent == shortestPathTree[parent].getTo() )
      parent = shortestPathTree[parent].getFrom();      
    path.push_front(parent); 
  }while ( parent != source ); 

  //cout << "returning path" << endl;
  return path; 
}
 
double astar::getCostToTarget(){
  double cost = 0; 
  int parent = target; 
  //cout << "getting cost to target" << endl;

  do { 
    cost += shortestPathTree[parent].getCost();
    if ( parent == shortestPathTree[parent].getFrom() )
      parent = shortestPathTree[parent].getTo();
    else if ( parent == shortestPathTree[parent].getTo() )
      parent = shortestPathTree[parent].getFrom();      
  }while ( parent != source ); 
  return cost; 
}

void astar::printPath(){
  cout << "printing path from " ;
  cout << "target: " ;
  searchGraph.getNode(target).printNode() ;
  cout << " to source: " ;
  searchGraph.getNode(source).printNode(); 
  cout << endl;
  
  int parent = target; 
  do {
    searchGraph.printEdge(shortestPathTree[parent]);
    if ( parent == shortestPathTree[parent].getFrom() ) 
      parent = shortestPathTree[parent].getTo();
    else
      parent = shortestPathTree[parent].getFrom(); 
  } while ( parent != source ); 
  
  cout << "done printing" << endl;
} 
