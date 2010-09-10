/*
 * FakeMonteCarlo.h
 *
 *  Created on: Apr 7, 2010
 *      Author: appleapple
 */

#ifndef FAKEMONTECARLO_H_
#define FAKEMONTECARLO_H_

#include <string>
#include <fstream>
#include "MonteCarloInterface.h"

using namespace std;

class FakeMonteCarlo : public MonteCarloInterface{
public:
	FakeMonteCarlo();
	FakeMonteCarlo(Map * map, string fileName);
	~FakeMonteCarlo() {
		outputFile.close();
	};

	Position getPosition(){
		return Position(0,0,0);
	}

	double getConfidence() const {
		return 0;
	}

	void updateFilter(Move delta, const vector<Observation>& obs);

	vector<Particle> getParticles() {
		vector<Particle> retVal;
		return retVal;
	}

private:
	Map * map;
	string fileName;
	ofstream outputFile;
};

#endif /* FAKEMONTECARLO_H_ */
