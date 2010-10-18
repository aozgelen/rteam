/*
 * MonteCarlo.cpp
 *
 *  Created on: Dec 21, 2008
 *      Author: richardmarcley
 */
#include <stdlib.h>
#include <algorithm>
#include <iostream>
#include "MonteCarlo.h"
#include "MCLPositionEstimator.h"
#include "Utils.h"

using namespace std;

const float STARTING_PARTICLE_PROBABILITY = .2;

MonteCarlo::MonteCarlo(Map * map) {
	int i;
	this->map = map;
	for (i = 0; i < PARTICLE_NUM; i++) {
		particles.push_back(seedParticle());
	}

	particlesToReseed = 10;
	DELTA_RANDOM_THETA_CONFIDENT = .1;
	DELTA_RANDOM_XY_CONFIDENT = 10;
	DELTA_RANDOM_THETA = M_PI / 16;
	DELTA_RANDOM_XY = 20;

	TRACKING_RANDOM_X = .1;
	TRACKING_RANDOM_Y = 0;
	TRACKING_RANDOM_THETA = .2;

	debugger = NULL;
}

void MonteCarlo::setRandomCoefficients(double thetaConfident,
		double xyConfident, double theta, double xy) {

	DELTA_RANDOM_THETA_CONFIDENT = thetaConfident;
	DELTA_RANDOM_XY_CONFIDENT = xyConfident;
	DELTA_RANDOM_THETA = theta;
	DELTA_RANDOM_XY = xy;
}

void MonteCarlo::debug(vector<Observation>& obs) {
	if (debugger != NULL) {
		debugger->setPosition(getPosition());
		debugger->setConfidence(confidence);
		debugger->setParticles(particles);
		debugger->setObservations(obs);
	}
}

Position MonteCarlo::getPosition() {
	MCLPositionEstimator estimator = MCLPositionEstimator(map->length, map->height);
	position = estimator.getPosition(particles, confidence);
	return position;
}

//debug method for a bug where the position is outside the map or the theta is outside of
// (-PI PI] interval
void MonteCarlo::testAllParticlesInsideMap(char * message) {
	int i;
	for (i = 0; i < particles.size(); i++) {
		//if particle is outside of the field reseed it;
		Position pos = particles[i].getPosition();
		if (!isInsideMap(particles[i]) || pos.getTheta() > PI || pos.getTheta()
				<= -PI) {
			std::cout<< "Particle outside bounds: " << pos.getX() << "/"
					<< pos.getY() << "/" << pos.getTheta() << " " << message
					<< std::endl;

			particles[i] = seedParticle();
		}
	}
}

void MonteCarlo::updateFilter(Move delta, vector<Observation>& obs) {
	applyMoveToParticles(delta);
	updateParticleProbabilities(obs);
	resample();

	//TODO: do not do it twice
	//we are doing it in resample but before introducing new particles.
	normalizeParticleProbabilities();

	debug(obs);
}

//this method updates particle positions according to the given move and some induced randomness.
//particles that have a bad probability move more randomly than particles with high probability.
//particles that move outside of the map are reseeded.
void MonteCarlo::applyMoveToParticles(Move delta) {
	unsigned int i;
	double deltaXY;
	double deltaTheta;

	if (confidence > .3) {
		deltaXY = DELTA_RANDOM_XY_CONFIDENT;
		deltaTheta = DELTA_RANDOM_THETA_CONFIDENT;
	} else {
		deltaXY = DELTA_RANDOM_XY;
		deltaTheta = DELTA_RANDOM_THETA;
	}

	for (i = 0; i < particles.size(); i++) {
		double probability = particles[i].probability;
		double x = induceRandomness(delta.getX(), TRACKING_RANDOM_X, deltaXY, probability);
		double y = induceRandomness(delta.getY(), TRACKING_RANDOM_Y, deltaXY, probability);
		double theta = induceRandomness(delta.getTheta(), TRACKING_RANDOM_THETA, deltaTheta,
				probability);

		particles[i].updatePosition(Move(x, y, theta));

		//if particle is outside of the field reseed it;
		if (!isInsideMap(particles[i])) {
			particles[i] = seedParticle();
		}
	}
}

//this method updates particles probabilities according to the current observations;
void MonteCarlo::updateParticleProbabilities(const vector<Observation>& obs) {
	//TODO make sure it is implemented ok...  test it !
	for (unsigned int i = 0; i < particles.size(); i++) {
		particles[i].updateProbability(obs);
	}
}

//this method updates the set of particles;  it duplicates successful particles and gets rid of
//particles that have bad probability;
//the expected number of a particle with probability p is TotalParticleNum * p / sum(all prob);
void MonteCarlo::resample() {
	sort(particles.begin(), particles.end(), &Particle::isMoreProbable);
	vector<double> normalizedP = normalizeParticleProbabilities();

	int particleNum = particles.size();
	int i = 0;
	vector<Particle> resampled;

	//we are trying a different approach to reseeding...
	while (particles[i].probability > .1 && resampled.size() < PARTICLE_NUM
			- particlesToReseed) {
		int copies = (int) max(normalizedP[i] * particles.size(), 1.0);
		resampled.insert(resampled.end(), copies, particles[i]);
		particleNum -= copies;
		i++;
	}

	//reseed about 'particlesToReseed' particles every iteration
	//TODO make this reseeding smarter: account for landmarks and history of landmarks
	while (particleNum > 0) {
		resampled.push_back(seedParticle());
		particleNum--;
	}

	particles = resampled;
}

//TODO particles have now normalizedProbabilities so we should use that
//field instead of returning a different vector.
vector<double> MonteCarlo::normalizeParticleProbabilities() {
	double probabilitySum = addParticleProbabilities();
	return divideProbabilitiesBy(probabilitySum);
}

double MonteCarlo::addParticleProbabilities() {
	double probabilitySum = 0;
	for (unsigned int i = 0; i < particles.size(); i++) {
		probabilitySum += particles[i].probability;
	}
	return probabilitySum;
}

vector<double> MonteCarlo::divideProbabilitiesBy(double probabilitySum) {
	vector<double> normalizedProbabilities;
	for (unsigned int i = 0; i < particles.size(); i++) {
		double normalized = particles[i].probability / probabilitySum;
		particles[i].normalizedProbability = normalized;
		normalizedProbabilities.push_back(normalized);
	}
	return normalizedProbabilities;
}

double MonteCarlo::induceRandomness(double x, double percentRandom, double maxRandom,
		double probability) {
	return (x + x * percentRandom * Utils::getRandom(-1, 1) + maxRandom * Utils::getRandom(-1, 1) * (1 - probability));
}

Particle MonteCarlo::seedParticle() {
	Particle newParticle = Particle();

	double x = Utils::getRandom(0, map->getLength());
	double y = Utils::getRandom(0, map->getHeight());
	double theta = Utils::getRandom(-PI, PI);

	newParticle.setPosition(Position(x, y, theta));
	newParticle.probability = STARTING_PARTICLE_PROBABILITY;
	newParticle.normalizedProbability = 0;

	return newParticle;
}

bool MonteCarlo::isInsideMap(Particle particle) {
	double x = particle.getPosition().getX();
	double y = particle.getPosition().getY();
	if (x <= 0 || y <= 0 || x >= map->getLength() || y >= map->getHeight()) {
		return false;
	}
	return true;
}
