/*
 * InterfaceToLocalization.cpp
 *
 */

#include "InterfaceToLocalization.h"

#define ITL_DEBUG false

InterfaceToLocalization::InterfaceToLocalization(Map * map, 
						 int fieldOfVision){
  // this way of locking and unlocking the mutex is error prone in the face of possible
  // exceptions thrown in between. instead use boost::mutex:scoped_lock lock(robotMutex) 
  // this doesn't require unlocking and releases lock upon object leaving scope. However,
  // it will only become a danger if there are multiple threads accessing these functions
  // for the same robot.
  robotMutex.lock();
  
  mc = new MonteCarlo(map);
  mc->setRandomCoefficients(Utils::toRadians(10), 10, Utils::toRadians(20), 20);
  observationVariance = 0;
	
  this->map = map;
  this->fov = fieldOfVision;
  
  destination = Position(0, 0, 0);
  destinationReached = false; 

  foundItem = false; 

  robotMutex.unlock();
}

void InterfaceToLocalization::setBlobFinderProxy(PlayerClient* pc) { bfp = new BlobfinderProxy(pc, 0); }

void InterfaceToLocalization::setPosition2dProxy(PlayerClient* pc) { p2d = new Position2dProxy(pc, 0); }

Position InterfaceToLocalization::getPosition() {
  Position pos ;
    
  //if ( !isDestinationSet() ) {
    robotMutex.lock(); 
    pos = mc->getPosition(); // in (cm)
    robotMutex.unlock();
    /*}
  else {  // startPos in (cm) and previousMove in (m), we need to return (cm)
    // convert the motion into global coordinate system using startPos angle. 
    double px = previousMove.getX(), py = previousMove.getY(), st = startPos.getTheta() ;
    double tx = px * cos(st) + py * -sin(st); 
    double ty = px * sin(st) + py * cos(st); 
    

    pos = Position( startPos.getX() + (tx * 100),
		    startPos.getY() + (ty * 100), 
		    startPos.getTheta() + previousMove.getTheta());
		    }*/
  return pos;
}


/* Updates the observations and the mc filter if the robot is on the move or 
 * changed it's position since last update 
 */
void InterfaceToLocalization::update() {
  if (!isDestinationSet()){
    updateObservations();
  }

  //if ( obs.size() > 0 ) 
  //  displayObservationSummary();
  
  Move lastMove = getLastMove();
  //if ( lastMove.getX() + lastMove.getY() + lastMove.getTheta() != 0 ) 
  //  cout << "lastMove(" << lastMove.getX() << "," << lastMove.getY() << "," << lastMove.getTheta() << ")" << endl ;
  if (obs.size() > 0 || lastMove.getX() + lastMove.getY() + lastMove.getTheta() != 0) {
    //cout << "applying filter" << endl;
    mc->updateFilter(lastMove, obs);
  }
}

void InterfaceToLocalization::move(Position relativePosition) {
  string label = "\tInterfaceToLocalization::move(destination)> ";

  robotMutex.lock();

  resetDestinationInfo();

  // convert x, y (cm) to (m)
  destination = Position(relativePosition.getX() / 100.0,
			 relativePosition.getY() / 100.0, 
			 relativePosition.getTheta());
  //destinationReached = false;

  //startPos = mc->getPosition();
  
  cout << label << "new dest(" 
       << destination.getX() << "," 
       << destination.getY() << "," 
       << destination.getTheta() << ") & startPos(" 
       << (int) startPos.getX() << "," 
       << (int) startPos.getY() << "," 
       << (int) startPos.getTheta() << ")" << endl;
    
  p2d->GoTo(destination.getX(), destination.getY(), destination.getTheta());
  robotMutex.unlock();
}

void InterfaceToLocalization::setSpeed(double xs, double ys, double ts){
  robotMutex.lock();
  cout << "setting speed(" << xs << ", " << ys << ", " << ts << ")" << endl;
  resetDestinationInfo(); 
  
  if ( xs < 0 ) 
    destination = Position(-1000, 0, 0); 
  else if ( xs > 0 )
    destination = Position(1000, 0, 0); 
  else if ( ys < 0 ) 
    destination = Position(0, -1000, 0); 
  else if ( ys > 0 )
    destination = Position(0, 1000, 0); 
  else if ( ts < 0 ) 
    destination = Position(0, 0, -M_PI * 100); 
  else if ( ts > 0 )
    destination = Position(0, 0, M_PI * 100); 
  
  p2d->SetSpeed(xs, ys, ts);
  robotMutex.unlock();
}

