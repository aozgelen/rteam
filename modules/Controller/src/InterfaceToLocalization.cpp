/*
 * InterfaceToLocalization.cpp
 *
 * Author: George Rabanca 
 *         Tuna Ozgelen (currently maintaining)
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
  cumulativeMove = Position(0, 0, 0);
  
  foundItem = false; 

  robotMutex.unlock();
}

void InterfaceToLocalization::setBlobFinderProxy(PlayerClient* pc) { bfp = new BlobfinderProxy(pc, 0); }

void InterfaceToLocalization::setPosition2dProxy(PlayerClient* pc) { p2d = new Position2dProxy(pc, 0); }


Position InterfaceToLocalization::getPosition() {
  Position pos ;
  if ( !isDestinationSet() ) {
    robotMutex.lock(); 
    pos = mc->getPosition(); // in (cm)
    robotMutex.unlock();
  }
  else {  // startPos in (cm) and cumulativeMove in (m), we need to return (cm)
    pos = Position( startPos.getX() + (cumulativeMove.getX() * 100),
		    startPos.getY() + (cumulativeMove.getY() * 100), 
		    startPos.getTheta() + cumulativeMove.getTheta());
  }
  return pos;
}


/* Updates the observations and the mc filter if the robot is on the move or 
   changed it's position since last update 
*/
void InterfaceToLocalization::update() {

  string label = "\tInterfaceToLocalization::update()> ";

  if (!isDestinationSet()){
    //cout<< label << "updateObservations" << endl;
     updateObservations();
   }

  Move lastMove = getLastMove();
  if ( isDestinationSet() && (lastMove.getX() + lastMove.getTheta() != 0) )
    cout << label << "last move(" << lastMove.getX() << "," 
	 << lastMove.getY() << "," << lastMove.getTheta() << ")" << endl;
  //displayObservationSummary();
  if (obs.size() > 0 || lastMove.getX() + lastMove.getTheta() != 0) {
    mc->updateFilter(lastMove, obs);
  }
}

void InterfaceToLocalization::move(Position relativePosition) {
  string label = "\tInterfaceToLocalization::move(destination)> ";

  robotMutex.lock();
  // convert x, y (cm) to (m)
  destination = Position(relativePosition.getX() / 100.0,
			 relativePosition.getY() / 100, 
			 relativePosition.getTheta());
  
  cout << label << "new destination: (" 
       << destination.getX() << "," 
       << destination.getY() << "," 
       << destination.getTheta() << ")" << endl;
  
  // set the starting position
  startPos = mc->getPosition();
  cout << label << "start position set to: (" 
       << startPos.getX() << "," 
       << startPos.getY() << "," 
       << startPos.getTheta() << ")" << endl;
    
  p2d->ResetOdometry();
  p2d->GoTo(destination.getX(), destination.getY(), destination.getTheta());
  cumulativeMove = Position(0, 0, 0);
  robotMutex.unlock();
}

// this should replace isMoving. isMoving is returning this exact same thing
// only because the info retreived from player about the motion is not reliable.
bool InterfaceToLocalization::isDestinationSet() {
  robotMutex.lock();
  bool destSet = !(destination == Position(0,0,0));
  robotMutex.unlock(); 
  
  return destSet; 
}

bool InterfaceToLocalization::isMoving() {
  robotMutex.lock();
  bool isMoving = !(destination == Position(0, 0, 0));
  robotMutex.unlock();
  if (isMoving)
    cout << "Moving..." << endl;
  
  return isMoving;
}

