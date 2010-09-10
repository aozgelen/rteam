/*
 * Map.cpp
 *
 *  Created on: Dec 21, 2008
 *      Author: richardmarcley
 *    Modified: added walls 
 */

#include "Map.h"
#include "Utils.h"

#include <iostream>
using namespace std;

Map::Map(){}

Map::Map(int length, int height) {
  this->length = length;
  this->height = height;
}

void Map::addMarker(MapMarker marker)
{
  markers.push_back(marker);
}

vector<MapMarker> Map::getMarkerById(string id)
{
  vector<MapMarker> foundMarkers;

  for (int i =0; i< markers.size(); i++) {
    if (markers[i].getId() == id) {
      foundMarkers.push_back(markers[i]);
    }
  }

  return foundMarkers;
}

MapMarker Map::getMarker(int index) {
  return markers[index];
}

void Map::addWall(MapWall wall){
  walls.push_back(wall); 
}

vector<MapWall> Map::getWallById(string id){
  vector<MapWall> foundWalls;

  for (int i =0; i< walls.size(); i++) {
    if (walls[i].getId() == id) {
      foundWalls.push_back(walls[i]);
    }
  }

  return foundWalls;
}

MapWall Map::getWall(int index){
  return walls[index];
}


