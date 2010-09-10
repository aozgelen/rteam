/*
 * MonteCarloDebugger.h
 *
 *  Created on: Apr 15, 2010
 *      Author: appleapple
 */

#ifndef MONTECARLODEBUGGER_H_
#define MONTECARLODEBUGGER_H_
#include "Position.h"
#include "Particle.h"
#include <vector>

using namespace std;

class MonteCarloDebugger {
public:
	MonteCarloDebugger();
	void setConfidence(double confidence) {
		this->confidence = confidence;
	}

	double getConfidence() {
		return confidence;
	}

	void setParticles(vector<Particle> & particles) {
		this->particles = particles;
	}

	void setPosition(Position position){
		this->position = position;
	}

	Position getPosition() {
		return position;
	}

	void setObservations(vector<Observation>& obs) {
		this->obs = obs;
	}

	vector<Observation> getObservations() {
		return obs;
	}

	void display();

public:
	double confidence;
	Position position;
	vector<Particle> particles;
	vector<Observation> obs;
};

#endif /* MONTECARLODEBUGGER_H_ */
