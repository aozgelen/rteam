#include "definitions.h"
#include "robot.h"
#include "loiter.h"
#include "wallAvoid.h"
#include "Controller.h"

#include "libplayerc++/playerc++.h"
using namespace PlayerCc;

#include "InterfaceToLocalization.h"
#include "VisualDebugger.h"
#include "Surveyor.h"
#include "Aibo.h"
#include <DataSerializer.h>
#include <MonteCarlo.h>

#include <cstdlib>
#include <iostream>
#include <sstream>
#include <unistd.h>
#include <cstring>
using namespace std;

Controller * ct; 
VisualDebugger * vD; 

Map * myMap;                           // for all robots
InterfaceToLocalization * rbt;         // specific to each robot? 

void init(void) {
  glClearColor(0.0, 0.0, 0.0, 0.0);
  glShadeModel(GL_FLAT);
}

//this method is called every couple of seconds and updates virtual robot position and
//MC.  Then it displays the current state of MC by indirectly calling "draw()".
void displayObservations(int unused) {
  glutPostRedisplay();
  glutTimerFunc(20, displayObservations, 0);
}

/* Wrapper functions for GLUT callback registers. functions of another class cannot be 
   used as functors. This is the workaround 
*/
void reshape(int w, int h){ vD->reshape(w,h); } 

void keyboard(unsigned char key, int x, int y){ vD->keyboard(key,x,y); } 

void keyboardSpecial(int key, int x, int y){ vD->keyboardSpecial(key,x,y); } 

void mouse(int button, int state, int x, int y){ vD->mouse(button, state, x, y); }

void draw(void){ vD->draw(); }

void drawFog(void){ vD->drawFog(); }
/* end wrapper functions */

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

  while( !mFile.eof() ) {
    cmd = ""; label = ""; x1 = 0 ; y1 = 0 ; x2 = 0 ; y2 = 0 ; 
    mFile >> cmd ; 

    // in case the file is empty create a default map
    if ( mFile.eof() ){
      if ( first ){
	cout << "Empty map file!..." << endl; 
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
  cout << "\t-f <config_filename>" << endl;
  exit(1);
}

int main(int argc, char **argv)
{
  Utils::initRandom();          // srand(time(NULL))
  bool visualDEBUG = false; 

  // usage: <exec> [-d] [-f <config-file>]. to add more flags add it to the end of string followed
  // by a : or :: if the flag doesn't require an argument
  const char* optflags = "d::f:"; 
  int ch;

  ifstream configFile;
  string central_server_hostname = "127.0.0.1"; 
  int central_server_port = 6667;
  string  player_hostname = "127.0.0.1"; 
  int player_port = 6665;
  string label, type; 
  
  if ( argc == 1 ) {
    cout << "no config file arguments provided" << endl;
    displayUsage(argc, argv);
    exit(1); 
  }
  else {
    while ( -1 != ( ch = getopt( argc, argv, optflags ))){
      switch ( ch ) {
      case 'f': {
	cout << "configuration file: " << optarg << endl;
	configFile.open(optarg, ios::in); 
	if( !configFile ) {
	  cout << "Can't open configuration file: " << optarg << ". Aborted" << endl; 
	  exit(1);
	}

	string  cmd, tmp, mfile; 

	// parse configuration file + attempt to connect the player and central servers
	while( !configFile.eof() ) {
	  cmd = ""; label = ""; type = ""; 
	  mfile = "map.conf";    
	  configFile >> cmd ; 
	  
	  // if the line is commented skip it otherwise process.
	  if (! ( cmd[0] == '/' && cmd[1] == '/' ) ){ 
	    if ( cmd == "central_server" ) {
	      configFile >> central_server_hostname >> central_server_port ;
	    }      
	    else if ( cmd == "map" ){
	      configFile >> mfile;
	      ifstream mapFile( mfile.c_str(), ios::in ); 
	      if ( !mapFile ) {
		cout << "Unable to open map file " <<  mfile << endl; 
		createMap_defaultField();
	      }
	      else {
		readMapFile(mapFile);
		mapFile.close();
	      }
	    }
	    else if ( cmd == "robot" ){ 
	      configFile >> label >> type >> player_hostname >> player_port ; 
	    }
	    else {
	      cout << "Unknown config command: " << cmd << endl;
	      getline(configFile, tmp); 
	    }
	  }
	  else {
	    // ignore the rest of the line.
	    getline(configFile, tmp); 
	  }
	} 

	configFile.close();
	break;
      }
      case 'd':
	visualDEBUG = true; 
	break;
      default:
	displayUsage(argc, argv);
	break;
      }
    }
  }

  try {
    // connect to the player server
    PlayerClient pc(player_hostname, player_port);

    rbt = new Surveyor(myMap);  // this sets the interface to localization

    // startup the robot
    Robot robot(pc, rbt, label, type);
    
    ct = new Controller(&pc, &robot, myMap, rbt);
    //ct = new Controller(&pc, myMap, rbt, label, type);
    
    // connect robot to the central server. nothing happens if it fails so be careful
    if (!ct->getRobot()->Connect(central_server_hostname, central_server_port)) {
      cerr << "Failed to establish a connection to the Central Server.\n"
	   << "Central Server hostname: " << central_server_hostname << "\n"
	   << "Central Server port: " << central_server_port << endl;
      exit(1);
    }

    boost::thread * controllerThread = new boost::thread(*ct); 

    if ( visualDEBUG ) {
      vD = new VisualDebugger(myMap, ct);
      
      glutInit(&argc, argv);
      glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB);
      glutInitWindowSize(myMap->getLength() * 2, myMap->getHeight() * 2);
      glutInitWindowPosition(100, 100);
      glutCreateWindow(argv[0]);
      init();
      //glutEstablishOverlay();

      // register callbacks
      glutDisplayFunc(draw);
      glutReshapeFunc(reshape);
      glutMouseFunc(mouse);
      glutKeyboardFunc(keyboard);
      glutSpecialFunc(keyboardSpecial);  // special key strokes arrows, shift etc. 
      //glutOverlayDisplayFunc(drawFog);
      //glutPostOverlayRedisplay();
      
      glutSetCursor(GLUT_CURSOR_CROSSHAIR);
      glutTimerFunc(100, displayObservations, 0);      
      glutMainLoop();  
    }
    controllerThread->join();
  
  }
  catch (PlayerError){
    cerr << "Failed to establish a connection to the Player Server.\n"
	 << "Robot: " << label << ", type:" << type << "\n"
	 << "Player Server hostname: " << player_hostname << "\n"
	 << "Player Server port: " << player_port << endl;
    exit(1);
  }

  return 0;
}
