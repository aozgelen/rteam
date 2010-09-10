#ifndef GRAPH_H
#define GRAPH_H

#include "Node.h"
#include "Edge.h"
#include <vector>
#include <queue>
#include "Map.h"
#include "MapWall.h"
#include "GraphUtils.h"

class Graph {
private:
  vector<Node> nodes; 
  vector<Edge> edges; 
  bool digraph; 

  int proximity;           // proximity between 2 nodes ( in cm ) 
  Map * map ; 

  void generateNavGraph();    // uses flood fill algorithm 
  void updateAdjacencyLists(Edge e); // when an edge is added/removed update the adjacency lists of the nodes involved

public: 
  Graph(Map*, bool, int);
  int getProximity() const { return proximity; }
  vector<Node> getNodes() const { return nodes; }
  vector<Edge> getEdges() const { return edges; }
  Node getNode(int n);
  vector<Node> getNode(int x, int y); 
  vector<Node> getNeighbors(Node n); 
  Edge getEdge(int n1, int n2);
  void addNode(Node n); 
  void addEdge(Edge e); 
  virtual bool removeNode(Node n); 
  virtual bool removeEdge(Edge e);
  bool isNode(Node n); 
  bool isEdge(Edge e); 
  int numNodes() const { return nodes.size(); }
  int numEdges() const { return edges.size(); }
  bool isdigraph() { return digraph; }
  void setdigraph(bool b) { digraph = b; }
  void printEdge(Edge);
  void printGraph() ;

  double calcEdgeCost(Node, Node);
  double calcCost(Node, Node);
  bool isWithinBorders( int, int );
  bool isPathObstructed( int, int, int, int );
};

#endif
