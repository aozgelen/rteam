/*
 * RobocupMap.cpp
 *
 *  Created on: Dec 26, 2008
 *      Author: richardmarcley
 */

#include "RobocupMap.h"

RobocupMap::RobocupMap() {
	markers.push_back(MapMarker("blue goal", 0, 1900));
	markers.push_back(MapMarker("yellow goal", 5000, 1900));
	markers.push_back(MapMarker("blue/yellow", 2500, 3800));
	markers.push_back(MapMarker("yellow/blue", 2500, 0));

	length = 5000;
	height = 3800;
}
