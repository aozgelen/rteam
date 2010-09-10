/*
 * Particle.cpp
 *
 *  Created on: Dec 21, 2008
 *      Author: richardmarcley
 */
#include <math.h>
#include <time.h>		//for randomize seed
#include <iostream>

#include "Particle.h"
#include "Utils.h"

void Particle::updatePosition(Move move)
{
	position.moveRelative(move);
}


//each observation calculates the probability for the given position of the particle
//then the probability is not changed with more than MAX_PROBABILITY_INCREASE or MAX_PROBABILITY_DECREASE
void Particle::updateProbability(const vector<Observation> &obs)
{
	double newProbability;

	unsigned int i;
	for (i=0; i<obs.size(); i++) {
		newProbability = obs[i].calculateLikelihoodForPosition(position);
		changeProbability(newProbability);
	}
}

void Particle::updatePostion(double rotation, double distance)
{
	position.rotate(rotation);
	position.walk(distance);
}

bool Particle::isMoreProbable(Particle p1, Particle p2){
	if (p1.probability > p2.probability) return true;
	return false;
}

void Particle::changeProbability(double newProbability)
{
    if(probability - 0.05 > newProbability) {
        probability = probability - 0.05;
        return;
    }

	if(probability + 0.03 < newProbability){
		probability = probability + 0.03;
		return;
	}

	probability = newProbability;
}
