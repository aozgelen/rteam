/*
 * MCLPositionEstimator.cpp
 *
 *  Created on: Jan 21, 2009
 *      Author: Geotty
 */

#include "MCLPositionEstimator.h"

MCLPositionEstimator::MCLPositionEstimator(const int length, const int height) {
	cubeX = length / partitionNum;
	cubeY = height / partitionNum;
	cubeT = 360 / partitionNum;
}

Position MCLPositionEstimator::averagePositions(vector<Particle> particles) {
	Position averagePos = Position();

	if (particles.size() == 0)
		return averagePos;

	int particleNum = particles.size();
	double avX = 0, avY = 0, avTheta = 0, sinTheta = 0, cosTheta = 0;

	for (int i = 0; i < particleNum; ++i) {
		avX += particles[i].getPosition().getX();
		avY += particles[i].getPosition().getY();
		sinTheta += sin(particles[i].getPosition().getTheta());
		cosTheta += cos(particles[i].getPosition().getTheta());
	}

	avX = avX / particleNum;
	avY = avY / particleNum;

	sinTheta = sinTheta / particleNum;
	cosTheta = cosTheta / particleNum;
	avTheta = atan2(sinTheta, cosTheta);

	return Position(avX, avY, avTheta);
}

//this implementation of getPosition is dividing the space into cubes of equal size
//then finds the 2x2x2 supercube with most particles, and then averages the position
//of those particles.
Position MCLPositionEstimator::getPosition(vector<Particle> particles, double& confidence) {
  int cubes[partitionNum][partitionNum][partitionNum] = { };

  //count the number of particles in each cube
  for (unsigned int i = 0; i < particles.size(); i++) {
    Position pos = particles[i].getPosition();
    
    int cubeIndexX = (int) (pos.getX() / cubeX);
    int cubeIndexY = (int) (pos.getY() / cubeY);

    double theta = pos.getTheta() >= 0 ? pos.getTheta() : (2 * PI + pos.getTheta());
    int degrees = (int) Utils::toDegrees(theta);
    int cubeIndexT = (int) (degrees / cubeT);

    if (cubeIndexT < 0 || cubeIndexX < 0 || cubeIndexY < 0 || cubeIndexT
	>= partitionNum || cubeIndexX >= partitionNum || cubeIndexY
	>= partitionNum) {
      //do we still get here? let's return position 0, 0, 0 if any of the particles are
      //outside the field
      confidence = 0;
      // this below continue was return Position( pos.getX(), pos.getY(), pos.getTheta() ). so if there 
      // was a particle which was out of bounds then the whole sampling was wasted, hence the whacky 
      // position estimate. 
      continue; 
    } else
      cubes[cubeIndexX][cubeIndexY][cubeIndexT]++;
  }
  
  //find the max supercube
  int maxX = 0, maxY = 0, maxT = 0, maxParticles = 0;
  for (int x = 0; x < partitionNum - 1; ++x) {
    for (int y = 0; y < partitionNum - 1; ++y) {
      for (int t = 0; t < partitionNum - 1; ++t) {
	int particlesInSupercube = cubes[x][y][t];
	particlesInSupercube += cubes[x][y][t + 1];
	particlesInSupercube += cubes[x][y + 1][t];
	particlesInSupercube += cubes[x][y + 1][t + 1];
	particlesInSupercube += cubes[x + 1][y][t];
	particlesInSupercube += cubes[x + 1][y][t + 1];
	particlesInSupercube += cubes[x + 1][y + 1][t];
	particlesInSupercube += cubes[x + 1][y + 1][t + 1];

	if (particlesInSupercube > maxParticles) {
	  maxX = x;
	  maxY = y;
	  maxT = t;
	  maxParticles = particlesInSupercube;
	}
      }
    }
  }

  //find the particles that belong to the max supercube just found
  vector<Particle> particlesToAverage;
  confidence = 0;
  for (unsigned int i = 0; i < particles.size(); i++) {
    Position pos = particles[i].getPosition();
    
    int cubeIndexX = (int) (pos.getX() / cubeX);
    if ((cubeIndexX != maxX) && (cubeIndexX != maxX + 1))
      continue;
    
    int cubeIndexY = (int) (pos.getY() / cubeY);
    if ((cubeIndexY != maxY) && (cubeIndexY != maxY + 1))
      continue;
    
    double theta = pos.getTheta() >= 0 ? pos.getTheta() : (2 * PI + pos.getTheta());
    int degrees = (int) Utils::toDegrees(theta);
    int cubeIndexT = (int) (degrees / cubeT);
    if ((cubeIndexT != maxT) && (cubeIndexT != maxT + 1))
      continue;
    
    particlesToAverage.push_back(particles[i]);
    //		confidence += particles[i].normalizedProbability;
    confidence += particles[i].probability / particles.size();
  }
  
  //	printf("There are %d particles in the supercube.",
  //			particlesToAverage.size());
  
  Position averagePosition = averagePositions(particlesToAverage);
  return averagePosition;
}