bool InterfaceToLocalization::isDestinationSet() {
  robotMutex.lock();
  bool destSet = !(destination == Position(0,0,0));
  robotMutex.unlock(); 
  
  return destSet; 
}

Move InterfaceToLocalization::getLastMove() {
  Move lastMove;
  string label = "\tITL::getLastMove()> " ;
  
  robotMutex.lock();

  if (destination == Position(0, 0, 0)){
    lastMove = Move(0, 0, 0);
  }
  else {
    double p2dX = p2d->GetXPos(); 
    double p2dY = p2d->GetYPos(); 
    double p2dYaw = p2d->GetYaw(); 
    if ( p2d->IsFresh() 
	 && ( p2dX + p2dY + p2dYaw ) != 0 
	 && !( p2dX == previousMove.getX() && p2dY == previousMove.getY() && p2dYaw == previousMove.getTheta() )){
      
      p2d->NotFresh();
      /* cout << label << "new reading, p2d: (" 
	   << p2dX << "," 
	   << p2dY << "," 
	   << p2dYaw << ")" 
	   << "\t previousMove: (" 
	   << previousMove.getX() << "," 
	   << previousMove.getY() << "," 
	   << previousMove.getTheta() << ")" << endl;
      */
      double delta_x = 0.0, delta_y = 0.0, delta_theta = 0.0;

      if (p2dX != 0) 
	delta_x = p2dX - previousMove.getX();
      if (p2dY != 0)
	delta_y = p2dY - previousMove.getY();
      if (p2dYaw != 0)
	delta_theta = p2dYaw - previousMove.getTheta();

      // convert to cm 
      // in order to update the particle locations properly we have to convert 
      if ( delta_x + delta_y != 0 ) {
	int delta = sqrt( pow(delta_x * 100, 2) + pow(delta_y * 100, 2) );       
	lastMove = Move( delta, 0, 0 ); 
      }
      else if ( delta_theta != 0 )
	lastMove = Move( 0, 0, delta_theta ); 

      //lastMove = Move(delta_x * 100 ,delta_y * 100 ,delta_theta);

      previousMove.setX(p2dX); 
      previousMove.setY(p2dY); 
      previousMove.setTheta(p2dYaw);
      
    }
    else {  // position not fresh so we didn't move
      lastMove = Move(0,0,0);
    }
  
    // check if the destination is reached
    if ( !destinationReached 
	 && abs(destination.getX() - previousMove.getX()) < 0.01 
	 && abs(destination.getY() - previousMove.getY()) < 0.01 
	 && abs(destination.getTheta() - previousMove.getTheta()) < 0.03 ){
      //cout << label << "destination reached." << endl;
      destinationReached = true; 
    }

  }
  robotMutex.unlock();

  return lastMove;
}

void InterfaceToLocalization::resetDestinationInfo() {
      destination = Position(0, 0, 0);
      previousMove = Position(0, 0, 0);
      p2d->ResetOdometry();
      destinationReached = false;
}

void InterfaceToLocalization::moveToMapPosition(int x, int y){
  Position pos = convertToRobotCoordinates(Position(x, y, 0)); 
  move(pos);
}

Position InterfaceToLocalization::convertToRobotCoordinates(Position mapPos){
  Position currPos = getPosition();  // in cm
      
  cout << "currPos(" 
       << currPos.getX() << ", " 
       << currPos.getY() << ", "
       << currPos.getTheta() << "-Degrees:" << Utils::toDegrees(currPos.getTheta()) << ")" << endl;
  
  // delta x, y from map perspective
  int tx = mapPos.getX() - currPos.getX() ;
  int ty = mapPos.getY() - currPos.getY() ;
  double theta = currPos.getTheta();
  
  // transform map ( x, y ) to robot ( x', y' )
  double nx = tx * cos(theta) + ty * sin(theta); 
  double ny = -tx * sin(theta) + ty * cos(theta); 
  double ntheta = calcHeadingToDestination(nx, ny);
  
  Position dest( (int) nx, 
		 (int) ny,
		 ntheta);
      
  cout << "destination relative to robot (" 
       << dest.getX() << ", " 
       << dest.getY() << ", " 
       << dest.getTheta() << "-Degrees:" << Utils::toDegrees(dest.getTheta()) << ")" << endl;
  
  return dest; 
}

