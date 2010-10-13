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
	Aibo(Map * map) : InterfaceToLocalization(map, 59){
		setObservationVariance(.2);

		printf("Set motor enable\n");
		p2d->SetMotorEnable(true);
	}
};

#endif /* AIBO_H_ */
