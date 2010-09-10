/*
 * Map.h
 *
 *  Created on: Dec 21, 2008
 *      Author: richardmarcley
 */

#ifndef MAP_H_
#define MAP_H_

#include <vector>
#include <math.h>
#include <stdio.h>
#include <string>
#include "MapMarker.h"
#include "MapWall.h"
#include "Position.h"
using namespace std;

class Map {
public:
  Map();
  Map(int length, int height);
  
  void addMarker(MapMarker marker);
  
  vector<MapMarker> getMarkerById(string id);
  
  MapMarker getMarker(int index);
  
  vector<MapMarker> getMarkers() {
    return markers;
  }

  /* added for walls */
  void addWall(MapWall wall); 
  
  vector<MapWall> getWallById(string id);

  MapWall getWall(int index); 

  vector<MapWall> getWalls() {
    return walls;
  }
  /* add for walls ends */ 
  
  int getLength() {
    return length;
  }
  
  int getHeight() {
    return height;
  }
  
  int length;
  int height;
  
protected:
  vector<MapMarker> markers;
  vector<MapWall> walls;   //added by tuna 
};

#endif /* MAP_H_ */
