/*
 * InterfaceToLocalization.cpp
 *
 * Author: George Rabanca
 *
 */

#include "InterfaceToLocalization.h"

#define DEBUG (false)
#define COLOR_PINK 0
#define COLOR_YELLOW 1
#define COLOR_BLUE 2
#define COLOR_GREEN 3
#define COLOR_ORANGE 4

InterfaceToLocalization::InterfaceToLocalization(Map * map, 
						 int fieldOfVision,
						 PlayerClient * robot) {

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
  
  this->robot = robot;
  bfp = new BlobfinderProxy(robot, 0);
  p2d = new Position2dProxy(robot, 0);
  
  destination = Position(0, 0, 0);

  cumulativeMove = Position(0, 0, 0);
  robotMutex.unlock();
}


/* Updates the observations and the mc filter if the robot is on the move or 
   changed it's position since last update 
*/
void InterfaceToLocalization::update() {

  robotMutex.lock();
  robot->Read();
  robotMutex.unlock();

  //	if (!p2d->IsFresh() && !bfp->IsFresh())
  //		return;

  if (!isMoving()){
    //cout << "robot is not moving. updating observations" << endl;
    updateObservations();
  }

  Move lastMove = getLastMove();
  if (obs.size() > 0 || lastMove.getX() + lastMove.getTheta() != 0) {
    //cout << "updating filter" << endl;
    mc->updateFilter(lastMove, obs);
    
    if (lastMove.getX() != 0 || lastMove.getTheta() != 0) {
      printf("Updated filter with move: %f, %f, %f\n", lastMove.getX(), lastMove.getY(), lastMove.getTheta());
    }
  }
}

void InterfaceToLocalization::move(Position relativePosition) {
  robotMutex.lock();
  destination = Position(relativePosition.getX() / 100.0,
			 relativePosition.getY() / 100, 
			 relativePosition.getTheta());

  p2d->SetOdometry(0, 0, 0);
  p2d->GoTo(destination.getX(), destination.getY(), destination.getTheta());
  
  //printf("destination: %f, %f, %f\n", destination.getX(), destination.getY(), destination.getTheta());

  cumulativeMove = Position(0, 0, 0);
  robotMutex.unlock();
}

bool InterfaceToLocalization::isMoving() {
  robotMutex.lock();
  bool isMoving = !(destination == Position(0, 0, 0));
  robotMutex.unlock();
  
  return isMoving;
}

Move InterfaceToLocalization::getLastMove() {
  Move lastMove;
  
  robotMutex.lock();

  if (destination == Position(0, 0, 0))
    lastMove = Move(0, 0, 0);
  else {
    if (p2d->IsFresh()) {
      
      p2d->NotFresh();

      double x = p2d->GetXPos() - cumulativeMove.getX();
      double y = p2d->GetYPos() - cumulativeMove.getY();
      double theta = p2d->GetYaw() - cumulativeMove.getTheta();
      cumulativeMove.moveRelative(Move(x, y, theta));
    }

    lastMove = Move(destination.getX() * 100, destination.getY() * 100, destination.getTheta());

    //printf("reached destination\n");
    destination = Position(0, 0, 0);
    cumulativeMove = Position(0, 0, 0);
    
    p2d->SetOdometry(0, 0, 0);
  }

  robotMutex.unlock();

  return lastMove;
}

