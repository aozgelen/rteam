/*
 * Position.cpp
 *
 *  Created on: Dec 26, 2008
 *      Author: richardmarcley
 */

#include "Position.h"
#include "Utils.h"
#include <stdio.h>

Position::Position(double x, double y, double theta) {
	this->x = x;
	this->y = y;
	this->theta = Utils::normalizeAngle(theta);
}

double Position::getDistance(Position other) {
	double dx = x - other.getX();
	double dy = y - other.getY();
	double distance = sqrt(dx * dx + dy * dy);
	return distance;
}

double Position::getX() const {
	return x;
}

void Position::setX(double x) {
	this->x = x;
}

double Position::getY() const {
	return y;
}

void Position::setY(double y) {
	this->y = y;
}

double Position::getTheta() const {
	return theta;
}

void Position::setTheta(double theta) {
	this->theta = Utils::normalizeAngle(theta);
}

void Position::add(Position absolutePos) {
	y += absolutePos.getY();
	x += absolutePos.getX();
	setTheta(theta + absolutePos.getTheta());
}

void Position::sub(Position absolutePos) {
	y -= absolutePos.getY();
	x -= absolutePos.getX();
	setTheta(theta - absolutePos.getTheta());
}

void Position::moveAbsolute(Move move) {
	y += move.getY();
	x += move.getX();
	setTheta(theta + move.getTheta());
}

void Position::moveRelative(Move move) {  
  y += move.getX() * sin(theta) + move.getY() * cos(theta);
  x += move.getX() * cos(theta) - move.getY() * sin(theta);
  setTheta(theta + move.getTheta());
}

void Position::rotate(double radians) {
	setTheta(theta + radians);
}

void Position::walk(double distance) {
	x += distance * cos(theta);
	y += distance * sin(theta);
}

string* Position::toString() {
	char retVal[100];
	sprintf(retVal, "x=%f, y=%f, th=%f(%f deg)", x, y, theta, theta * 180 / PI);

	string* str = new string();
	str->assign(retVal);
	return str;
}

bool Position::operator==(Position p){
	return (x == p.getX() && y == p.getY() && theta == p.getTheta());
}
