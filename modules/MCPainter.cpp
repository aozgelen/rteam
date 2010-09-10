/*
 * MCPainter.cpp
 *
 *  Created on: Jun 2, 2010
 *      Author: appleapple
 */

#include "MCPainter.h"

MCPainter::MCPainter() {
	// TODO Auto-generated constructor stub

}

void MCPainter::drawGrid(Map * map) {
	glBegin(GL_LINES);
	{
		glColor3f(.3, .3, .3);
		for (int i = 0; i < 10; i++) {
			glVertex2f(0, i * map->getHeight() / 10.0);
			glVertex2f(map->getLength(), i * map->getHeight() / 10.0);
			glVertex2f(i * map->getLength() / 10.0, 0);
			glVertex2f(i * map->getLength() / 10.0, map->getHeight());
		}
	}
	glEnd();
}

void MCPainter::drawMarkers(Map * map) {
	glBegin(GL_LINES);
	{
		vector<MapMarker> markers = map->getMarkers();
		for (int i = 0; i < markers.size(); i++) {
			glColor3f(0, 1, 1);
			glVertex2f(markers[i].getX() - 1, markers[i].getY() - 1);
			glVertex2f(markers[i].getX() - 1, markers[i].getY() + 1);
			glVertex2f(markers[i].getX() + 1, markers[i].getY() + 1);
			glVertex2f(markers[i].getX() + 1, markers[i].getY() - 1);
		}
	}
	glEnd();
}

void MCPainter::drawWalls(Map * map){
  glBegin(GL_LINES);
  {
    vector<MapWall> walls = map->getWalls();
    vector<MapWall>::iterator iter; 
    for ( iter = walls.begin(); iter != walls.end(); iter++ ){
      glColor3f(1,1,1);
      glVertex2f(iter->getX0(), iter->getY0()); 
      glVertex2f(iter->getX1(), iter->getY1());
    }
  }
  glEnd();
}

void MCPainter::drawNodes(Graph * g){
  if ( g->numNodes() > 0 ) {
    glBegin(GL_LINES);
    {
      vector<Node> nodes = g->getNodes();
      vector<Node>::iterator iter; 
      for( iter = nodes.begin(); iter != nodes.end(); iter++ ) {
	glColor3f(0.25,0.25,0.25); 
	glVertex2f(iter->getX()-1, iter->getY()-1); 
	glVertex2f(iter->getX()-1, iter->getY()+1); 
	glVertex2f(iter->getX()-1, iter->getY()+1); 
	glVertex2f(iter->getX()+1, iter->getY()+1); 
	glVertex2f(iter->getX()+1, iter->getY()+1); 
	glVertex2f(iter->getX()+1, iter->getY()-1); 
	glVertex2f(iter->getX()+1, iter->getY()-1); 
	glVertex2f(iter->getX()-1, iter->getY()-1); 
      }
    }
    glEnd();
  }
}


void MCPainter::drawEdges(Graph * g){
  if ( g->numEdges() > 0 ) {
    glBegin(GL_LINES);
    {
      vector<Edge> edges = g->getEdges();
      vector<Edge>::iterator iter; 
      for( iter = edges.begin(); iter != edges.end(); iter++ ) {
	glColor3f(0.1,0.1,0.1);  
	Node n1 = g->getNode(iter->getFrom()); 
	Node n2 = g->getNode(iter->getTo());
	glVertex2f(n1.getX(), n1.getY()); 
	glVertex2f(n2.getX(), n2.getY()); 
      }
    }
    glEnd();
  }
}


void MCPainter::drawSource(Graph * g, int x, int y){
  glBegin(GL_LINES);
  if ( g->isWithinBorders(x,y) ) {
    glColor3f(0,1,0);
    glVertex2f(x-1, y-1); 
    glVertex2f(x-1, y+1); 
    glVertex2f(x-1, y+1); 
    glVertex2f(x+1, y+1); 
    glVertex2f(x+1, y+1); 
    glVertex2f(x+1, y-1); 
    glVertex2f(x+1, y-1); 
    glVertex2f(x-1, y-1); 
  }
  glEnd();
}

void MCPainter::drawTarget(Graph * g, int x, int y){
  glBegin(GL_LINES);
  if ( g->isWithinBorders(x,y) ) {
    glColor3f(1,0,0);
    glVertex2f(x-1, y-1); 
    glVertex2f(x-1, y+1); 
    glVertex2f(x-1, y+1); 
    glVertex2f(x+1, y+1); 
    glVertex2f(x+1, y+1); 
    glVertex2f(x+1, y-1); 
    glVertex2f(x+1, y-1); 
    glVertex2f(x-1, y-1); 
  }
  glEnd();
}

