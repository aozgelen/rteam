/*
 * MonteCarlo.h
 *
 *  This is the main class for the MonteCarlo localization.
 *  It should be initialized with a map containing markers and their positions.
 *  Then should be continuously updated with the robot moves and marker observations.
 *
 *  The getPosition() and getConfidence() methods are giving the approximate position
 *  of the robot.
 */

#ifndef MONTECARLO_H_
#define MONTECARLO_H_

#include "vector"
#include "Utils.h"
#include "Map.h"
#include "Particle.h"
#include "Move.h"
#include "MonteCarloDebugger.h"

using namespace std;

#define PARTICLE_NUM 1000

class MonteCarlo {
public:
	MonteCarlo(Map * map);

	void updateFilter(Move delta, vector<Observation>& obs);

	double getConfidence() const { return confidence; }
	vector<Particle> getParticles() { return particles; }
	void setDebugger(MonteCarloDebugger * debugger) { this->debugger = debugger; }
	MonteCarloDebugger* getDebugger() { return debugger; }
	Position getPosition();
	void setRandomCoefficients(double thetaConfident, double xyConfident, double theta, double xy);

private:
	vector<Particle> particles;
	Map *map;
	int particlesToReseed;
	double confidence;
	Position position;
	MonteCarloDebugger * debugger;

	double DELTA_RANDOM_THETA_CONFIDENT;
	double DELTA_RANDOM_XY_CONFIDENT;
	double DELTA_RANDOM_THETA;
	double DELTA_RANDOM_XY;

	double TRACKING_RANDOM_X;
	double TRACKING_RANDOM_Y;
	double TRACKING_RANDOM_THETA;


	void updateParticleProbabilities(const vector<Observation>& obs);
	void applyMoveToParticles(Move delta);
	void resample();
	Particle seedParticle();

	double induceRandomness(double x, double percentRandom, double maxRandom, double probability);
	bool isInsideMap(Particle particle);
	vector<double> normalizeParticleProbabilities();
	double addParticleProbabilities();
	vector<double> divideProbabilitiesBy(double probabilitySum);

	void testAllParticlesInsideMap(char * message);

	void debug(vector<Observation>& obs);

	friend class MonteCarloTest;
};

#endif /* MONTECARLO_H_ */
