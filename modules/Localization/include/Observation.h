/*
 * Observation.h
 *
 *      Author: richardmarcley
 */

#ifndef OBSERVATION_H_
#define OBSERVATION_H_

#include "MapMarker.h"
#include "MapWall.h"
#include "Position.h"
#include "Map.h"

class Observation {
public:
  /*<<<<<<< HEAD
 Observation(string markerId, Map* map, double bearing, double variance, double value=1) :
  bearing(0) {
  =======*/
  /*Observation(string markerId, Map* map, double bearing, double variance) :
  bearing(0), bearingLeft(InvalidBearing), bearingRight(InvalidBearing) {
    
>>>>>>> master
    this->map = map;
    this->markerId = markerId;
    this->bearing = bearing;
    this->variance = variance;
<<<<<<< HEAD
    this->value = value;
=======
  }
  */
  
 Observation(string markerId, Map* map, double variance, double bearingLeft = InvalidBearing, double bearingRight = InvalidBearing) :
  bearing(0) {
    // for DEBUG info TODO delete
    printInfo = false;
    this->map = map;
    this->markerId = markerId;
    //this->bearing = bearing;
    this->variance = variance;
    this->bearingLeft = bearingLeft; 
    this->bearingRight = bearingRight;
    calculateValue();
    //print();
  }

  // TODO : test this with the first version of blob processing where used blobs were erased on the fly and causing free() invalid pointer error.
  //~Observation(){}
  
  double calculateLikelihoodForPosition(Position) const;
  
  double getBearing() const { return bearing; }
  double getBearingLeft() const { return bearingLeft; } 
  double getBearingRight() const { return bearingRight; }
  
  string getMarkerId() const{
    return markerId;
  }
  
  void setMap(Map * map) {
    this->map = map;
  }


  double getValue() const { return value; }
  void setValue(double value) {
    this->value = value; 
  }

  void print() const {
    cout << "Observation " << markerId 
	 << " bearing for left: " << bearingLeft 
      //<< "\tcenter: " << bearing 
	 << "\tright: " << bearingRight 
	 << "\tvalue: " << value << endl;  
  } 
  
  const static double InvalidBearing = -M_PI * 3;

  // for printing DEBUG info for a single particle TODO delete
  mutable bool printInfo;
  
private:
  
  double calculateLikelihoodForMarkerAndPosition(MapMarker marker, Position position) const;
  void calculateValue();
  
  bool isWallBlocking(MapMarker marker, Position position) const; 
  bool get_line_intersection(double,double,double,double,double,double,double,double,double*,double*) const;
  
  Map * map;
  string markerId;
  double value;
  double bearing, bearingLeft, bearingRight;
  double variance;
};

#endif /* OBSERVATION_H_ */