Position InterfaceToLocalization::convertToMapCoordinates(Position robotPos){
  
}

// this function returns the angle between the robots current pose and the given relative position
// currently it checks the arctan of the location and adjusts to angle according to its quadrant
// TODO: check if this can be replaced with atan2
double InterfaceToLocalization::calcHeadingToDestination(double rx, double ry){
  // make sure the div by zero doesn't happen.
  if ( rx == 0 )
    rx = 0.01;

  double angle = atan(ry/rx);
  if ( rx < 0 && ry < 0 ) 
    angle = -1 * ( PI - angle ) ;
  if ( rx < 0 && ry > 0 )
    angle += PI ; 
 
  return angle; 
}

double InterfaceToLocalization::getAngle(double x) {
  int xRes = 160;
  int halfXRes = 80;
  
  double angleLeftOfCenter = -1 * (x - halfXRes) / xRes * Utils::toRadians(fov);
  return angleLeftOfCenter;
}

double InterfaceToLocalization::radiansToDegrees(double rad) {
  return rad * 180 / M_PI;
}
 
bool InterfaceToLocalization::positionEqual(Position p1, Position p2) {
  double absx = fabs(p1.getX() - p2.getX());
  double absy = fabs(p1.getY() - p2.getY());
  double abst = fabs(p1.getTheta() - p2.getTheta());
  
  return (absx == 0 && absy == 0 && Utils::toRadians(abst) == 0);
}


/****************************************************** BLOB PROCESSING **************************************************/


