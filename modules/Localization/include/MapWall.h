/*
 * MapWall.h
 *
 *  Created on: Jul 28 2010
 *      Author: tuna
 */

#ifndef MAP_WALL_H_
#define MAP_WALL_H_

#include "Utils.h"
#include "Position.h"
#include <vector>

class MapWall {
public:
  MapWall() {}
  
  MapWall(string i, double fx, double fy, double sx, double sy);

  string getId() const { return id; }

  double getX0() const { return x0; }
  double getX1() const { return x1; }
  double getY0() const { return y0; }
  double getY1() const { return y1; }
  
  void setX0(double x) { x0 = x; }
  void setY0(double y) { y0 = y; }
  void setX1(double x) { x1 = x; }
  void setY1(double y) { y1 = y; }
  
private:
  double x0, y0, x1, y1;
  string id;
};

#endif /* WALL_H_ */
