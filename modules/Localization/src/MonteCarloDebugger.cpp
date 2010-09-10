/*
 * MonteCarloDebugger.cpp
 *
 *  Created on: Apr 15, 2010
 *      Author: appleapple
 */

#include "MonteCarloDebugger.h"


MonteCarloDebugger::MonteCarloDebugger() {
	// TODO Auto-generated constructor stub

}

void MonteCarloDebugger::display() {
	printf("\n\n*****************\n");
	printf("Position: (%f, %f, %f), Confidence: %f\n", position.getX(), position.getY(), position.getTheta(), confidence);
	printf("There are %d particles\n", particles.size());

	for (int i=0; i<particles.size(); i++) {
		Position p = particles[i].getPosition();
		printf("%f, %f\n", p.getX(), p.getY());
	}
}