void InterfaceToLocalization::updateObservations() {
  robotMutex.lock();

  obs.clear();

  if (!bfp->IsFresh()) {
    robotMutex.unlock();
    return;
  }
  
  vector<observationBlob> P_blobs;
  vector<observationBlob> Y_blobs;
  vector<observationBlob> B_blobs;
  vector<observationBlob> G_blobs;
  vector<observationBlob> O_blobs;

  for (int i = 0; i < bfp->GetCount(); i++) {
    player_blobfinder_blob p_blob = bfp->GetBlob(i); 
    
    double bearing = getAngle((p_blob.left + p_blob.right) / 2);
    int color = getBlobColor(p_blob);

    observationBlob blob( color, 
			  p_blob.left, 
			  p_blob.right, 
			  p_blob.top, 
			  p_blob.bottom, 
			  bearing ) ;

    if (color == COLOR_PINK)
      P_blobs.push_back(blob);
    else if (color == COLOR_YELLOW)
      Y_blobs.push_back(blob);
    else if (color == COLOR_GREEN)
      G_blobs.push_back(blob);
    else if (color == COLOR_ORANGE)
      O_blobs.push_back(blob);
    else if (color == COLOR_BLUE) {
      B_blobs.push_back(blob);
    }
  }

  bfp->NotFresh();

  /* Process blobs: join overlapping blobs of the same color */
 
  cout << "initial blobs: " << endl;
  printBlobs(P_blobs); 
  printBlobs(Y_blobs); 
  printBlobs(B_blobs); 
  printBlobs(G_blobs); 
  printBlobs(O_blobs); 
    
  cout << "\njoining blobs: " << endl;
  int before, after; 
  do {
    before = P_blobs.size(); 
    joinBlobs(P_blobs);
    after = P_blobs.size(); 
  } while ( after != before ) ;
  do {
    before = Y_blobs.size(); 
    joinBlobs(Y_blobs);
    after = Y_blobs.size(); 
  } while ( after != before ) ;
  do {
    before = G_blobs.size(); 
    joinBlobs(G_blobs);
    after = G_blobs.size(); 
  } while ( after != before ) ;
  do {
    before = B_blobs.size(); 
    joinBlobs(B_blobs);
    after = B_blobs.size(); 
  } while ( after != before ) ;
  do {
    before = O_blobs.size(); 
    joinBlobs(O_blobs);
    after = O_blobs.size(); 
  } while ( after != before ) ;
  
  cout << "\nafter joining blobs: " << endl;
  printBlobs(P_blobs); 
  printBlobs(Y_blobs); 
  printBlobs(B_blobs); 
  printBlobs(G_blobs); 
  printBlobs(O_blobs); 
  
  cout << endl << endl;

  /* Calling order of finding marker functions is crucial!. Everytime one of these are called it erases the color 
     blobs from observations. correct order of search should be room (3 colors), corner (2 colors) 
     and enterance (1 color) fncts.
  */

  // room markers
  vector<Observation> BPY_markers = getRoomMarkers(B_blobs, P_blobs, Y_blobs, "b/p/y");
  vector<Observation> BYP_markers = getRoomMarkers(B_blobs, Y_blobs, P_blobs, "b/y/p");
  vector<Observation> BOP_markers = getRoomMarkers(B_blobs, O_blobs, P_blobs, "b/o/p");
  vector<Observation> BPO_markers = getRoomMarkers(B_blobs, P_blobs, O_blobs, "b/p/o");
  vector<Observation> BOY_markers = getRoomMarkers(B_blobs, O_blobs, Y_blobs, "b/o/y");
  vector<Observation> BYO_markers = getRoomMarkers(B_blobs, Y_blobs, O_blobs, "b/y/o");
  
  if ( !BPY_markers.empty() ) 
    obs.insert(obs.begin(), BPY_markers.begin(), BPY_markers.end());
  if ( !BYP_markers.empty() ) 
    obs.insert(obs.begin(), BYP_markers.begin(), BYP_markers.end());
  if ( !BOP_markers.empty() ) 
    obs.insert(obs.begin(), BOP_markers.begin(), BOP_markers.end());
  if ( !BPO_markers.empty() ) 
    obs.insert(obs.begin(), BPO_markers.begin(), BPO_markers.end());
  if ( !BOY_markers.empty() ) 
    obs.insert(obs.begin(), BOY_markers.begin(), BOY_markers.end());
  if ( !BYO_markers.empty() ) 
    obs.insert(obs.begin(), BYO_markers.begin(), BYO_markers.end());

  
  // corridor markers
  //vector<Observation> POY_markers = getRoomMarkers(P_blobs, O_blobs, Y_blobs, "p/o/y");
  //vector<Observation> YPO_markers = getRoomMarkers(Y_blobs, P_blobs, O_blobs, "y/p/o");
  vector<Observation> PYO_markers = getRoomMarkers(P_blobs, Y_blobs, O_blobs, "p/y/o");
  vector<Observation> YOP_markers = getRoomMarkers(Y_blobs, O_blobs, P_blobs, "y/o/p");

  //if ( !POY_markers.empty() ) 
  //   obs.insert(obs.begin(), POY_markers.begin(), POY_markers.end());
  //if ( !YPO_markers.empty() ) 
  //  obs.insert(obs.begin(), YPO_markers.begin(), YPO_markers.end());
  if ( !PYO_markers.empty() ) 
    obs.insert(obs.begin(), PYO_markers.begin(), PYO_markers.end());
  if ( !YOP_markers.empty() ) 
    obs.insert(obs.begin(), YOP_markers.begin(), YOP_markers.end());
 

  // corner markers
  vector<Observation> YB_markers = getCornerMarkers(Y_blobs, B_blobs, "y/b");
  vector<Observation> BY_markers = getCornerMarkers(B_blobs, Y_blobs, "b/y");
  vector<Observation> PY_markers = getCornerMarkers(P_blobs, Y_blobs, "p/y");
  vector<Observation> YP_markers = getCornerMarkers(Y_blobs, P_blobs, "y/p");

  if ( !BY_markers.empty() )   
    obs.insert(obs.begin(), BY_markers.begin(), BY_markers.end());
  if ( !YB_markers.empty() ) 
    obs.insert(obs.begin(), YB_markers.begin(), YB_markers.end());
  if ( !PY_markers.empty() ) 
    obs.insert(obs.begin(), PY_markers.begin(), PY_markers.end());
  if ( !YP_markers.empty() ) 
    obs.insert(obs.begin(), YP_markers.begin(), YP_markers.end());

  // enterance markers
  vector<Observation> B_markers = getEnteranceMarkers(B_blobs, "b");
  vector<Observation> O_markers = getEnteranceMarkers(O_blobs, "o");
  vector<Observation> P_markers = getEnteranceMarkers(P_blobs, "p");
  vector<Observation> Y_markers = getEnteranceMarkers(Y_blobs, "y");

  if ( !B_markers.empty() ) 
    obs.insert(obs.begin(), B_markers.begin(), B_markers.end());  
  if ( !O_markers.empty() ) 
    obs.insert(obs.begin(), O_markers.begin(), O_markers.end());
  if ( !P_markers.empty() ) 
    obs.insert(obs.begin(), P_markers.begin(), P_markers.end());  
  if ( !Y_markers.empty() ) 
    obs.insert(obs.begin(), Y_markers.begin(), Y_markers.end());  

  // G_markers blobs
  vector<Observation> G_markers = findGreenObjects(G_blobs, "g");
  obs.insert(obs.begin(), G_markers.begin(), G_markers.end());

  if ( G_markers.size() > 0 ){ 
    foundItem = true ; 
  }

  robotMutex.unlock();

  //if ( ITL_DEBUG )
  displayObservationSummary();
}