Move InterfaceToLocalization::getLastMove() {
  Move lastMove;
  string label = "\tITL::getLastMove()> " ;
  
  robotMutex.lock();

  if (destination == Position(0, 0, 0)){
    //cout << label << "destination is not set. last move is (0,0,0)" << endl;
    lastMove = Move(0, 0, 0);
  }
  else {
    if (p2d->IsFresh()) {
      
      p2d->NotFresh();
      cout << label << "new reading, p2d: (" 
	   << p2d->GetXPos() << "," 
	   << p2d->GetYPos() << "," 
	   << p2d->GetYaw() << ")" 
	   << "\t cumulativeMove: (" 
	   << cumulativeMove.getX() << "," 
	   << cumulativeMove.getY() << "," 
	   << cumulativeMove.getTheta() << ")" << endl;
      
      // this piece is contradictory to what cumulativeMove holds. 
      // what this does is gets the difference between the odometry 
      // and the last update to get our lastMove. but the problem is
      // the cumulativeMove is in global coordinate system and p2d 
      // is in local one.
      
      // trial: convert the cumulativeMove to robots local coordinates
      
      /*double locx = (cumulativeMove.getX() * cos(cumulativeMove.getTheta()) 
		   + cumulativeMove.getY() * sin(cumulativeMove.getTheta()));
      double locy = (-cumulativeMove.getX() * sin(cumulativeMove.getTheta()) 
		   + cumulativeMove.getY() * cos(cumulativeMove.getTheta()));
      
      double x = p2d->GetXPos() - locx;
      double y = p2d->GetYPos() - locy;
      */
      double x = p2d->GetXPos() - cumulativeMove.getX();
      double y = p2d->GetYPos() - cumulativeMove.getY();
      double theta = p2d->GetYaw() - cumulativeMove.getTheta();
      
      // converted to cm 
      lastMove = Move(x * 100 ,y * 100 ,theta);
      
      // this is supposed to take the move convert it to global coordinate
      // system. direct addition of these coordinates to start position 
      // should give us our current location on the map.
      cumulativeMove.moveRelative(Move(x, y, theta));
    }
    else {  // position not fresh so we didn't move
      lastMove = Move(0,0,0);
    }
  
    // check if the destination is reached
    if ( positionEqual(destination, cumulativeMove) ){
      cout << label << "destination reached." << endl;
      destination = Position(0, 0, 0);
      cumulativeMove = Position(0, 0, 0);
      p2d->ResetOdometry();
    }

  }
  robotMutex.unlock();

  return lastMove;
}

void InterfaceToLocalization::moveToMapPosition(Position mapPos){

}

Position InterfaceToLocalization::convertToRobotCoordinates(Position mapPos){

}

Position InterfaceToLocalization::convertToMapCoordinates(Position robotPos){

}

