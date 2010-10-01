#include "definitions.h"
#include "robot.h"
#include "loiter.h"
#include "wallAvoid.h"
#include "BehaviorTester.h"

#include "libplayerc++/playerc++.h"
using namespace PlayerCc;

#include "MonteCarloVisualDebugger.h"
#include "MCPainter.h"
#include "InterfaceToLocalization.h"
#include "Surveyor.h"
#include "Aibo.h"
#include <DataSerializer.h>
#include <MonteCarlo.h>
#include "PathTester.h"

#include <cstdlib>
#include <iostream>
#include <sstream>
#include <unistd.h>
#include <cstring>
using namespace std;

Map * myMap;
InterfaceToLocalization * rbt; 
MonteCarlo * mc;
MonteCarloVisualDebugger * debugger;
Graph * g;
PathPlanner * planner; 

int goalCounter = 0;
int updateCounter = 0;
double deltaDistance = 0;
double goalX;
double goalY;

void init(void) {
  glClearColor(0.0, 0.0, 0.0, 0.0);
  glShadeModel(GL_FLAT);
}

void updateRobot() {
  //cout << "calling interface to localization update() func" << endl;
  rbt->update();
}

//this method is called every couple of seconds and updates virtual robot position and
//MC.  Then it displays the current state of MC by indirectly calling "draw()".
void displayObservations(int unused) {
  updateRobot();
  glutPostRedisplay();
  glutTimerFunc(20, displayObservations, 0);
}

//called when the window changes position and size
void reshape(int w, int h) {
  glViewport(0, 0, (GLsizei) w, (GLsizei) h);
  glMatrixMode(GL_PROJECTION);
  glLoadIdentity();
  gluOrtho2D(-10, myMap->getLength() + 10, -10, myMap->getHeight() + 10);
}

void keyboard(unsigned char key, int x, int y){
  if (key == 'w' || key == 'W')
    rbt->move(Position(10, 0, 0));
  if (key == 's' || key == 'S')
    rbt->setSpeed(-10, 0, 0);
  if (key == 'a' || key == 'A')
    rbt->move(Position(0, 0, Utils::toRadians(22.5)));
  if (key == 'd' || key == 'D')
    rbt->move(Position(0, 0, Utils::toRadians(-22.5)));
}

int getWinX(int x) {
  int wx = ( (double) (glutGet(GLUT_WINDOW_WIDTH) - 40)/ (double) myMap->getLength() ) * x + 20; 
  return wx; 
}

int getWinY(int y) {
  int wy = ( myMap->getHeight() - y ) * ( (double)( glutGet(GLUT_WINDOW_HEIGHT)- 20 ) / (double) myMap->getHeight() ) + 10;
  return wy;
}

int getMapX(int x) {
  int mx = ( x - 20 ) * ( (double) myMap->getLength()/ (double) (glutGet(GLUT_WINDOW_WIDTH)-40)); 
  return mx; 
}

int getMapY(int y){
  int my = myMap->getHeight() - (( y - 10 ) * ((double) myMap->getHeight() /(double) (glutGet(GLUT_WINDOW_HEIGHT) - 20) ));
  return my;
}

void mouse(int button, int state, int x, int y) {
  if (button == GLUT_LEFT_BUTTON) {
    if (state == GLUT_DOWN){
      Position p = rbt->getPosition();
      
      Node s(1, p.getX(), p.getY()); 
      planner->setSource(s); 

      cout << "clicked point: (" << x << "," << y << ")" << endl;

      Node t(1, getMapX(x), getMapY(y)); 
      planner->setTarget(t); 
      planner->calcPath();
    }			
  }
}

// this bit doesn't work. maybe due to graphix card requirement? 
void drawFog(void){
  cout << "setting overlay" << endl;
  glutUseLayer(GLUT_OVERLAY);
  glClear(GL_COLOR_BUFFER_BIT);
  glClearColor(0.25,0.25,0.25,0.25); // set current color to black
  glutSwapBuffers(); 
}

