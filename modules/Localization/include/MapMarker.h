/*
 * MapMarker.h
 *
 *  Created on: Dec 21, 2008
 *      Author: richardmarcley
 */

#ifndef MAP_MARKER_H_
#define MAP_MARKER_H_

#include "Utils.h"
#include "Position.h"

class MapMarker {
public:
	MapMarker() {}

	MapMarker(string id, double x, double y) {
		this->id = id;
		this->x = x;
		this->y = y;
	}

	double getBearing(Position position) const;

	string getId() const {
		return id;
	}

	double getX() const {
		return x;
	}

	void setX(double x) {
		this->x = x;
	}

	double getY() const {
		return y;
	}

	void setY(double y) {
		this->y = y;
	}

private:
	double x, y;
	string id;
};
#endif /* MARKER_H_ */
