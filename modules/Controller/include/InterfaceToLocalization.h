/*
 * InterfaceToLocalization.h
 *
 */

#ifndef INTERFACE_TO_LOCALIZATION_H_
#define INTERFACE_TO_LOCALIZATION_H_

#include "boost/thread/mutex.hpp"
#include "libplayerc++/playerc++.h"
#include <unistd.h>
#include "Observation.h"
#include "Move.h"
#include "Position.h"
#include "Map.h"
#include "MonteCarlo.h"
#include "blobfinder_blob.h"

using namespace PlayerCc;

class InterfaceToLocalization {
public:
  //InterfaceToLocalization(Map * map, int fieldOfVision, PlayerClient * robot);
  InterfaceToLocalization(Map * map, int fieldOfVision);

  void update();
  void move(Position relativePosition);
  Position getPosition();

  double getConfidence() {
    robotMutex.lock();
    double confidence = mc->getConfidence();
    robotMutex.unlock();
    return confidence;
  }
  
  MonteCarlo * getMonteCarlo() {
    return mc;
  }

  void setMCDebugger(){
    debugger = new MonteCarloDebugger(); 
    mc->setDebugger(debugger); 
  }

  MonteCarloDebugger* getMCDebugger(){ return debugger; }

  vector<Observation> getObservations() {
    robotMutex.lock();
    vector<Observation> observations = obs;
    robotMutex.unlock();
    
    return observations;
  }

  void setObservationVariance(double observationVariance) {
    this->observationVariance = observationVariance;
  }

  void setSpeed(double, double, double);
  void moveToMapPosition(int, int);

  bool isDestinationReached() { return destinationReached; }
  bool isDestinationSet();
  bool isFound() { return foundItem; }
  void resetDestinationInfo(); 

  void setBlobFinderProxy(PlayerClient*);
  void setPosition2dProxy(PlayerClient*);

  void printBlobColor(player_blobfinder_blob);
  void printBlobs(vector<player_blobfinder_blob>& ); 
  void printBlobInfo(player_blobfinder_blob);

protected:
  MonteCarlo * mc;
  MonteCarloDebugger * debugger; 
  CameraProxy * cp;
  BlobfinderProxy * bfp;
  Position2dProxy * p2d;
  
  boost::mutex robotMutex;

  Position startPos; 
  Position previousMove; 
  bool destinationReached; 
  Position destination;
  Position cumulativeMove;

  Map * map;
  int fov;						//the field of vision in degrees
  vector<Observation> obs;
  double observationVariance;
  
  bool foundItem ;

  bool positionEqual(Position p1, Position p2);

  double radiansToDegrees(double rad);
  double getAngle(double x);

  void readData();
  void updateObservations();
  Move getLastMove();
  
  Position convertToRobotCoordinates(Position mapPos); 
  Position convertToMapCoordinates(Position robotPos); 
  double calcHeadingToDestination(double x, double y);

  int getBlobColor(player_blobfinder_blob blob);

  bool blobOnTopOf(player_blobfinder_blob top, player_blobfinder_blob bottom);

  void displayObservationSummary();
  void joinBlobs(vector<player_blobfinder_blob>&);
  bool isOverlapping( player_blobfinder_blob, player_blobfinder_blob );

  vector<Observation> findRoomMarkersFromBlobs( vector<player_blobfinder_blob>& topBlobs, 
						vector<player_blobfinder_blob>& middleBlobs, 
						vector<player_blobfinder_blob>& bottomBlobs, 
						string id);

  vector<Observation> findCornerMarkersFromBlobs( vector<player_blobfinder_blob>& topBlobs, 
						  vector<player_blobfinder_blob>& bottomBlobs, 
						  string id);

  vector<Observation> findEnteranceMarkersFromBlobs( vector<player_blobfinder_blob>& blobs, 
						     string id);

  vector<Observation> findGreenBlobs( vector<player_blobfinder_blob>& blobs, string id);
};

#endif /* INTERFACE_TO_LOCALIZATION_H_ */
