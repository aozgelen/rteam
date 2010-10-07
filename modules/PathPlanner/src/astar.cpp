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
  search();
}

void astar::search(){
  IndexedPriorityQLow<double> frontierNodes(f_cost,searchGraph.numNodes()); 

  frontierNodes.insert(source); 

  while( !frontierNodes.empty() ) {
    int nextCloseNode = frontierNodes.Pop();
    shortestPathTree[nextCloseNode] = searchFrontier[nextCloseNode];

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
	}
	else
	  nextNode = iter->getTo();

	double hCost = get_euclidian_distance( searchGraph.getNode(iter->getTo()).getX(), 
					       searchGraph.getNode(iter->getTo()).getY(), 
					       searchGraph.getNode(target).getX(), 
					       searchGraph.getNode(target).getY() ) 
	  - ( neighborhoodHeuristic(searchGraph.getNode(nextNode), neighborhoodLevel) * (searchGraph.getProximity()) );

	double gCost = g_cost[nextCloseNode] + iter->getCost() ;
	
	if ( searchFrontier[iter->getTo()].getTo() == Node::invalid_node_index && iter->getTo() != nextCloseNode ) {
	  f_cost[iter->getTo()] = gCost + hCost; 
	  g_cost[iter->getTo()] = gCost; 
	  frontierNodes.insert(iter->getTo());
	  searchFrontier[iter->getTo()] = *iter;
	}
	else if ( gCost < g_cost[iter->getTo()] &&
		  searchFrontier[iter->getTo()].getTo() == Node::invalid_node_index ) {
	  f_cost[iter->getTo()] = gCost + hCost; 
	  g_cost[iter->getTo()] = gCost; 
	  frontierNodes.ChangePriority(iter->getTo());
	  searchFrontier[iter->getTo()] = *iter;	
	}
	// if the graph is a digraph then From nodes have to be considered as well
	if ( searchGraph.isdigraph() ) {
	  if ( searchFrontier[iter->getFrom()].getFrom() == Node::invalid_node_index && iter->getFrom() != nextCloseNode ) {
	    f_cost[iter->getFrom()] = gCost + hCost; 
	    g_cost[iter->getFrom()] = gCost; 
	    frontierNodes.insert(iter->getFrom());
	    searchFrontier[iter->getFrom()] = *iter;
	  }
	  else if ( gCost < g_cost[iter->getFrom()] &&
		    searchFrontier[iter->getFrom()].getFrom() == Node::invalid_node_index ) {
	    f_cost[iter->getFrom()] = gCost + hCost; 
	    g_cost[iter->getFrom()] = gCost; 
	    frontierNodes.ChangePriority(iter->getFrom());
	    searchFrontier[iter->getFrom()] = *iter;	
	  }
	}
      }
    }
  }
}

// this doesn't get exact number of neighbors, some nodes are counted multiple times but
// there is no need to be specific. 
int astar::neighborhoodHeuristic(Node n, int level) {
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

  do { 
    if ( parent == shortestPathTree[parent].getFrom() )
      parent = shortestPathTree[parent].getTo();
    else if ( parent == shortestPathTree[parent].getTo() )
      parent = shortestPathTree[parent].getFrom();      
    path.push_front(parent); 
  }while ( parent != source ); 

  return path; 
}
 
double astar::getCostToTarget(){
  double cost = 0; 
  int parent = target; 

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
} 
