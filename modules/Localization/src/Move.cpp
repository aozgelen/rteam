/*
 * Move.cpp
 *
 *  Created on: Dec 27, 2008
 *      Author: richardmarcley
 */

#include "Move.h"
#include "Utils.h"

Move::Move(double x, double y, double theta){
	this->x = x;
	this->y = y;
	this->theta = Utils::normalizeAngle(theta);
}



