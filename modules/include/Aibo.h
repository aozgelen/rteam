/*
 * Surveyor.h
 *
 *  Created on: Jul 1, 2010
 *      Author: robotics
 */

#ifndef AIBO_H
#define AIBO_H_

#include "InterfaceToLocalization.h"

class Aibo: public InterfaceToLocalization {
public:
	Aibo(Map * map, PlayerClient * robot) : InterfaceToLocalization(map, 59, robot){
		setObservationVariance(.2);

		printf("Set motor enable\n");
		p2d->SetMotorEnable(true);
	}
};

#endif /* AIBO_H_ */
