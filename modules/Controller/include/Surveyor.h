/*
 * Surveyor.h
 *
 *  Created on: Jul 1, 2010
 *      Author: robotics
 */

#ifndef SURVEYOR_H_
#define SURVEYOR_H_

#include "InterfaceToLocalization.h"

class Surveyor: public InterfaceToLocalization {
public:
  // InterfaceToLocalization( Map, FieldOfView, PlayerClient );
	Surveyor(Map * map, PlayerClient * robot) : InterfaceToLocalization(map, 41, robot){
		setObservationVariance(1);
	}
};

#endif /* SURVEYOR_H_ */