// this function should get the values for the colors from the player colors file. Currently it is hard coded
// the color values are display values designated by the user in the colors file, not the actual values observed by
// the blob finder proxy. In other words, blob.color returns the display value not the actual rgb observed. 
int InterfaceToLocalization::getBlobColor(player_blobfinder_blob blob) {
  uint32_t color = blob.color;
  int b = color % 256;
  color = color / 256;
  int g = color % 256;
  color = color / 256;
  int r = color % 256;
  
  if (r == 255 && g == 0 && b == 255)
    return COLOR_PINK;
  else if (r == 255 && g == 255 && b == 0)
    return COLOR_YELLOW;
  else if (r == 0 && g == 255 && b == 0)
    return COLOR_GREEN;
  else if (r == 0 && g == 0 && b == 255)
    return COLOR_BLUE;
  else if (r == 255 && g == 125 && b == 0)
    return COLOR_ORANGE;
  else {
    return -1;
  }
}

/* 
//BUGGY! Program chrashes with a SEGABRT when processing the blobs in this fashion. TODO: find out why
vector<Observation> InterfaceToLocalization::findRoomMarkersFromBlobs(vector<player_blobfinder_blob>& topBlobs,
								      vector<player_blobfinder_blob>& middleBlobs, 
								      vector<player_blobfinder_blob>& bottomBlobs, 
								      string id) {
  vector<Observation> ob;

  // process blobs: yellow and orange colors tend to get mixed up with one another. 
  // if there is a yellow or an orange blob in the searched marker. check to see if 
  // there also is the other in the observation. First step check if any of the colors
  // are in the observed blobs.
  //bool orangeOrYellow = false; 
  if ( !(topBlobs.empty() || middleBlobs.empty() || bottomBlobs.empty() )){
    for( int i = 0; i < topBlobs.size(); i++ ) {
      for ( int k = 0; k < middleBlobs.size(); k++ ) {
	for ( int j = 0; j < bottomBlobs.size(); j++ ) {
	
	  player_blobfinder_blob bottom = bottomBlobs[j];
	  player_blobfinder_blob middle = middleBlobs[k];
	  player_blobfinder_blob top = topBlobs[i]; 
	  
	  if (blobOnTopOf(top, middle) && blobOnTopOf(middle,bottom)) {
	    Observation observation = Observation(id, 
						  map,
						  this->getAngle((bottom.right + bottom.left + top.right +
								  top.left + middle.right + middle.left) / 6), 
						  observationVariance);
	    
	    ob.push_back(observation);
	    
	    topBlobs.erase(topBlobs.begin()+i);
	    i--;
	    bottomBlobs.erase(bottomBlobs.begin()+j);
	    j--;
	    middleBlobs.erase(middleBlobs.begin()+k);
	    k--;
	    if ( topBlobs.size()==0 || middleBlobs.size()==0 || bottomBlobs.size()==0 ) {
	      return ob; 
	    }
	  }
	}
      }
    }
  }
  return ob;
}
*/