void InterfaceToLocalization::updateObservations() {
  robotMutex.lock();
  
  obs.clear();

  if ( DEBUG )
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
  
  if ( DEBUG ){
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

  if ( DEBUG ) 
    cout << "size of obs: " << obs.size() << endl;

  // corridor markers
  vector<Observation> pinkOverOrangeOverYellow = findRoomMarkersFromBlobs(pinkBlobs, orangeBlobs, yellowBlobs, "p/o/y");
  vector<Observation> yellowOverPinkOverOrange = findRoomMarkersFromBlobs(yellowBlobs, pinkBlobs, orangeBlobs, "y/p/o");
  vector<Observation> pinkOverYellowOverOrange = findRoomMarkersFromBlobs(pinkBlobs, yellowBlobs, orangeBlobs, "p/y/o");
  vector<Observation> yellowOverOrangeOverPink = findRoomMarkersFromBlobs(yellowBlobs, orangeBlobs, pinkBlobs, "y/o/p");

  if ( DEBUG ){
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

  if ( DEBUG )
    cout << "size of obs: " << obs.size() << endl;

  // corner markers
  vector<Observation> yellowOverBlue = findCornerMarkersFromBlobs(yellowBlobs, blueBlobs, "y/b");
  vector<Observation> blueOverYellow = findCornerMarkersFromBlobs(blueBlobs, yellowBlobs, "b/y");
  vector<Observation> pinkOverYellow = findCornerMarkersFromBlobs(pinkBlobs, yellowBlobs, "p/y");
  vector<Observation> yellowOverPink = findCornerMarkersFromBlobs(yellowBlobs, pinkBlobs, "y/p");

  if ( DEBUG ) {
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

  if ( DEBUG ) 
    cout << "size of obs: " << obs.size() << endl;

  // enterance markers
  vector<Observation> blue = findEnteranceMarkersFromBlobs(blueBlobs, "b");
  vector<Observation> orange = findEnteranceMarkersFromBlobs(orangeBlobs, "o");

  if ( DEBUG ) {
    cout << "checked enterance markers" << endl; 
    cout << "b:" << blue.size() << ", o:" << orange.size() << endl;
  }

  if ( !blue.empty() ) 
    obs.insert(obs.begin(), blue.begin(), blue.end());  
  if ( !orange.empty() ) 
    obs.insert(obs.begin(), orange.begin(), orange.end());

  if ( DEBUG ){
    cout << "size of obs: " << obs.size() << endl;

    for ( int i = 0; i < obs.size(); i++ ) {
      cout << "obs " << i << " is " << obs[i].getMarkerId() << endl;
    }
  }

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

  if ( DEBUG )
    cout << "checking 3 color markers :" << id << endl;
  
  vector<Observation> ob;
  
  for( int i = 0; i < topBlobs.size(); i++ ) {
    for ( int k = 0; k < middleBlobs.size(); k++ ) {
      for ( int j = 0; j < bottomBlobs.size(); j++ ) {
	
	player_blobfinder_blob bottom = bottomBlobs[j];
	player_blobfinder_blob middle = middleBlobs[k];
	player_blobfinder_blob top = topBlobs[i]; 
	
	if ( DEBUG ) {
	  cout << "top "; 
	  printBlobInfo(top); 
	  cout << "middle "; 
	  printBlobInfo(middle); 
	  cout << "bottom "; 
	  printBlobInfo(bottom);
	} 
	
	if (blobOnTopOf(top, middle) && blobOnTopOf(middle,bottom)) {
	  if ( DEBUG ) 
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
	    if ( DEBUG )
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

  if ( DEBUG )
    cout << "checking 2 color markers" << endl;
  vector<Observation> ob;
 
  for( int i = 0; i < topBlobs.size(); i++ ){
    for ( int j = 0; j < bottomBlobs.size(); j++ ){
      player_blobfinder_blob bottom = bottomBlobs[j];
      player_blobfinder_blob top = topBlobs[i]; 
      
      if ( DEBUG ) {
	cout << "top "; 
	printBlobInfo(top); 
      
	cout << "bottom "; 
	printBlobInfo(bottom);
      }
	  
      if (blobOnTopOf(top, bottom)) {
	if ( DEBUG ) 
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
	  if ( DEBUG )
	    cout << "one of the blobs is empty. leaving function" << endl;
	  return ob;
	}
      }
    }
  }
  return ob;
}

vector<Observation> InterfaceToLocalization::findEnteranceMarkersFromBlobs(vector<player_blobfinder_blob>& blobs,
									   string id) {


  if ( DEBUG )
    cout << "checking single " << id << " markers" << endl;
  vector<Observation> ob;
  
  if ( DEBUG ) 
    cout << id << " blobs size: " << blobs.size() << endl;
  for(int i = 0; i < blobs.size(); i++ ){
    player_blobfinder_blob blob = blobs[i];
    if ( DEBUG ) 
      printBlobInfo(blob);
    
    //if () {           // check the length/width ratio. should be close to 2:1
    Observation observation = Observation(id, 
					  map,
					  this->getAngle((blob.right + blob.left) / 2), 
					  observationVariance);
    
    ob.push_back(observation);
    
    blobs.erase(blobs.begin()+i, blobs.begin()+i+1);
    i--;
    if ( blobs.size() == 0 ) {
      if ( DEBUG )  
	cout << "oops now our blob is empty. leaving function" << endl;
      return ob;
      
    }
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
