#ifndef NODE_H
#define NODE_H

#include <iostream>
#include <vector> 
using namespace std; 

class Node {
protected:
  int id; 
  int x, y; 

  vector<Node> neighbors;  // this is the adjacency list of the node. populated from Graph.
public:
  Node(int i = invalid_node_index, int xt = 0, int yt = 0): id(i), x(xt), y(yt){} 
  bool operator == (const Node& n) const{
    return ( this->id == n.getID() && this->x == n.getX() && this->y == n.getY() );
  }
  bool operator<(const Node& n) const{
    return ( this->id < n.getID() );
  }
  /*void operator=(const Node& n){
    this->id = n.getID(); 
    this->x = n.getX(); 
    this->y = n.getY();
    }*/
  void setID(int i) { id = i; }
  int getID() const { return id; }
  void setX(int x) { this->x = x; }
  int getX() const { return x; }
  void setY(int y) { this->y = y; }
  int getY() const { return y; }
  void printNode() const{
    cout << "<NODE: " << id << " :(" << x << "," << y << ") >";
  }

  void addNeighbor(Node n){
    neighbors.push_back(n);
  }

  void removeNeighbor(Node n){
    vector<Node>::iterator iter; 
    for( iter = neighbors.begin(); iter != neighbors.end(); iter++ )
      if ( *iter == n ) {
	neighbors.erase(iter);
	break;
      }
  }

  vector<Node> getNeighbors() {
    return neighbors;
  }

  int numNeighbors() const { return neighbors.size(); }

  bool isNeighbor(Node n) {
    vector<Node>::iterator iter; 
    for( iter = neighbors.begin(); iter != neighbors.end(); iter++ )
      if ( *iter == n ) 
	return true;
    return false; 
  }

  bool neighborEmpty(){
    return ( neighbors.size() == 0 );
  }

  static const int invalid_node_index = -1; 
}; 

#endif