vector<Observation> InterfaceToLocalization::getRoomMarkers(vector<observationBlob>& topBlobs,
								      vector<observationBlob>& middleBlobs, 
								      vector<observationBlob>& bottomBlobs, 
								      string id) {
  vector<Observation> ob;

  if ( isUsable(topBlobs) && isUsable(middleBlobs) && isUsable(bottomBlobs) ){
    for( int i = 0; i < topBlobs.size(); i++ ) {
      for ( int k = 0; k < middleBlobs.size(); k++ ) {
	for ( int j = 0; j < bottomBlobs.size(); j++ ) {
	  observationBlob& top = topBlobs[i]; 
	  observationBlob& middle = middleBlobs[k];
	  observationBlob& bottom = bottomBlobs[j];

	  if ( !top.Used() && !middle.Used() && !bottom.Used() ){
	    if (blobOnTopOf(top, middle) && blobOnTopOf(middle,bottom)) {
	      int avgLeft = ( top.Left() + middle.Left() + bottom.Left() ) / 3 ; 
	      int avgRight = ( top.Right() + middle.Right() + bottom.Right() ) / 3 ; 
	      //int avgCenter = ( avgLeft + avgRight ) / 2 ;

	      //double angleCenter = getAngle(avgCenter); 
	      double angleLeft, angleRight ;
	      ( avgLeft > 5 ) ? angleLeft = getAngle(avgLeft) : angleLeft = Observation::InvalidBearing ;
	      ( avgRight < 155 ) ? angleRight = getAngle(avgRight) : angleRight = Observation::InvalidBearing ;
	      Observation observation = Observation(id, 
						    map,
						    //angleCenter,
						    observationVariance,
						    angleLeft, 
						    angleRight);
	      
	      ob.push_back(observation);
	      top.setInUse(); 
	      middle.setInUse(); 
	      bottom.setInUse();	      
	      if ( !isUsable(topBlobs) || !isUsable(middleBlobs) || !isUsable(bottomBlobs) )
		return ob;
	    }
	  }
	}
      }
    }
  }
  return ob;
}

vector<Observation> InterfaceToLocalization::getCornerMarkers(vector<observationBlob>& topBlobs,
									vector<observationBlob>& bottomBlobs, 
									string id) {
  
  vector<Observation> ob;
  
  if ( isUsable(topBlobs) && isUsable(bottomBlobs) ){
    for( int i = 0; i < topBlobs.size(); i++ ){
      for ( int j = 0; j < bottomBlobs.size(); j++ ){
	observationBlob& bottom = bottomBlobs[j];
	observationBlob& top = topBlobs[i]; 
	
	// process blob: add the marker only if there isn't a significant gap between them 
	double topL = static_cast<double>(top.Bottom() - top.Top()); 
	double topW = static_cast<double>(top.Right() - top.Left()); 
	double bottomL = static_cast<double>(bottom.Bottom() - bottom.Top()); 
	double bottomW = static_cast<double>(bottom.Right() - bottom.Left()); 
	double eps = 0.5;
	if (blobOnTopOf(top, bottom) && (bottom.Top() - top.Bottom() < ( topL + bottomL )/2 )) {
	  //cout << "Corner blob found" << endl;
	  int avgLeft = ( top.Left() + bottom.Left() ) / 2 ; 
	  int avgRight = ( top.Right() + bottom.Right() ) / 2 ; 
	  //int avgCenter = ( avgLeft + avgRight ) / 2 ;
	  //cout << "avgLeft: " << avgLeft << ", avgRight: " << avgRight << /*", avgCenter: " << avgCenter << */endl;
	  
	  //double angleCenter = getAngle(avgCenter); 
	  double angleLeft, angleRight ;
	  ( avgLeft > 5 ) ? angleLeft = getAngle(avgLeft) : angleLeft = Observation::InvalidBearing ;
	  ( avgRight < 155 ) ? angleRight = getAngle(avgRight) : angleRight = Observation::InvalidBearing ;
	  //cout << "angleLeft: " << angleLeft << ", angleRight: " << angleRight << /*", avgCenter: " << angleCenter <<*/ endl;
	  Observation observation = Observation(id, 
						map,
						//angleCenter,
						observationVariance,
						angleLeft, 
						angleRight);

	  
	  ob.push_back(observation);
	  top.setInUse(); 
	  bottom.setInUse();
	  if ( !isUsable(topBlobs) || !isUsable(bottomBlobs) ){
	    return ob;
	  }
	}
      }
    }
  }
  
  return ob;
  
}