void MCPainter::drawPath(Graph * g, list<int> nodes) {
  if ( !nodes.empty() ) {
    // draw the line between the source and the first node
    
    // draw the lines between nodes
    list<int>::iterator iter;
    for( iter = nodes.begin(); iter != nodes.end(); ) {
      int f = *iter; 
      //iter++;
      if( ++iter != nodes.end() ){
	glBegin(GL_LINES);
	glColor3f(0.5,0.5,0);
	cout << "drawing line from: " ; 
	g->getNode(f).printNode(); 
	glVertex2f(g->getNode(f).getX(), g->getNode(f).getY()); 
	cout << " to: " ;
	g->getNode(*iter).printNode(); 
	glVertex2f(g->getNode(*iter).getX(), g->getNode(*iter).getY()); 
	cout << endl;
	glEnd();
      }
    }
    
    // draw the line between the last node and the target
  }
}

void MCPainter::drawParticles(MonteCarloVisualDebugger * debugger) {
	glBegin(GL_POINTS);
	{
		glVertex2f(0, 0);
		glVertex2f(150, 150);
		for (int i = 0; i < debugger->particles.size(); i++) {
			double c = debugger->particles[i].probability;
			glColor3f(.5*(1-c), 1 *c, 0);
			Position p = debugger->particles[i].getPosition();
			glVertex2f(p.getX(), p.getY());
		}
	}
	glEnd();
}

void MCPainter::drawObservations(MonteCarloVisualDebugger * debugger,
		MonteCarlo * mc) {
	vector<Observation> obs = debugger->getObservations();
	glBegin(GL_LINES);
	{
		for (int i = 0; i < obs.size(); i++) {

			if (obs[i].getMarkerId() == "p/y" || obs[i].getMarkerId() == "y/p")
				glColor3f(1, 1, 0);
			else if (obs[i].getMarkerId() == "b/p" || obs[i].getMarkerId()
					== "p/b")
				glColor3f(0, 0, 1);
			else if (obs[i].getMarkerId() == "p/g" || obs[i].getMarkerId()
					== "g/p")
				glColor3f(0, 1, 0);
			else if (obs[i].getMarkerId() == "p/o" || obs[i].getMarkerId()
					== "o/p")
				glColor3f(1, .5, 0);
			else
				glColor3f(1, 0, 1);

			Position position = mc->getPosition();
			glVertex2f(position.getX(), position.getY());

			int lineLen = 1000;
			int x = position.getX() + lineLen * cos(obs[i].getBearing()
					+ position.getTheta());
			int y = position.getY() + lineLen * sin(obs[i].getBearing()
					+ position.getTheta());
			glVertex2f(x, y);
		}
	}
	glEnd();
}

void MCPainter::drawPosition(MonteCarlo * mc, Position realPosition) {
	//draw estimated position
	glBegin(GL_LINES);
	{
		Position p = mc->getPosition();
		glColor3f(1, 0, 0);
		int lineLen = 15;
		int x1 = p.getX() - lineLen * cos(p.getTheta() + .3);
		int y1 = p.getY() - lineLen * sin(p.getTheta() + .3);
		int x2 = p.getX() - lineLen * cos(p.getTheta() - .3);
		int y2 = p.getY() - lineLen * sin(p.getTheta() - .3);
		glVertex2f(p.getX(), p.getY());
		glVertex2f(x1, y1);
		glVertex2f(x2, y2);
		glVertex2f(p.getX(), p.getY());
	}
	glEnd();

	//draw real position
	glBegin(GL_LINES);
	{
		Position p = realPosition;
		glColor3f(0, 0, 1);
		int lineLen = 100;
		int x = p.getX() + lineLen * cos(p.getTheta());
		int y = p.getY() + lineLen * sin(p.getTheta());
		glVertex2f(p.getX(), p.getY());
		glVertex2f(x, y);
	}
	glEnd();
}

void MCPainter::drawGoal(double goalX, double goalY) {
	glBegin(GL_LINES);
	{
		glColor3f(1, 0, 0);
		glVertex2f(goalX - 10, goalY);
		glVertex2f(goalX + 10, goalY);
		glVertex2f(goalX, goalY + 10);
		glVertex2f(goalX, goalY - 10);
	}
	glEnd();
}