void draw(void) {
  MCPainter painter;
  glutUseLayer(GLUT_NORMAL);
  glClear(GL_COLOR_BUFFER_BIT);
  glClearColor(1,1,1,1); // set current color to white

  painter.drawMarkers(myMap);
  painter.drawWalls(myMap);
  painter.drawParticles(debugger);
  painter.drawObservations(debugger, mc);       // draws the lines from position to markers
  painter.drawPosition(mc, Position(0, 0, 0));  // draws the position of the robot
  painter.drawGoal(goalX, goalY);
  painter.drawNodes(g); 
  painter.drawEdges(g);
  if ( planner->getSource().getID() != Node::invalid_node_index ){
    painter.drawSource(g, planner->getSource().getX(), planner->getSource().getY()); 
  }
  if ( planner->getTarget().getID() != Node::invalid_node_index ){
    painter.drawTarget(g, planner->getTarget().getX(), planner->getTarget().getY());
  }
  if ( !planner->getPath().empty() )
    painter.drawPath(g, planner->getPath());
  glutSwapBuffers();
}


void createMap_defaultField() {
  myMap = new Map(500, 400);
  
  // outer walls
  myMap->addWall(MapWall("wall1", 0, 0, 0, 400)); 
  myMap->addWall(MapWall("wall2", 0, 0, 500, 0)); 
  myMap->addWall(MapWall("wall3", 500, 0, 500, 400)); 
  myMap->addWall(MapWall("wall4", 0, 400, 500, 400)); 
}

void readMapFile(ifstream& mFile) {
  string  cmd, label, tmp;
  int x1, y1, x2, y2; 
  bool first = true ;

  cout << "read File. " << endl; 
  
  while( !mFile.eof() ) {
    cmd = ""; label = ""; x1 = 0 ; y1 = 0 ; x2 = 0 ; y2 = 0 ; 
    mFile >> cmd ; 

    // in case the file is empty create a default map
    if ( mFile.eof() ){
      if ( first ){
	cout << "Empty map file!" << endl; 
	createMap_defaultField();
      }
    }
    // if the line is commented skip it otherwise process.
    else if (! ( cmd[0] == '/' && cmd[1] == '/' ) ){ 
      // if the first command includes size, set the window for the specified values
      // else create the default map
      if ( first ){
	first = false;
	if ( cmd == "size" ) {
	  mFile >> x1 >> y1 ; 
	  myMap = new Map(x1, y1);
	  continue;
	}
	else
	  createMap_defaultField();
      }

      // process the command
      if ( cmd == "marker" ){ 
	  mFile >> label >> x1 >> y1 ; 
	  myMap->addMarker(MapMarker(label, x1, y1));	
      }
      else if ( cmd == "wall" ){
	mFile >> label >> x1 >> y1 >> x2 >> y2 ; 
	myMap->addWall(MapWall(label, x1, y1, x2, y2)); 
      }
      else {
	if ( cmd == "size" )
	  cout << "size command has to be the first command in map config file. command ignored." << endl;
	else
	  cout << "Unknown map config command: " << cmd << endl;
	getline(mFile, tmp); 
      }
      
    }
    else {
      // ignore the rest of the line.
      getline(mFile, tmp); 
    }
  } 
}


void displayUsage(int argc, char** argv){
  cout << "USAGE: " << argv[0] << " [ options ]" << endl << endl;
  cout << "Where [ options ] can be: " << endl ;
  cout << "\t-d \t(for running optional visual debugger)" << endl;
  cout << "\t-m <map_filename>" << endl;
  cout << "\t-p <player_hostname>:<player_port>" << endl;
  cout << "\t-s <server_hostname>:<server_port>" << endl;
  exit(1);
}


