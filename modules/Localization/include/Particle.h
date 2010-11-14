/*
 * Particle.h
 *
 *  Created on: Dec 21, 2008
 *      Author: richardmarcley
 */

#ifndef PARTICLE_H_
#define PARTICLE_H_
#include <vector>
#include "Map.h"
#include "Observation.h"

using namespace std;

#define STARTING_PROBABILITY 3.0/10

class Particle {
public:
  //Particle(); 
  //Particle(double);
  void updatePosition(Move delta);
  void updatePostion(double rotation, double distance);
  void updateProbability(const vector<Observation> &obs);
  Position getPosition() const
  {
    return position;
  }
  
  void setPosition(Position position) {
    this->position = position;
  }
  
  void reset(vector<Observation*> obs);
  string *toString()
  {
    char prob_str[100];
    sprintf(prob_str, "%s probability: %f", position.toString()->c_str(), probability);
    string *retVal = new string(prob_str);
    return retVal;
  }
  
  static bool isMoreProbable(Particle p1, Particle p2);
  
  double probability;
  double normalizedProbability;
  const static double conservationRatio = .9;
private:
  Position position;
  //void changeProbability(double newProbability);
};

#endif /* PARTICLE_H_ */
