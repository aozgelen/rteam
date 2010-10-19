#include "VisualDebugger.h"

VisualDebugger::VisualDebugger(Map * m, Controller * c): myMap(m), robot(c) {
  itl = robot->getInterfaceToLocalization(); 
  mc = itl->getMonteCarlo(); 
  debugger = new MonteCarloDebugger();
  mc->setDebugger(debugger);
  planner = robot->getPlanner(); 
  g = robot->getNavgraph();
  
  keyboardCtrl = KEY_CTRL_STEP;
}

//called when the window changes position and size
void VisualDebugger::reshape(int w, int h) {
  glViewport(0, 0, (GLsizei) w, (GLsizei) h);
  glMatrixMode(GL_PROJECTION);
  glLoadIdentity();
  gluOrtho2D(-10, myMap->getLength() + 10, -10, myMap->getHeight() + 10);
}

void VisualDebugger::keyboard(unsigned char key, int x, int y){
  cout << "\tUSER KEY COMMAND: " << key << endl;
  // switch mode. alternate between move in discrete steps vs. continuos mode
  if (key == 's' || key == 'S'){
    robot->setOpMode(MANUAL);
    (keyboardCtrl == KEY_CTRL_STEP) ? keyboardCtrl = KEY_CTRL_CONT : keyboardCtrl = KEY_CTRL_STEP ;    
  }
  // switch to auto mode
  if (key == 'a' || key == 'A'){
    robot->setOpMode(AUTO);
  }
  // stop the robot 
  if (key == ' ' ){
    robot->setOpMode(MANUAL);
    robot->stop();
  }
}

void VisualDebugger::keyboardSpecial(int key, int x, int y){
  if (key == GLUT_KEY_UP){
    robot->setOpMode(MANUAL);
    if ( keyboardCtrl == KEY_CTRL_STEP) 
      itl->move(Position(10, 0, 0));
    if ( keyboardCtrl == KEY_CTRL_CONT) {
      // need to set something here for moving particles
      itl->setSpeed(10, 0, 0);
    }
  }
  if (key == GLUT_KEY_DOWN){
    robot->setOpMode(MANUAL);
    if ( keyboardCtrl == KEY_CTRL_STEP) 
      itl->move(Position(-10, 0, 0));
    if ( keyboardCtrl == KEY_CTRL_CONT) 
      itl->setSpeed(-10, 0, 0);
  }
  if (key == GLUT_KEY_LEFT){
    robot->setOpMode(MANUAL);
    if ( keyboardCtrl == KEY_CTRL_STEP) 
      itl->move(Position(0, 0, Utils::toRadians(22.5))); 
    if ( keyboardCtrl == KEY_CTRL_CONT) 
      itl->move(Position(0, 0, Utils::toRadians(22.5))); 
  }
  if (key == GLUT_KEY_RIGHT){
    robot->setOpMode(MANUAL);
    if ( keyboardCtrl == KEY_CTRL_STEP) 
      itl->move(Position(0, 0, Utils::toRadians(-22.5))); 
    if ( keyboardCtrl == KEY_CTRL_CONT) 
      itl->move(Position(0, 0, Utils::toRadians(-22.5))); 
  }
}

int VisualDebugger::getWinX(int x) {
  int wx = ( (double) (glutGet(GLUT_WINDOW_WIDTH) - 40)/ (double) myMap->getLength() ) * x + 20; 
  return wx; 
}

int VisualDebugger::getWinY(int y) {
  int wy = ( myMap->getHeight() - y ) * ( (double)( glutGet(GLUT_WINDOW_HEIGHT)- 20 ) / (double) myMap->getHeight() ) + 10;
  return wy;
}

int VisualDebugger::getMapX(int x) {
  int mx = ( x - 20 ) * ( (double) myMap->getLength()/ (double) (glutGet(GLUT_WINDOW_WIDTH)-40)); 
  return mx; 
}

int VisualDebugger::getMapY(int y){
  int my = myMap->getHeight() - (( y - 10 ) * ((double) myMap->getHeight() /(double) (glutGet(GLUT_WINDOW_HEIGHT) - 20) ));
  return my;
}

void VisualDebugger::mouse(int button, int state, int x, int y) {
  robot->setOpMode(MIXED_INIT);
  if (button == GLUT_LEFT_BUTTON) {
    if (state == GLUT_DOWN){
      cout << "\tUSER MOUSE COMMAND: go (" << x << "," << y << ")" << endl;
      Position p = itl->getPosition();
      
      Node s(1, p.getX(), p.getY()); 
      planner->setSource(s); 

      Node t(1, getMapX(x), getMapY(y)); 
      planner->setTarget(t); 
      planner->calcPath();
    }			
  }
}

// this bit doesn't work. maybe due to graphix card requirement? 
void VisualDebugger::drawFog(void){
  MCPainter painter; 
  glutUseLayer(GLUT_OVERLAY);
  glClear(GL_COLOR_BUFFER_BIT);
  glClearColor(0.25,0.25,0.25,0.25); // set current color to black

  // call some functioni from MCPainter to draw the fog on top of the map
  painter.drawFogOfExploration(); 
  glutSwapBuffers(); 
}

void VisualDebugger::draw(void) {
  MCPainter painter;
  glutUseLayer(GLUT_NORMAL);
  glClear(GL_COLOR_BUFFER_BIT);
  glClearColor(1,1,1,1); // set current color to white

  painter.drawMarkers(myMap);
  painter.drawWalls(myMap);
  painter.drawParticles(debugger);
  painter.drawObservations(debugger, itl);       // draws the lines from position to markers
  painter.drawPosition(itl, Position(0, 0, 0));  // draws the position of the robot
  painter.drawNodes(g); 
  painter.drawEdges(g);
  if ( planner->getSource().getID() != Node::invalid_node_index ){
    painter.drawSource(g, planner->getSource().getX(), planner->getSource().getY()); 
  }
  if ( planner->getTarget().getID() != Node::invalid_node_index ){
    painter.drawTarget(g, planner->getTarget().getX(), planner->getTarget().getY());
  }
  if ( !planner->getPath().empty() ){
    painter.drawPath(itl, planner, g);
  }
  if ( robot->getWaypoint().getID() != Node::invalid_node_index ){
    // cout<< "drawing waypoint at: " ; 
    // robot->getWaypoint().printNode();
    // cout<< endl;
    painter.drawGoal(g, robot->getWaypoint().getX(), robot->getWaypoint().getY());
  }
  glutSwapBuffers();
}

