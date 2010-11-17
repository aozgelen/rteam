/*
 * InterfaceToLocalization.cpp
 *
 */

#include "InterfaceToLocalization.h"

#define ITL_DEBUG false
#define COLOR_PINK 0
#define COLOR_YELLOW 1
#define COLOR_BLUE 2
#define COLOR_GREEN 3
#define COLOR_ORANGE 4

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
  if ( lastMove.getX() + lastMove.getY() + lastMove.getTheta() != 0 ) 
    cout << "lastMove(" << lastMove.getX() << "," << lastMove.getY() << "," << lastMove.getTheta() << ")" << endl ;
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
			 relativePosition.getY() / 100, 
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

void InterfaceToLocalization::updateObservations() {
  robotMutex.lock();

  obs.clear();

  if (!bfp->IsFresh()) {
    robotMutex.unlock();
    return;
  }
  
  vector<player_blobfinder_blob> pinkBlobs;
  vector<player_blobfinder_blob> yellowBlobs;
  vector<player_blobfinder_blob> blueBlobs;
  vector<player_blobfinder_blob> greenBlobs;
  vector<player_blobfinder_blob> orangeBlobs;
  
  for (int i = 0; i < bfp->GetCount(); i++) {
    double bearing = getAngle((bfp->GetBlob(i).left + bfp->GetBlob(i).right) / 2);
    
    int color = getBlobColor(bfp->GetBlob(i));

    if (color == COLOR_PINK)
      pinkBlobs.push_back(bfp->GetBlob(i));
    else if (color == COLOR_YELLOW)
      yellowBlobs.push_back(bfp->GetBlob(i));
    else if (color == COLOR_GREEN)
      greenBlobs.push_back(bfp->GetBlob(i));
    else if (color == COLOR_ORANGE)
      orangeBlobs.push_back(bfp->GetBlob(i));
    else if (color == COLOR_BLUE) {
      blueBlobs.push_back(bfp->GetBlob(i));
    }
  }
  
  bfp->NotFresh();

  /* Process blobs: join overlapping blobs of the same color */
  /*
    joinBlobs(pinkBlobs); 
    joinBlobs(yellowBlobs); 
    joinBlobs(greenBlobs); 
    joinBlobs(blueBlobs); 
    joinBlobs(orangeBlobs); 
  */

  /* Calling order of finding marker functions is crucial!. Everytime one of these are called it erases the color 
     blobs from observations. correct order of search should be room (3 colors), corner (2 colors) 
     and enterance (1 color) fncts.
  */

  // room markers
  vector<Observation> blueOverPinkOverYellow = findRoomMarkersFromBlobs(blueBlobs, pinkBlobs, yellowBlobs, "b/p/y");
  vector<Observation> blueOverYellowOverPink = findRoomMarkersFromBlobs(blueBlobs, yellowBlobs, pinkBlobs, "b/y/p");
  vector<Observation> blueOverOrangeOverPink = findRoomMarkersFromBlobs(blueBlobs, orangeBlobs, pinkBlobs, "b/o/p");
  vector<Observation> blueOverPinkOverOrange = findRoomMarkersFromBlobs(blueBlobs, pinkBlobs, orangeBlobs, "b/p/o");
  vector<Observation> blueOverOrangeOverYellow = findRoomMarkersFromBlobs(blueBlobs, orangeBlobs, yellowBlobs, "b/o/y");
  vector<Observation> blueOverYellowOverOrange = findRoomMarkersFromBlobs(blueBlobs, yellowBlobs, orangeBlobs, "b/y/o");
  
  if ( !blueOverPinkOverYellow.empty() ) 
    obs.insert(obs.begin(), blueOverPinkOverYellow.begin(), blueOverPinkOverYellow.end());
  if ( !blueOverYellowOverPink.empty() ) 
    obs.insert(obs.begin(), blueOverYellowOverPink.begin(), blueOverYellowOverPink.end());
  if ( !blueOverOrangeOverPink.empty() ) 
    obs.insert(obs.begin(), blueOverOrangeOverPink.begin(), blueOverOrangeOverPink.end());
  if ( !blueOverPinkOverOrange.empty() ) 
    obs.insert(obs.begin(), blueOverPinkOverOrange.begin(), blueOverPinkOverOrange.end());
  if ( !blueOverOrangeOverYellow.empty() ) 
    obs.insert(obs.begin(), blueOverOrangeOverYellow.begin(), blueOverOrangeOverYellow.end());
  if ( !blueOverYellowOverOrange.empty() ) 
    obs.insert(obs.begin(), blueOverYellowOverOrange.begin(), blueOverYellowOverOrange.end());

  // corridor markers
  vector<Observation> pinkOverOrangeOverYellow = findRoomMarkersFromBlobs(pinkBlobs, orangeBlobs, yellowBlobs, "p/o/y");
  vector<Observation> yellowOverPinkOverOrange = findRoomMarkersFromBlobs(yellowBlobs, pinkBlobs, orangeBlobs, "y/p/o");
  vector<Observation> pinkOverYellowOverOrange = findRoomMarkersFromBlobs(pinkBlobs, yellowBlobs, orangeBlobs, "p/y/o");
  vector<Observation> yellowOverOrangeOverPink = findRoomMarkersFromBlobs(yellowBlobs, orangeBlobs, pinkBlobs, "y/o/p");

  if ( !pinkOverOrangeOverYellow.empty() ) 
    obs.insert(obs.begin(), pinkOverOrangeOverYellow.begin(), pinkOverOrangeOverYellow.end());
  if ( !yellowOverPinkOverOrange.empty() ) 
    obs.insert(obs.begin(), yellowOverPinkOverOrange.begin(), yellowOverPinkOverOrange.end());
  if ( !pinkOverYellowOverOrange.empty() ) 
    obs.insert(obs.begin(), pinkOverYellowOverOrange.begin(), pinkOverYellowOverOrange.end());
  if ( !yellowOverOrangeOverPink.empty() ) 
    obs.insert(obs.begin(), yellowOverOrangeOverPink.begin(), yellowOverOrangeOverPink.end());

  // corner markers
  vector<Observation> yellowOverBlue = findCornerMarkersFromBlobs(yellowBlobs, blueBlobs, "y/b");
  vector<Observation> blueOverYellow = findCornerMarkersFromBlobs(blueBlobs, yellowBlobs, "b/y");
  vector<Observation> pinkOverYellow = findCornerMarkersFromBlobs(pinkBlobs, yellowBlobs, "p/y");
  vector<Observation> yellowOverPink = findCornerMarkersFromBlobs(yellowBlobs, pinkBlobs, "y/p");

  if ( !blueOverYellow.empty() )   
    obs.insert(obs.begin(), blueOverYellow.begin(), blueOverYellow.end());
  if ( !yellowOverBlue.empty() ) 
    obs.insert(obs.begin(), yellowOverBlue.begin(), yellowOverBlue.end());
  if ( !pinkOverYellow.empty() ) 
    obs.insert(obs.begin(), pinkOverYellow.begin(), pinkOverYellow.end());
  if ( !yellowOverPink.empty() ) 
    obs.insert(obs.begin(), yellowOverPink.begin(), yellowOverPink.end());

  // enterance markers
  vector<Observation> blue = findEnteranceMarkersFromBlobs(blueBlobs, "b");
  vector<Observation> orange = findEnteranceMarkersFromBlobs(orangeBlobs, "o");

  if ( !blue.empty() ) 
    obs.insert(obs.begin(), blue.begin(), blue.end());  
  if ( !orange.empty() ) 
    obs.insert(obs.begin(), orange.begin(), orange.end());

  // green blobs
  vector<Observation> green = findGreenBlobs(greenBlobs, "g");
  obs.insert(obs.begin(), green.begin(), green.end());

  if ( green.size() > 0 ){ 
    foundItem = true ; 
  }

  if ( ITL_DEBUG )
    displayObservationSummary();

  //cout << "unlocking robotMutex" << endl; 
  robotMutex.unlock();
  //cout << "leaving ITL" << endl; 
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
    /*if ( getBlobColor(topBlobs[0]) == COLOR_YELLOW || 
	 getBlobColor(middleBlobs[0]) == COLOR_YELLOW || 
	 getBlobColor(middleBlobs[0]) == COLOR_ORANGE || 
	 getBlobColor(bottomBlobs[0]) == COLOR_YELLOW || 
	 getBlobColor(bottomBlobs[0]) == COLOR_ORANGE )  
      orangeOrYellow = true;
    */
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

vector<Observation> InterfaceToLocalization::findCornerMarkersFromBlobs(vector<player_blobfinder_blob>& topBlobs,
									vector<player_blobfinder_blob>& bottomBlobs, 
									string id) {

  vector<Observation> ob;
 
  if ( !( topBlobs.empty() || bottomBlobs.empty() ) ){
    for( int i = 0; i < topBlobs.size(); i++ ){
      for ( int j = 0; j < bottomBlobs.size(); j++ ){
	player_blobfinder_blob bottom = bottomBlobs[j];
	player_blobfinder_blob top = topBlobs[i]; 
	
	// process blob: add the marker only if there isn't a significant gap between them 
	double topL = static_cast<double>(top.bottom - top.top); 
	double topW = static_cast<double>(top.right - top.left); 
	double bottomL = static_cast<double>(bottom.bottom - bottom.top); 
	double bottomW = static_cast<double>(bottom.right - bottom.left); 
	double eps = 0.5;
	if (blobOnTopOf(top, bottom) && (bottom.top - top.bottom < ( topL + bottomL )/2 )) {
	  Observation observation = Observation(id, 
						map,
						this->getAngle((bottom.right + bottom.left + top.right + top.left) / 4), 
						observationVariance);
	  
	  ob.push_back(observation);
	  
	  topBlobs.erase(topBlobs.begin()+i);
	  i--;
	  bottomBlobs.erase(bottomBlobs.begin()+j);
	  j--;
	  if ( topBlobs.size()==0 || bottomBlobs.size()==0 ){
	    return ob;
	  }
	}
      }
    }
  }
  return ob;
}

vector<Observation> InterfaceToLocalization::findEnteranceMarkersFromBlobs(vector<player_blobfinder_blob>& blobs,
									   string id) {

  vector<Observation> ob;

  if ( !blobs.empty() ) {
    for(int i = 0; i < blobs.size(); i++ ){
      player_blobfinder_blob blob = blobs[i];
      
      // process blobs: check the length/width ratio. should be close to 2:1. just to be sure 1.5:1
      double d = static_cast<double>( blob.bottom - blob.top ) / ( blob.right - blob.left ); 

      if ( d > 1.75 ) {           
	Observation observation = Observation(id, 
					      map,
					      this->getAngle((blob.right + blob.left) / 2), 
					      observationVariance);
	
	ob.push_back(observation);
	
	blobs.erase(blobs.begin()+i);
	i--;
	if ( blobs.size() == 0 ) {
	  return ob;
	}
      }
    }
  }
  return ob;
}

vector<Observation> InterfaceToLocalization::findGreenBlobs(vector<player_blobfinder_blob>& blobs,
									   string id) {

  vector<Observation> ob;
  
  if ( !blobs.empty() ) {
    for(int i = 0; i < blobs.size(); i++ ){
      player_blobfinder_blob blob = blobs[i];
    
      if ( blob.area > 30 ) {           
	Observation observation = Observation(id, 
					      map,
					      this->getAngle((blob.right + blob.left) / 2), 
					      observationVariance);
      
	ob.push_back(observation);
	
	blobs.erase(blobs.begin()+i, blobs.begin()+i+1);
	i--;
	if ( blobs.size() == 0 ) {
	  return ob;
	}
      }
    }
  }
  return ob;
}

bool InterfaceToLocalization::blobOnTopOf(player_blobfinder_blob top,
					  player_blobfinder_blob bottom) {
  return (top.left < bottom.right && 
	  top.right > bottom.left && 
	  top.bottom < bottom.top + 3 && 
	  top.bottom + 50 > bottom.top);
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

void InterfaceToLocalization::printBlobs(vector<player_blobfinder_blob>& blobs){
  if( !blobs.empty() ){
    cout << "Printing " ;
    printBlobColor(blobs[0]);
    cout << " blobs." << endl;
    for( vector<player_blobfinder_blob>::iterator iter = blobs.begin(); iter != blobs.end(); iter++ )
      printBlobInfo(*iter);
  }
}

void InterfaceToLocalization::printBlobColor(player_blobfinder_blob b){
  switch( getBlobColor(b) ){
  case 0:
    cout << "pink" ; 
    break; 
  case 1: 
    cout << "yellow" ; 
    break;
  case 2: 
    cout << "blue" ; 
    break;
  case 3: 
    cout << "green" ; 
    break;
  case 4: 
    cout << "orange" ; 
    break;
  }
}

void InterfaceToLocalization::printBlobInfo(player_blobfinder_blob blob){
  cout << "blob is at (" << blob.x << "," << blob.y << "), color:" ; 
  switch ( getBlobColor(blob) ) {
  case COLOR_PINK: 
    cout << "pink " ; 
    break; 
  case COLOR_ORANGE:
    cout << "orange " ; 
    break; 
  case COLOR_YELLOW:
    cout << "yellow " ;
    break;
  case COLOR_BLUE: 
    cout << "blue ";
  }
  cout << ", area:" << blob.area << ", top:" << blob.top << ", bottom:" << blob.bottom 
       << ", left:" << blob.left << ", right:" << blob.right << endl;
}

// to go backwards
void InterfaceToLocalization::setSpeed(double xs, double ys, double ts){
  robotMutex.lock();
  p2d->SetSpeed(xs, ys, ts);
  robotMutex.unlock();
}

// Fix this
void InterfaceToLocalization::joinBlobs(vector<player_blobfinder_blob>& blobs){
  vector<player_blobfinder_blob> newBlobs = blobs, tempBlobs;
  bool needProcessing = true ;
  while ( needProcessing ) {
    needProcessing = false; 
    for( vector<player_blobfinder_blob>::iterator iter1 = newBlobs.begin(); iter1 != newBlobs.end(); iter1++ )
      for ( vector<player_blobfinder_blob>::iterator iter2 = iter1 + 1; iter2 != newBlobs.end(); iter2++ ){
	if ( isOverlapping( *iter1, *iter2 ) ){
	  // form new blob 
	  int nleft = static_cast<int>( PlayerCc::min( iter1->left, iter2->left )) ; 
	  int nright = static_cast<int>( PlayerCc::max( iter1->right, iter2->right )); 
	  int ntop = static_cast<int>( PlayerCc::min( iter1->top, iter2->top )); 
	  int nbottom = static_cast<int>( PlayerCc::max( iter1->bottom, iter2->bottom ));
	  int nx = (nright + nleft)/2 ;
	  int ny = (ntop + nbottom)/2 ;
	  int narea = ( nright - nleft ) * ( nbottom - ntop );
	  blobfinder_blob nb(static_cast<int>(iter1->id), static_cast<int>(iter1->color), 
			     narea, nx, ny, nleft, nright, ntop, nbottom, iter1->range);
	  // add to tempBlobs 
	  tempBlobs.push_back(nb);
	  needProcessing = true ; 
	}
      }
    newBlobs = tempBlobs;
  }
  blobs = newBlobs; 
}

bool InterfaceToLocalization::isOverlapping(player_blobfinder_blob b1, player_blobfinder_blob b2 ){
  bool overlap = false; 
  // set the epsilon to be the %35 of the average height and width of these blobs.
  // why %35? why not.
  double hEps = ( static_cast<double>((b1.bottom - b1.top) + (b2.bottom - b2.top)) / 2) * 0.35;
  double lEps = ( static_cast<double>((b1.right - b1.left) + (b2.right - b2.left)) / 2) * 0.35; 

  /* For cases: 
     
     1.
     b1
     ----
     | -|--   
     --|- |   
       ----
         b2

     2.
         b1
       ----
     --|- |	 
     | -|--
     ----
     b2

     3.
     b2
     ----
     | -|--   
     --|- |   
       ----
         b1

     4.
         b2
       ----
     --|- |	 
     | -|--
     ----
     b1
  */
  if ( b2.top < b1.bottom - hEps && b2.left + lEps < b1.right ) 
    overlap = true; 
  if ( b2.top < b1.bottom - hEps && b1.left + lEps < b2.right ) 
    overlap = true; 
  if ( b1.top < b2.bottom - hEps && b1.left + lEps < b2.right )
    overlap = true; 
  if ( b1.top < b2.bottom - hEps && b2.left + lEps < b1.right )
    overlap = true; 
}

void InterfaceToLocalization::displayObservationSummary(){
  string label = "\tInterfaceToLocalization::displayObservationSummary()> " ;
  
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
    
    //if ( !obs.empty() )
    //cout << endl;
  }
  else{
    //cout << label << "No observations are available at this time!." << endl;
  }
}
