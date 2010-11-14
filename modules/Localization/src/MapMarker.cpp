/*
 * MapMarker.cpp
 *
 *  Created on: Jan 14, 2009
 *      Author: Geotty
 */
#include "MapMarker.h"
#include <iostream>
using namespace std; 

double MapMarker::getBearing(Position position) const{
	double alpha;
	alpha = atan2(y - position.getY(), x - position.getX());
	alpha -= position.getTheta();
	alpha = Utils::normalizeAngle(alpha);
	return alpha;
}

