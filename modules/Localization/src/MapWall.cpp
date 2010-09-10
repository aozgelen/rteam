/*
 * MapMarker.cpp
 *
 *  Created on: Jul 28, 2010
 *      Author: tuna     
 */
#include "MapWall.h"

MapWall::MapWall(string i, double fx, double fy, double sx, double sy) 
  : id(i), x0(fx), y0(fy), x1(sx), y1(sy) {}


/*vector<double> MapWall::getBearings(Position position) const{
  vector<double> bearings; 

  double alpha;
  alpha = atan2(y0 - position.getY(), x0 - position.getX());
  alpha -= position.getTheta();
  alpha = Utils::normalizeAngle(alpha);
  bearings.push_back(alpha);

  alpha = atan2(y1 - position.getY(), x1 - position.getX());
  alpha -= position.getTheta();
  alpha = Utils::normalizeAngle(alpha);
  bearings.push_back(alpha);

  return bearings;
}
*/