void InterfaceToLocalization::updateObservations() {
  robotMutex.lock();

  obs.clear();

  if ( ITL_DEBUG )
    cout << endl << " ************************************ Observation Update **********************************" << endl;

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
  if (ITL_DEBUG) {
    printBlobs(pinkBlobs); 
    printBlobs(yellowBlobs); 
    printBlobs(greenBlobs); 
    printBlobs(blueBlobs); 
    printBlobs(orangeBlobs); 
  }

  /*
    joinBlobs(pinkBlobs); 
    joinBlobs(yellowBlobs); 
    joinBlobs(greenBlobs); 
    joinBlobs(blueBlobs); 
    joinBlobs(orangeBlobs); 
  */

  if (ITL_DEBUG) {
    printBlobs(pinkBlobs); 
    printBlobs(yellowBlobs); 
    printBlobs(greenBlobs); 
    printBlobs(blueBlobs); 
    printBlobs(orangeBlobs); 
  }

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
  
  if ( ITL_DEBUG ){
    cout << "checked room markers" << endl; 
    cout << "bpy:" << blueOverPinkOverYellow.size() << ", byp:" << blueOverYellowOverPink.size()
	 << ", bop:" << blueOverOrangeOverPink.size() << ", bpo:" << blueOverPinkOverOrange.size()
	 << ", boy:" << blueOverOrangeOverYellow.size() << ", byo:" << blueOverYellowOverOrange.size() << endl;
  }

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

  if ( ITL_DEBUG ) 
    cout << "size of obs: " << obs.size() << endl;

  // corridor markers
  vector<Observation> pinkOverOrangeOverYellow = findRoomMarkersFromBlobs(pinkBlobs, orangeBlobs, yellowBlobs, "p/o/y");
  vector<Observation> yellowOverPinkOverOrange = findRoomMarkersFromBlobs(yellowBlobs, pinkBlobs, orangeBlobs, "y/p/o");
  vector<Observation> pinkOverYellowOverOrange = findRoomMarkersFromBlobs(pinkBlobs, yellowBlobs, orangeBlobs, "p/y/o");
  vector<Observation> yellowOverOrangeOverPink = findRoomMarkersFromBlobs(yellowBlobs, orangeBlobs, pinkBlobs, "y/o/p");

  if ( ITL_DEBUG ){
    cout << "checked corridor markers" << endl; 
    cout << "poy:" << pinkOverOrangeOverYellow.size() << ", ypo:" << yellowOverPinkOverOrange.size()
	 << ", pyo:" << pinkOverYellowOverOrange.size() << ", yop:" << yellowOverOrangeOverPink.size() << endl;
  }

  if ( !pinkOverOrangeOverYellow.empty() ) 
    obs.insert(obs.begin(), pinkOverOrangeOverYellow.begin(), pinkOverOrangeOverYellow.end());
  if ( !yellowOverPinkOverOrange.empty() ) 
    obs.insert(obs.begin(), yellowOverPinkOverOrange.begin(), yellowOverPinkOverOrange.end());
  if ( !pinkOverYellowOverOrange.empty() ) 
    obs.insert(obs.begin(), pinkOverYellowOverOrange.begin(), pinkOverYellowOverOrange.end());
  if ( !yellowOverOrangeOverPink.empty() ) 
    obs.insert(obs.begin(), yellowOverOrangeOverPink.begin(), yellowOverOrangeOverPink.end());

  if ( ITL_DEBUG )
    cout << "size of obs: " << obs.size() << endl;

  // corner markers
  vector<Observation> yellowOverBlue = findCornerMarkersFromBlobs(yellowBlobs, blueBlobs, "y/b");
  vector<Observation> blueOverYellow = findCornerMarkersFromBlobs(blueBlobs, yellowBlobs, "b/y");
  vector<Observation> pinkOverYellow = findCornerMarkersFromBlobs(pinkBlobs, yellowBlobs, "p/y");
  vector<Observation> yellowOverPink = findCornerMarkersFromBlobs(yellowBlobs, pinkBlobs, "y/p");

  if ( ITL_DEBUG ) {
    cout << "checked corner markers" << endl; 
    cout << "yb:" << yellowOverBlue.size() << ", by:" << blueOverYellow.size()
	 << ", py:" << pinkOverYellow.size() << ", yp:" << yellowOverPink.size() << endl;
  }

  if ( !blueOverYellow.empty() )   
    obs.insert(obs.begin(), blueOverYellow.begin(), blueOverYellow.end());
  if ( !yellowOverBlue.empty() ) 
    obs.insert(obs.begin(), yellowOverBlue.begin(), yellowOverBlue.end());
  if ( !pinkOverYellow.empty() ) 
    obs.insert(obs.begin(), pinkOverYellow.begin(), pinkOverYellow.end());
  if ( !yellowOverPink.empty() ) 
    obs.insert(obs.begin(), yellowOverPink.begin(), yellowOverPink.end());

  if ( ITL_DEBUG ) 
    cout << "size of obs: " << obs.size() << endl;

  // enterance markers
  vector<Observation> blue = findEnteranceMarkersFromBlobs(blueBlobs, "b");
  vector<Observation> orange = findEnteranceMarkersFromBlobs(orangeBlobs, "o");

  if ( ITL_DEBUG ) {
    cout << "checked enterance markers" << endl; 
    cout << "b:" << blue.size() << ", o:" << orange.size() << endl;
  }

  if ( !blue.empty() ) 
    obs.insert(obs.begin(), blue.begin(), blue.end());  
  if ( !orange.empty() ) 
    obs.insert(obs.begin(), orange.begin(), orange.end());

  // see if there are any green blobs
  vector<Observation> green = findGreenBlobs(greenBlobs, "g");
  obs.insert(obs.begin(), green.begin(), green.end());
  if ( green.size() > 0 ){ 
    foundItem = true ; 
    if ( ITL_DEBUG )
      cout << "found green blob" << endl;
  }

  if ( ITL_DEBUG ){
    cout << "size of obs: " << obs.size() << endl;

    for ( int i = 0; i < obs.size(); i++ ) {
      cout << "obs " << i << " is " << obs[i].getMarkerId() << endl;
    }
  }

  if ( ITL_DEBUG )
    displayObservationSummary();
  
  robotMutex.unlock();
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

  if ( ITL_DEBUG )
    cout << "checking 3 color markers :" << id << endl;
  
  vector<Observation> ob;

  // process blobs: yellow and orange colors tend to get mixed up with one another. 
  // if there is a yellow or an orange blob in the searched marker. check to see if 
  // there also is the other in the observation. First step check if any of the colors
  // are in the observed blobs.
  bool orangeOrYellow = false; 
  if ( !(topBlobs.empty() || middleBlobs.empty() || bottomBlobs.empty() ))
    if ( getBlobColor(topBlobs[0]) == COLOR_YELLOW || 
	 getBlobColor(middleBlobs[0]) == COLOR_YELLOW || 
	 getBlobColor(middleBlobs[0]) == COLOR_ORANGE || 
	 getBlobColor(bottomBlobs[0]) == COLOR_YELLOW || 
	 getBlobColor(bottomBlobs[0]) == COLOR_ORANGE )  
      orangeOrYellow = true;
  
  for( int i = 0; i < topBlobs.size(); i++ ) {
    for ( int k = 0; k < middleBlobs.size(); k++ ) {
      for ( int j = 0; j < bottomBlobs.size(); j++ ) {
	
	player_blobfinder_blob bottom = bottomBlobs[j];
	player_blobfinder_blob middle = middleBlobs[k];
	player_blobfinder_blob top = topBlobs[i]; 
	
	if ( ITL_DEBUG ) {
	  cout << "top "; 
	  printBlobInfo(top); 
	  cout << "middle "; 
	  printBlobInfo(middle); 
	  cout << "bottom "; 
	  printBlobInfo(bottom);
	} 
	
	if (blobOnTopOf(top, middle) && blobOnTopOf(middle,bottom)) {
	  // process blob: if there is orange or yellow...TO DO
	  
	  if ( ITL_DEBUG ) 
	    cout << "top, middle and bottom blobs are on top of each other. room marker found!" << endl;
	  Observation observation = Observation(id, 
						map,
						this->getAngle((bottom.right + bottom.left + top.right +
								top.left + middle.right + middle.left) / 6), 
						observationVariance);
	    
	  ob.push_back(observation);
	  
	  topBlobs.erase(topBlobs.begin()+i, topBlobs.begin()+i+1);
	  i--;
	  bottomBlobs.erase(bottomBlobs.begin()+j, bottomBlobs.begin()+j+1);
	  j--;
	  middleBlobs.erase(middleBlobs.begin()+k, middleBlobs.begin()+k+1);
	  k--;
	  if ( topBlobs.size()==0 || middleBlobs.size()==0 || bottomBlobs.size()==0 ) {
	    if ( ITL_DEBUG )
	      cout << "one of the blob vectors is empty. jumping out of this function! " << endl; 
	    return ob; 
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

  if ( ITL_DEBUG )
    cout << "checking 2 color markers" << endl;
  vector<Observation> ob;
 
  for( int i = 0; i < topBlobs.size(); i++ ){
    for ( int j = 0; j < bottomBlobs.size(); j++ ){
      player_blobfinder_blob bottom = bottomBlobs[j];
      player_blobfinder_blob top = topBlobs[i]; 
      
      if ( ITL_DEBUG ) {
	cout << "top "; 
	printBlobInfo(top); 
      
	cout << "bottom "; 
	printBlobInfo(bottom);
      }
	  
      // process blob: add the marker only if there isn't a significant gap between them 
	double topL = static_cast<double>(top.bottom - top.top); 
	double topW = static_cast<double>(top.right - top.left); 
	double bottomL = static_cast<double>(bottom.bottom - bottom.top); 
	double bottomW = static_cast<double>(bottom.right - bottom.left); 
      double eps = 0.5;
      if (blobOnTopOf(top, bottom) && (bottom.top - top.bottom < ( topL + bottomL )/2 )) {
	// process blob: if you see a 2 color marker at an extreme angle the above condition will fail 
	// to recognize a missing blob in between 2 others. check if the height/width ratio is close to 1:1
	// This bit does more harm than good at the moment, in case you are wondering why it is commented out.
	//if( (( topL / topW ) > 1 - eps && ( topL / topW ) < 1 + eps ) &&
	//    (( bottomL / bottomW ) > 1 - eps && ( bottomL / bottomW ) < 1 + eps )) {
	  if ( ITL_DEBUG ) 
	    cout << "top blob is on top of the bottom blob. Marker found! " << endl;
	  
	  Observation observation = Observation(id, 
						map,
						this->getAngle((bottom.right + bottom.left + top.right + top.left) / 4), 
						observationVariance);
	  
	  ob.push_back(observation);
	  
	  topBlobs.erase(topBlobs.begin()+i, topBlobs.begin()+i+1);
	  i--;
	  bottomBlobs.erase(bottomBlobs.begin()+j, bottomBlobs.begin()+j+1);
	  j--;
	  if ( topBlobs.size()==0 || bottomBlobs.size()==0 ){
	    if ( ITL_DEBUG )
	      cout << "one of the blobs is empty. leaving function" << endl;
	    return ob;
	  }
	  //}
	  //else 
	  //cout << "looking at the blobs from an extreme angle. discarding observation" << endl; 
      }
      else 
	if (ITL_DEBUG)
	cout << "DISCARDING BLOB INFO: there is a significant gap between these blobs." << endl; 
    }
  }
  return ob;
}

vector<Observation> InterfaceToLocalization::findEnteranceMarkersFromBlobs(vector<player_blobfinder_blob>& blobs,
									   string id) {


  if ( ITL_DEBUG )
    cout << "checking single " << id << " markers" << endl;
  vector<Observation> ob;
  
  if ( ITL_DEBUG ) 
    cout << id << " blobs size: " << blobs.size() << endl;
  for(int i = 0; i < blobs.size(); i++ ){
    player_blobfinder_blob blob = blobs[i];
    if ( ITL_DEBUG ) 
      printBlobInfo(blob);
    
    // process blobs: check the length/width ratio. should be close to 2:1. just to be sure 1.5:1
    double d = static_cast<double>( blob.bottom - blob.top ) / ( blob.right - blob.left ); 
    if (ITL_DEBUG)
      cout << "h/w ratio: " << d << endl;
    if ( d > 1.75 ) {           
      Observation observation = Observation(id, 
					    map,
					    this->getAngle((blob.right + blob.left) / 2), 
					    observationVariance);
      
      ob.push_back(observation);
      
      blobs.erase(blobs.begin()+i, blobs.begin()+i+1);
      i--;
      if ( blobs.size() == 0 ) {
	if ( ITL_DEBUG )  
	  cout << "oops now our blob is empty. leaving function" << endl;
	return ob;
	
      }
    }
    else 
      if (ITL_DEBUG)
	cout << "discarding observation h/w ratio doesn't match" << endl; 
  }
  return ob;
}

vector<Observation> InterfaceToLocalization::findGreenBlobs(vector<player_blobfinder_blob>& blobs,
									   string id) {


  if ( ITL_DEBUG )
    cout << "checking single " << id << " markers" << endl;
  vector<Observation> ob;
  
  if ( ITL_DEBUG ) 
    cout << id << " blobs size: " << blobs.size() << endl;
  for(int i = 0; i < blobs.size(); i++ ){
    player_blobfinder_blob blob = blobs[i];
    if ( ITL_DEBUG ) 
      printBlobInfo(blob);
    
    if ( blob.area > 30 ) {           
      Observation observation = Observation(id, 
					    map,
					    this->getAngle((blob.right + blob.left) / 2), 
					    observationVariance);
      
      ob.push_back(observation);
      
      blobs.erase(blobs.begin()+i, blobs.begin()+i+1);
      i--;
      if ( blobs.size() == 0 ) {
	if ( ITL_DEBUG )  
	  cout << "oops now our blob is empty. leaving function" << endl;
	return ob;
	
      }
    }
    else 
      if (ITL_DEBUG)
	cout << "discarding observation area is not big enough" << endl; 
  }
  return ob;
}

bool InterfaceToLocalization::blobOnTopOf(player_blobfinder_blob top,
					  player_blobfinder_blob bottom) {
  return (top.left < bottom.right && top.right > bottom.left && top.bottom
	  < bottom.top + 3 && top.bottom + 50 > bottom.top);
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
    //cout << "************************* Observation Summary *************************** " << endl; 
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
      cout << "\t\t\t" << roomMarkers.size() << " room markers found: " ; 
      for ( vector<Observation>::iterator i = roomMarkers.begin(); i != roomMarkers.end(); i++ )
	cout << i->getMarkerId() << " " ; 
      cout << endl;
    }
    
    if ( cornerMarkers.size() != 0 ) {
      cout << "\t\t\t" << cornerMarkers.size() << " corner markers found: " ; 
      for ( vector<Observation>::iterator i = cornerMarkers.begin(); i != cornerMarkers.end(); i++ )
	cout << i->getMarkerId() << " " ; 
      cout << endl;
    }
    
    if ( enteranceMarkers.size() != 0 ) {
      cout << "\t\t\t" << enteranceMarkers.size() << " enterance markers found: " ; 
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
