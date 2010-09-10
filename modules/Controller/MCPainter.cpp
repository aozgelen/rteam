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

void MCPainter::drawMarkers(Map * aiboMap) {
	glBegin(GL_LINES);
	{
		vector<MapMarker> markers = aiboMap->getMarkers();
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

