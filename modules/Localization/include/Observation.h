/*
 * Observation.h
 *
 *  Created on: Dec 21, 2008
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
	Observation(string markerId, Map* map, double bearing, double variance) :
		bearing(0) {

		this->map = map;
		this->markerId = markerId;
		this->bearing = bearing;
		this->variance = variance;
	}

	double calculateLikelihoodForPosition(Position) const;

	double getBearing() const {
		return bearing;
	}

	string getMarkerId() const{
		return markerId;
	}

	void setMap(Map * map) {
		this->map = map;
	}

private:
	
	double calculateLikelihoodForMarkerAndPosition(MapMarker marker, Position position) const;

	bool isWallBlocking(MapMarker marker, Position position) const; 
	bool get_line_intersection(double,double,double,double,double,double,double,double,double*,double*) const;

	Map * map;
	string markerId;
	double bearing;
	double variance;
};

#endif /* OBSERVATION_H_ */
