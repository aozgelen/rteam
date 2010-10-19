/*
 * InterfaceToLocalization.h
 *
 *  Created on: Jun 22, 2010
 *      Author: robotics
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
  Position getPosition() {
    robotMutex.lock();
    Position pos = mc->getPosition();
    robotMutex.unlock();
    return pos;
  }
  /*Position getPosition(){
    return mc->getDebugger()->getPosition();
    }*/
  
  // remove this once the problem with observation update during motion is fixed
  /*void forceUpdateObservations() { 
    printf("forcing observation updates\n");
    updateObservations(); 
  }
  // desperate attempt to fix the bug explained before forceUpdateObservations
  Position getOdometry(){ 
    Move m = getLastMove();
    printf("last move in getOdometry(%f,%f,%f) \n", m.getX(), m.getY(), m.getTheta());
    return Position(m.getX()*100, m.getY()*100, m.getTheta());  
    }*/

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

  bool isMoving();

  void setBlobFinderProxy(PlayerClient*);
  void setPosition2dProxy(PlayerClient*);

  void printBlobColor(player_blobfinder_blob);
  void printBlobs(vector<player_blobfinder_blob>& ); 
  void printBlobInfo(player_blobfinder_blob);

protected:
  MonteCarlo * mc;
  //PlayerClient * robot;
  MonteCarloDebugger * debugger; 
  CameraProxy * cp;
  BlobfinderProxy * bfp;
  Position2dProxy * p2d;
  
  boost::mutex robotMutex;

  Position destination;
  Position cumulativeMove;
  Map * map;
  int fov;						//the field of vision in degrees
  vector<Observation> obs;
  double observationVariance;

  bool positionEqual(Position p1, Position p2, double xt, double yt, double tt);

  double radiansToDegrees(double rad);
  double getAngle(double x);

  void readData();
  void updateObservations();
  Move getLastMove();

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
};

#endif /* INTERFACE_TO_LOCALIZATION_H_ */
