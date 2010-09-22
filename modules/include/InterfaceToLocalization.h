/*
 * Robot.h
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

using namespace PlayerCc;

class InterfaceToLocalization {
public:
  InterfaceToLocalization(Map * map, int fieldOfVision, PlayerClient * robot);

  void update();
  void move(Position relativePosition);
  Position getPosition() {
    robotMutex.lock();
    Position pos = mc->getPosition();
    robotMutex.unlock();
    return pos;
  }

  double getConfidence() {
    robotMutex.lock();
    double confidence = mc->getConfidence();
    robotMutex.unlock();
    return confidence;
  }
  
  MonteCarlo * getMonteCarlo() {
    return mc;
  }
  
  vector<Observation> getObservations() {
    robotMutex.lock();
    vector<Observation> observations = obs;
    robotMutex.unlock();
    
    return observations;
  }

  void setObservationVariance(double observationVariance) {
    this->observationVariance = observationVariance;
  }

  bool isMoving();
  
  void printBlobInfo(player_blobfinder_blob);

protected:
  MonteCarlo * mc;
  PlayerClient * robot;
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
  
  double radiansToDegrees(double rad);
  double getAngle(double x);

  void readData();
  void updateObservations();
  Move getLastMove();

  int getBlobColor(player_blobfinder_blob blob);

  bool positionEqual(Position p1, Position p2);

  bool blobOnTopOf(player_blobfinder_blob top, player_blobfinder_blob bottom);

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