vector<Observation> InterfaceToLocalization::getEnteranceMarkers(vector<observationBlob>& blobs,
									   string id) {

  vector<Observation> ob;
  
  if ( isUsable(blobs) ) {
    for(int i = 0; i < blobs.size(); i++ ){
      observationBlob blob = blobs[i];
      
      // process blobs: check the length/width ratio. should be close to 2:1. just to be sure 1.5:1
      double d = static_cast<double>( blob.Bottom() - blob.Top() ) / ( blob.Right() - blob.Left() ); 

      if ( d > 1.75 ) {           
	//double angleCenter = getAngle((blob.Right() + blob.Left()) / 2); 
	double angleLeft, angleRight ;
	( blob.Left() > 1 ) ? angleLeft = getAngle(blob.Left()) : angleLeft = Observation::InvalidBearing ;
	( blob.Right() < 159 ) ? angleRight = getAngle(blob.Right()) : angleRight = Observation::InvalidBearing ;
	
	Observation observation = Observation(id, 
					      map,
					      //angleCenter,
					      observationVariance,
					      angleLeft, 
					      angleRight);
	
	ob.push_back(observation);
	blob.setInUse();
	if ( !isUsable(blobs) ) {
	  return ob;
	}
      }
    }
  }
  
  return ob;
}

vector<Observation> InterfaceToLocalization::findGreenObjects(vector<observationBlob>& blobs,
									   string id) {

  vector<Observation> ob;
  
  if ( isUsable(blobs) ) {
    for(int i = 0; i < blobs.size(); i++ ){
      observationBlob blob = blobs[i];
    
      if ( blob.Area() > 30 ) {           
	double angleLeft, angleRight ;
	( blob.Left() > 1 ) ? angleLeft = getAngle(blob.Left()) : angleLeft = Observation::InvalidBearing ;
	( blob.Right() < 159 ) ? angleRight = getAngle(blob.Right()) : angleRight = Observation::InvalidBearing ;
	Observation observation = Observation(id, 
					      map,
					      observationVariance,
					      angleLeft, 
					      angleRight);
      
	ob.push_back(observation);
	blob.setInUse();
	if ( !isUsable(blobs) ) {
	  return ob;
	}
      }
    }
  }
  
  return ob;
}

bool InterfaceToLocalization::blobOnTopOf(observationBlob top,
					  observationBlob bottom) {
  return (top.Left() < bottom.Right() && top.Right() > bottom.Left() && top.Bottom()
	  < bottom.Top() + 3 && top.Bottom() + 50 > bottom.Top());
}

void InterfaceToLocalization::printBlobs(vector<observationBlob>& blobs){
  if( !blobs.empty() ){
    cout << "Printing " ;
    blobs[0].printColor();
    cout << " blobs." << endl;
    for( vector<observationBlob>::iterator iter = blobs.begin(); iter != blobs.end(); iter++ )
      iter->print();
  }
}

void InterfaceToLocalization::joinBlobs(vector<observationBlob>& blobs){
  if ( !blobs.empty() ) {
    typedef vector<vector<observationBlob> > group_vector;
    group_vector blobGroup; 
    vector<observationBlob> newBlobs;
    
    // classify blobs together that are overlapping each other
    vector<observationBlob>::iterator iter;
    for ( iter = blobs.begin() ; iter != blobs.end() ; iter++ ) {
      if ( blobGroup.empty() ) {
	vector<observationBlob> t ; 
	t.push_back(*iter); 
	blobGroup.push_back(t); 
      }
      else {
	bool belongs = false; 
	group_vector::iterator iter2; 
	for ( iter2 = blobGroup.begin() ; iter2 != blobGroup.end() ; iter2++ ){
	  vector<observationBlob> tv = *iter2;
	  vector<observationBlob>::iterator iter3; 
	  for ( iter3 = tv.begin(); iter3 != tv.end() ; iter3++ ){
	    if ( isOverlapping( *iter, *iter3 ) ){
	      belongs = true; 
	      iter2->push_back(*iter);
	      break ; 
	    }
	  }
	  if ( belongs )
	    break ; 
	}
	
	// create new blobGroup if the blob doesn't overlap with existing blobs belonging to any group.
	if ( !belongs ) {
	  vector<observationBlob> t2 ; 
	  t2.push_back(*iter); 
	  blobGroup.push_back(t2); 
	}
      }
    }
  
    // join the blobs that belong to the same group
    group_vector::iterator it ; 
    for ( it = blobGroup.begin() ; it != blobGroup.end() ; it++ ){
      vector<observationBlob> group = *it;
      if ( group.size() > 1 ) {
	vector<observationBlob>::iterator it2;
	int minLeft = 1000, maxRight = 0, minTop = 1000, maxBottom = 0; 
	for ( it2 = group.begin(); it2 != group.end(); it2++ ) {
	  if ( it2->Left() < minLeft ) 
	    minLeft = it2->Left(); 
	  if ( it2->Right() > maxRight ) 
	    maxRight = it2->Right(); 
	  if ( it2->Top() < minTop ) 
	    minTop = it2->Top(); 
	  if ( it2->Bottom() > maxBottom )
	    maxBottom = it2->Bottom();
	}
	observationBlob nb(static_cast<int>(group.front().Color()), minLeft, maxRight, minTop, maxBottom, getAngle((minLeft + maxRight) / 2));
	cout << "new " ; 
	nb.print();
	newBlobs.push_back(nb); 
      }
      else {
	newBlobs.push_back(group.front()); 
      }
    }
    
    blobs = newBlobs;
  }
}

