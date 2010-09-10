/*
 * MonteCarloInterface.h
 *
 *  Created on: Apr 7, 2010
 *      Author: appleapple
 */

#ifndef MONTECARLOINTERFACE_H_
#define MONTECARLOINTERFACE_H_

#include "vector"
#include "Utils.h"
#include "Map.h"
#include "Particle.h"
#include "Move.h"

using namespace std;

class MonteCarloInterface {
public:
	MonteCarloInterface(){}
	MonteCarloInterface(Map*) {
	}
	virtual Position getPosition() =0;
	virtual void updateFilter(Move delta, const vector<Observation>& obs)=0;

	virtual double getConfidence() const =0;
	virtual vector<Particle> getParticles() =0;
};

#endif /* MONTECARLOINTERFACE_H_ */
