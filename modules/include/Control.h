/*
 * BehaviorThread.h
 *
 *  Created on: Jun 25, 2010
 *      Author: robotics
 */

#ifndef BEHAVIORTHREAD_H_
#define BEHAVIORTHREAD_H_

#include "InterfaceToLocalization.h"

class Control {
public:
	Control(InterfaceToLocalization * robot);

	void operator()();
private:
	InterfaceToLocalization * robot;
};

#endif /* BEHAVIORTHREAD_H_ */
