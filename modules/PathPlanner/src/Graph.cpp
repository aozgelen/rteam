#include "Graph.h"

Graph::Graph(Map * m, bool d = true, int p = 30): proximity(p), digraph(d), map(m){
  generateNavGraph();
}

void Graph::printEdge(Edge e) {
  cout << "<EDGE-From:" ; 
  getNode(e.getFrom()).printNode(); 
  cout << " -To:" ; 
  getNode(e.getTo()).printNode(); 
  cout << " - Cost: " << e.getCost() << " >" << endl;
}

void Graph::printGraph() {
  cout << "Number of nodes: " << numNodes() << ", number of edges: " << numEdges() << endl;
  vector<Edge>::iterator iter; 
  for( iter = edges.begin(); iter != edges.end(); iter++ )
    iter->printEdge();
}


/*
 * return a node with the given index or return one with invalid_index if not found.
 */
Node Graph::getNode(int n){
  vector<Node>::iterator iter; 
  for( iter = nodes.begin(); iter != nodes.end(); iter++ )
    if( iter->getID() == n ) 
      return *iter; 
  Node* n0 = new Node(); 
  return *n0;
}

/*
 * return an edge with the given indexes of nodes or return one with invalid indexes if not found
 */
Edge Graph::getEdge(int n1, int n2){
  vector<Edge>::iterator eiter;
  Node nd1 = getNode(n1);
  Node nd2 = getNode(n2); 
  if (! (nd1.getID() == Node::invalid_node_index || nd2.getID() == Node::invalid_node_index )){
    for( eiter = edges.begin(); eiter != edges.end(); eiter++ ){
      if ( n1 == eiter->getFrom() && n2 == eiter->getTo() ) {
	return *eiter; 
	if ( isdigraph() && n1 == eiter->getTo() && n2 == eiter->getFrom() ) 
	  return *eiter;
      }
    }
  }
  Edge * e0 = new Edge(Node::invalid_node_index, Node::invalid_node_index);
  return *e0;
}

void Graph::addNode(Node n){
  nodes.push_back(n);
}

void Graph::addEdge(Edge e){
  edges.push_back(e);
}

bool Graph::removeNode(Node n){
  vector<Node>::iterator iter = nodes.begin() ; 
  while ( iter != nodes.end() ) {
    if ( *iter == n ) {
      // remove all the edges that this node is a part of
      vector<Edge>::iterator eit = edges.begin(); 
      while( eit != edges.end()){
	if ( eit->getFrom() == n.getID() || eit->getTo() == n.getID() ) 
	  edges.erase(eit);
	else
	  eit++;
      }
      // then erase the node
      nodes.erase(iter);
      return true; 	  
    }
    else
      iter++;
  }
  return false; 
}

bool Graph::removeEdge(Edge e){
  vector<Edge>::iterator iter = edges.begin() ; 
  while ( iter != edges.end() ) {
    if ( iter->getFrom() == e.getFrom() && iter->getTo() == e.getTo() ) {
      edges.erase(iter);
      return true; 
    }
    else
      iter++;
  }
  return false;
}

bool Graph::isNode(Node n){
  vector<Node>::iterator iter; 
  for( iter = nodes.begin(); iter != nodes.end(); iter++ )
    if ( *iter == n ) 
      return true; 
  return false;
}

bool Graph::isEdge(Edge e){
  vector<Edge>::iterator iter; 
  for( iter = edges.begin(); iter != edges.end(); iter++ )
    if ( digraph ){
      Edge * e2 = new Edge(e.getTo(), e.getFrom(), e.getCost());
      if ( *iter == e || *iter == *e2 ) 
	return true; 
    }
    else {
      if ( *iter == e ) 
	return true; 
    }
  return false;

}