int main(int argc, char **argv)
{

  bool visualDEBUG = false; 

  // for parsing command line flags. currently the only flag is to pass a map config file
  // usage: <exec> -m <mapfile>. to add more flags add it to the end of string followed
  // by a : or :: if the flag doesn't require an argument
  const char* optflags = "d::m:p:s:"; 
  int ch;

  // initialize default arguments
  int server_port = 6667;
  string server_hostname = "127.0.0.1";
  int player_port = 6665;
  string player_hostname = "127.0.0.1";

  ifstream mapFile; 

  if ( argc == 1 ) {
    cout << "no arguments provided" << endl;
    mapFile.open("config_files/map.conf", ios::in );
    if( !mapFile ) cout << "can't open file" << endl;
    readMapFile(mapFile); 
    mapFile.close();
    visualDEBUG = true; 
  }
  else {
    while ( -1 != ( ch = getopt( argc, argv, optflags ))){
      switch ( ch ) {
      case 'm': 
	cout << "mapfile: " << optarg << endl;
	mapFile.open(optarg, ios::in); 
	if( !mapFile ) {
	  cout << "Can't open map file: " << optarg << ". Aborted" << endl; 
	  exit(1);
	}
	readMapFile(mapFile); 
	mapFile.close();
	break;
      case 's':
	server_hostname = strtok(optarg, ":"); 
	cout << "server_hostname: " << server_hostname << endl;
	server_port = atoi(strtok(NULL, ":"));
	cout << "server_port: " << server_port << endl;
	break; 
      case 'p':
	player_hostname = strtok(optarg, ":"); 
	cout << "player_hostname: " << player_hostname << endl;
	player_port = atoi(strtok(NULL, ":"));
	cout << "player_port: " << player_port << endl;
	break;
      case 'd':
	visualDEBUG = true; 
	break;
      default:
	displayUsage(argc, argv);
	break;
      }
    }
  }

  bool connectedPlayer = false; 
  bool connectedCentralServer = false;

  // try to connect to player and central server
  //while ( !
  try{
    // connect to the player server
    PlayerClient pc(player_hostname, player_port);

    rbt = new Surveyor(myMap, &pc);  // this sets the interface to localization
       
    // startup the robot
    Robot robot(pc, rbt);
    
    // connect robot to the central server
    if (!robot.Connect(server_hostname, server_port)) {
      cerr << "Failed to establish a connection to the Central Server.\n"
	   << "Central Server hostname: " << server_hostname << "\n"
	   << "Central Server port: " << server_port << endl;
      exit(1);
    }
    
    mc = rbt->getMonteCarlo();
    
    Utils::initRandom();
    
    //Control rt(rbt);
    //boost::thread * commandThread = new boost::thread(rt);

    g = new Graph(myMap, true, 30);

    Node n; 
    planner = new PathPlanner(*g,n,n); 

    // select a behavior
    /*
    Loiter behavior(pc);
    robot.SetBehavior(&behavior);
    
    while (robot.GetState() != STATE_QUIT) {

      // Update Player interfaces.
      pc.ReadIfWaiting();
      
      // Update the robot.
      robot.Update();
      
      // Take a quick breath.
      usleep(1);
    }
    */

    //BehaviorTester bt(&robot, &pc); 
    //boost::thread * behaviorThread = new boost::thread(bt); 

    //PathTester tt(planner);
    //boost::thread * pathThread = new boost::thread(tt);

    if ( visualDEBUG) {
      debugger = new MonteCarloVisualDebugger();
      mc->setDebugger(debugger);

      glutInit(&argc, argv);
      glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB);
      glutInitWindowSize(myMap->getLength() + 100 * 2, myMap->getHeight() * 2);
      glutInitWindowPosition(100, 100);
      glutCreateWindow(argv[0]);
      init();
      //glutEstablishOverlay();
      // register callbacks
      glutDisplayFunc(draw);
      glutReshapeFunc(reshape);
      glutMouseFunc(mouse);
      glutKeyboardFunc(keyboard);
      //glutOverlayDisplayFunc(drawFog);
      //glutPostOverlayRedisplay();
     
      glutSetCursor(GLUT_CURSOR_CROSSHAIR);
      glutTimerFunc(100, displayObservations, 0);      
      glutMainLoop();  
    }

  } catch (PlayerError) {
    cerr << "Failed to establish a connection to the Player Server.\n"
	 << "Player Server hostname: " << player_hostname << "\n"
	 << "Player Server port: " << player_port << endl;
    exit(1);
  }
  
  return 0;
}