/*
 * MCPainter.h
 *
 *  Created on: Jun 2, 2010
 *      Author: appleapple
 */

#ifndef MCPAINTER_H_
#define MCPAINTER_H_

#include <GL/gl.h>
#include <GL/glut.h>
#include <Map.h>
#include <MonteCarlo.h>
#include "MonteCarloVisualDebugger.h"
#include "Graph.h"
#include "PathPlanner.h"

class MCPainter {
public:
  MCPainter();
  
  void drawGrid(Map * map);
  void drawMarkers(Map * map);
  void drawWalls(Map * map); 
  void drawParticles(MonteCarloVisualDebugger * debugger);
  void drawObservations(MonteCarloVisualDebugger * debugger, MonteCarlo * mc);
  void drawPosition(MonteCarlo * mc, Position realPosition);
  void drawGoal(double goalX, double goalY);
  
  // for path planner
  void drawNodes(Graph * g);
  void drawEdges(Graph * g); 
  void drawSource(Graph * g, int x, int y); 
  void drawTarget(Graph * g, int x, int y);
  void drawPath(Graph * g, list<int> nodes); 
};

#endif /* MCPAINTER_H_ */
