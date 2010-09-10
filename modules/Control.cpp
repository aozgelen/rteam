/*
 * Control.cpp
 *
 *  Created on: Jun 25, 2010
 *      Author: robotics
 */

#include "Control.h"

Control::Control(InterfaceToLocalization* robot) :
	robot(NULL) {
	this->robot = robot;
}


/* 
   Bug: When controlling the surveyor, right turn commands ( r1, r2, r3, r4 ) 
        after some time it starts turning left. The radian conversion turns out
	+ instead of -. TEMP FIX: instead of turning -180 deg converted to radians
	it turns -179 deg
 */ 
void Control::operator()() {
	while (true) {
		if (!robot->isMoving()) {
			int dx, dt;
			printf("Enter move: ");

			char move[10];
			scanf("%s", move);

			if (!strcmp(move, "f1"))
				robot->move(Position(10, 0, 0));
			else if (!strcmp(move, "f2"))
				robot->move(Position(20, 0, 0));
			else if (!strcmp(move, "f3"))
				robot->move(Position(40, 0, 0));
			else if (!strcmp(move, "f4"))
				robot->move(Position(80, 0, 0));
			else if (!strcmp(move, "l1"))
			  robot->move(Position(0, 0, Utils::toRadians(22.5)));
			else if (!strcmp(move, "l2"))
			  robot->move(Position(0, 0, Utils::toRadians(45)));
			else if (!strcmp(move, "l3"))
			  robot->move(Position(0, 0, Utils::toRadians(90)));
			else if (!strcmp(move, "l4"))
			  robot->move(Position(0, 0, Utils::toRadians(180)));
			else if (!strcmp(move, "r1"))
			  robot->move(Position(0, 0, Utils::toRadians(-22.5)));
			else if (!strcmp(move, "r2"))
			  robot->move(Position(0, 0, Utils::toRadians(-45)));
			else if (!strcmp(move, "r3"))
			  robot->move(Position(0, 0, Utils::toRadians(-90)));
			else if (!strcmp(move, "r4"))
			  // toRadians(-180) wasn't giving the correct behavior
			  // : turning left instead of right. 
			  robot->move(Position(0, 0, Utils::toRadians(-179)));
			else {
				Position pos = robot->getPosition();
				printf("Location: %f, %f, Confidence: %f\n", pos.getX(),
						pos.getY(), robot->getConfidence());
			}
		}
	}
}
