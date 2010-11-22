/*
 * MapMarker.cpp
 *
 *  Created on: Jan 14, 2009
 *      Author: Geotty
 */
#include "MapMarker.h"
#include <iostream>
using namespace std; 

/*MapMarker::MapMarker(string id, double x, double y){
  this->id = id;
  this->x = x;
  this->y = y;
  }*/

/*MapMarker::MapMarker(string id, double x, double y, double lx = -1, double ly = -1, double rx = -1, double ry = -1) {
  this->id = id;
  this->x = x;
  this->y = y;
  this->lx = lx; 
  this->ly = ly; 
  this->rx = rx; 
  this->ry = ry;
  this->print();
  }*/

double MapMarker::getBearing(Position position) const{
	double alpha;
	alpha = atan2(y - position.getY(), x - position.getX());
	alpha -= position.getTheta();
	alpha = Utils::normalizeAngle(alpha);
	return alpha;
}

double MapMarker::getLeftBearing(Position position) const{
	double alpha;
	alpha = atan2(ly - position.getY(), lx - position.getX());
	alpha -= position.getTheta();
	alpha = Utils::normalizeAngle(alpha);
	return alpha;
}

double MapMarker::getRightBearing(Position position) const{
	double alpha;
	alpha = atan2(ry - position.getY(), rx - position.getX());
	alpha -= position.getTheta();
	alpha = Utils::normalizeAngle(alpha);
	return alpha;
}
