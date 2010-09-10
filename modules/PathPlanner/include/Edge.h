#ifndef EDGE_H
#define EDGE_H

#include "Node.h"
#include <vector>
#include <iostream>
using namespace std; 

using namespace std;

class Edge {
private:
  int from, to;  

protected:
  double cost;

public:
  Edge(): from (Node::invalid_node_index), to(Node::invalid_node_index), cost(0){} 
  Edge(int n1, int n2, double c = 0): from(n1), to(n2), cost(c){}
  bool operator == ( Edge e ) {
    int ef = e.getFrom();
    int et = e.getTo(); 
    double ec = e.getCost(); 
    return ( from == ef && to == et && cost == ec );
  }
  double getCost() { return cost; }
  void setCost(double c) { cost = c; }
  int getFrom(){ return from; }
  int getTo(){ return to; }
  vector<int> getNodes() {
    vector<int> n; 
    n.push_back(from); 
    n.push_back(to); 
    return n;
  }
  void printEdge() const{
    cout << "<EDGE-From Node:" << from << " -To Node:" << to << " - Cost: " << cost << " >" << endl;
  }
}; 

#endif