bool InterfaceToLocalization::isOverlapping(observationBlob b1, observationBlob b2 ){
  bool overlap = false; 
  /* For cases:      
     1.
     b1
     ----
     | -|--   
     --|- |   
       ----
         b2
  */
  if ( b2.Top() <= b1.Bottom()+1 && b2.Left() <= b1.Right()+1 && b1.Left() <= b2.Right()) 
    overlap = true; 
  /*
     2.
         b1
       ----
     --|- |	 
     | -|--
     ----
     b2
  */
  if ( b2.Top() <= b1.Bottom()+1 && b1.Left() <= b2.Right()+1 && b2.Left() <= b1.Right() ) 
    overlap = true; 
  /*
     3.
     b2
     ----
     | -|--   
     --|- |   
       ----
         b1
  */
  if ( b1.Top() <= b2.Bottom()+1 && b1.Left() <= b2.Right()+1 && b2.Left() <= b1.Right() )
    overlap = true; 
  /*
     4.
         b2
       ----
     --|- |	 
     | -|--
     ----
     b1
  */
  if ( b1.Top() <= b2.Bottom()+1 && b2.Left() <= b1.Right()+1 && b1.Left() <= b2.Right() )
    overlap = true; 

  return overlap;
}

void InterfaceToLocalization::displayObservationSummary(){
  if ( !obs.empty() ){
    cout << "************************* Observation Summary *************************** " << endl; 
    cout << "Confidence: " << getConfidence() << endl;
    vector<Observation>::iterator iter; 
    vector<Observation> roomMarkers; 
    vector<Observation> cornerMarkers; 
    vector<Observation> enteranceMarkers; 
    for ( iter = obs.begin(); iter != obs.end(); iter++ ){
      int numberOfBlobs = 1;
      string mark = iter->getMarkerId();
      while ( mark.find("/") != string::npos ){
	mark.replace(0, mark.find("/")+1, ""); 
	numberOfBlobs++; 
      }
      switch( numberOfBlobs ) {
      case 1: 
	enteranceMarkers.push_back(*iter); 
	break; 
      case 2: 
	cornerMarkers.push_back(*iter); 
	break; 
      case 3: 
	roomMarkers.push_back(*iter);
	break;
      }
    }
    
    if ( roomMarkers.size() != 0 ) {
      cout << "\t" << roomMarkers.size() << " room markers found: " ; 
      for ( vector<Observation>::iterator i = roomMarkers.begin(); i != roomMarkers.end(); i++ )
	cout << i->getMarkerId() << " " ; 
      cout << endl;
    }
    
    if ( cornerMarkers.size() != 0 ) {
      cout << "\t" << cornerMarkers.size() << " corner markers found: " ; 
      for ( vector<Observation>::iterator i = cornerMarkers.begin(); i != cornerMarkers.end(); i++ )
	cout << i->getMarkerId() << " " ; 
      cout << endl;
    }
    
    if ( enteranceMarkers.size() != 0 ) {
      cout << "\t" << enteranceMarkers.size() << " enterance markers found: " ; 
      for ( vector<Observation>::iterator i = enteranceMarkers.begin(); i != enteranceMarkers.end(); i++ )
	cout << i->getMarkerId() << " " ; 
      cout << endl;
    }
  }
}
