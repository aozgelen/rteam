/*
 * Observation.cpp
 *
 *  Created on: Jan 13, 2009
 *      Author: Geotty
 */

#include <math.h>
#include "Utils.h"
#include "Observation.h"

#include <iostream>

/* Calculates and returns the probability of seeing this observation from a given position.
 * If the markers in the observation is not unique, then it repeats this process for each 
 * marker and assigns the probability of the most likely one. 
 */ 
double Observation::calculateLikelihoodForPosition(Position position) const {
	vector<MapMarker> markers = map->getMarkerById(markerId);

	double probability = 0;
	for (int i = 0; i < markers.size(); i++) {
		double likelihood = calculateLikelihoodForMarkerAndPosition(markers[i],
				position);

		if (likelihood > probability)
			probability = likelihood;
	}

	return probability;
}

/* Given a marker and a position, this function checks the difference between the angles where the marker is observed
 * and where it should be observed. In addition a euclidian distance is calculated. If there is not a wall between the
 * position and marker and the distance is not too close, then the expectation of observing this marker from this position
 * is returned.
 */
double Observation::calculateLikelihoodForMarkerAndPosition(MapMarker marker, Position position) const {

	double expectedBearing = marker.getBearing(position);
	double deltaAngle = fabs((double) (bearing - expectedBearing));

	double distanceToMarker = position.getDistance(Position(marker.getX(),
			marker.getY(), 0));

	if (distanceToMarker < 5) return 0;

	/* check if there are any walls in the line of sight */
	if (isWallBlocking(marker, position)) return 0;

	double variance = 100 / distanceToMarker * Utils::toRadians(this->variance);
	
	double expectation = Utils::gaussian(deltaAngle, 0, variance)
			/ Utils::gaussian(0, 0, variance);

	return expectation;
}

bool Observation::isWallBlocking(MapMarker marker, Position position) const{
  double mx = marker.getX(); 
  double my = marker.getY(); 
  double px = position.getX();
  double py = position.getY(); 
  double ix, iy; 

  vector<MapWall> walls = map->getWalls();
  vector<MapWall>::iterator iter; 
  for( iter = walls.begin(); iter !=walls.end(); iter++ ){
    if ( get_line_intersection(iter->getX0(),iter->getY0(),iter->getX1(),iter->getY1(),mx,my,px,py,&ix,&iy) ){
      return true;
    }
  }
  return false;
}

// Returns 1 if the lines intersect, otherwise 0. In addition, if the lines 
// intersect the intersection point may be stored in the double i_x and i_y.
bool Observation::get_line_intersection(double p0_x, double p0_y, double p1_x, double p1_y, 
			   double p2_x, double p2_y, double p3_x, double p3_y, double *i_x, double *i_y) const
{
    double s1_x, s1_y, s2_x, s2_y;
    s1_x = p1_x - p0_x;     s1_y = p1_y - p0_y;
    s2_x = p3_x - p2_x;     s2_y = p3_y - p2_y;

    double s, t;
    s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
    t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);

    if (s >= 0 && s <= 1 && t >= 0 && t <= 1)
    {
        // Collision detected
        if (i_x != NULL)
            *i_x = p0_x + (t * s1_x);
        if (i_y != NULL)
            *i_y = p0_y + (t * s1_y);
	    
        return true;
    }

    return false; // No collision
}