void Graph::generateNavGraph() {
  int l = map->getLength(); 
  int h = map->getHeight(); 
  
  int xExcess = l % proximity; 
  int yExcess = h % proximity; 
  
  // create nodes
  Node * n; 
  int index = 0; 
  for( int x = xExcess/2; x < l; x += proximity ){
    for( int y = yExcess/2; y < h; y += proximity ) {
      n = new Node(index, x, y); 
      nodes.push_back(*n); 
      index++;
    }
  }

  // this is only for a grid graph where cost of edges can only be sides of a square or diagonal of it.
  // also it assumes the grid base is parallel to x and y axis. in short this is a really specific code 
  // not generic!
  double longedge = get_euclidian_distance(0, 0, proximity, proximity); 
  double shortedge = get_euclidian_distance(0, 0, proximity, 0 ); 

  // create edges
  vector<Node>::iterator iter; 
  for( iter = nodes.begin(); iter != nodes.end(); iter++ ){
    vector<Node> nbrs = getNeighbors(*iter); 
    vector<Node>::iterator it; 
    for( it = nbrs.begin(); it != nbrs.end(); it++ ){
      Edge * e = new Edge( iter->getID(), it->getID() ) ;
      if ( iter->getX() == it->getX() || iter->getY() == it->getY() )
	e->setCost(shortedge); 
      else
	e->setCost(longedge);
      if ( !isEdge(*e) )
	edges.push_back(*e);
    }
  }
}

vector<Node> Graph::getNeighbors(Node n){
  vector<Node> nbrs; 
  if( n.neighborEmpty() ) {
    for( int x = n.getX()-proximity; x <= n.getX()+proximity; x += proximity )
      for( int y = n.getY()-proximity; y <= n.getY()+proximity; y += proximity ) 
	if( ! ( x == n.getX() && y == n.getY() ) )
	  if ( isWithinBorders(x, y) &&  !isPathObstructed(n.getX(), n.getY(), x, y) ) {
 	    vector<Node> nd = getNode(x,y) ; 
	    vector<Node>::iterator iter; 
	    for( iter = nd.begin(); iter != nd.end(); iter++ ){
	      nbrs.push_back(*iter); 
	      n.addNeighbor(*iter);
	    }
	  }
  }
  else
    nbrs = n.getNeighbors(); 
  return nbrs; 
}

vector<Node> Graph::getNode(int x, int y){
  vector<Node> nds; 
  vector<Node>::iterator iter; 
  for( iter = nodes.begin(); iter != nodes.end(); iter++ ){
    if ( iter->getX() == x && iter->getY() == y ) 
      nds.push_back(*iter); 
  }
  return nds; 
}

// when an edge is added/removed update the adjacency lists of the nodes involved 
void Graph::updateAdjacencyLists(Edge e){
  if ( isEdge(e) ){      // already an edge so remove
    getNode(e.getFrom()).removeNeighbor(getNode(e.getTo())); 
    if ( digraph ) 
      getNode(e.getTo()).removeNeighbor(getNode(e.getFrom())); 
  }
  else {                 // not an edge so add
    getNode(e.getFrom()).addNeighbor(getNode(e.getTo())); 
  }
}

bool Graph::isPathObstructed(int x0, int y0, int x1, int y1 ){
  vector<MapWall> walls = map->getWalls();
  vector<MapWall>::iterator iter; 
  for( iter = walls.begin(); iter != walls.end(); iter++ ){
    double * ix = new double();
    double * iy = new double();
    if ( get_line_intersection( static_cast<double>(x0), static_cast<double>(y0), static_cast<double>(x1), 
				static_cast<double>(y1), static_cast<double>(iter->getX0()),
				static_cast<double>(iter->getY0()),static_cast<double>(iter->getX1()),
				static_cast<double>(iter->getY1()), ix, iy ) )
      return true;
  }
  return false;
}

bool Graph::isWithinBorders(int x, int y){
  int len = map->getLength(); 
  int hgt = map->getHeight(); 

  if ( x > 0 && x < len && y > 0 && y < hgt ) 
    return true;
 
  return false; 
}

double Graph::calcCost(Node n1, Node n2){
  return get_euclidian_distance(n1.getX(), n1.getY(), n2.getX(), n2.getY()); 
} 
