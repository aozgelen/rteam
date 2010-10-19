#ifndef VISUAL_DEBUGGER_H
#define VISUAL_DEBUGGER_H

#include "MCPainter.h"
#include "Controller.h"
#include "MonteCarloDebugger.h"

class VisualDebugger {
  Controller * robot; 
  Map * myMap;

  InterfaceToLocalization * itl ; 
  PathPlanner * planner; 
  Graph * g; 
  MonteCarlo * mc; 
  MonteCarloDebugger * debugger; 

  int keyboardCtrl; 
 public: 
  VisualDebugger(Map*, Controller*); 

  void reshape(int, int); 
  void keyboard(unsigned char, int, int); 
  void keyboardSpecial(int, int, int); 
  void mouse(int, int, int, int); 
  void draw(void); 
  void drawFog(void);

  // util functions
  int getWinX(int); 
  int getMapX(int); 
  int getWinY(int); 
  int getMapY(int);
};

#endif
