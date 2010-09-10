/*
 * Utils.h
 *
 *  Created on: Dec 23, 2008
 *      Author: richardmarcley
 */

#ifndef UTILS_H_
#define UTILS_H_

#include <math.h>
#include <string>
#include <stdlib.h>

using namespace std;

#define PI acos((double)-1)
#define _E 2.7182

class Utils {
public:
	//transforms any radian value angle to (-PI, PI] angle.
	static double normalizeAngle(double alpha) {
		int quotent = (int) (alpha / (2 * PI));
		double remainder = alpha - quotent * 2 * PI;

		if (remainder <= -1 * PI)
			remainder += 2 * PI;
		if (remainder > PI)
			remainder -= 2 * PI;
		return remainder;
	}

	static double gaussian(double x, double mean, double variance) {
		double coef = 1 / sqrt(2 * PI * variance);
		double exponent = -1 * (x - mean) * (x - mean) / (2 * variance);

		double retVal = coef * pow(M_E, exponent);
		return retVal;
	}

	static double applyRandomness(double x, double maxRandom) {
		return (x + maxRandom * getRandom(-1, 1));
	}

	//transforms an angle from radians to degrees
	static double toDegrees(double radians) {
		return 180 * radians / PI;
	}

	//transforms an angle from radians to degrees
	static double toRadians(double degrees) {
		return PI * degrees / 180;
	}

	static void initRandom() {
		srand(time(NULL));
	}

	static double getRandom(double lowerBound, double upperBound) {
		double intervalSize = upperBound - lowerBound;
		return (rand() * intervalSize / RAND_MAX + lowerBound);
	}

	static double getRandom() {
		return getRandom(0, 1);
	}

	static string getTime() {
		time_t timer = time(NULL);
		char * date = asctime(localtime(&timer));
		string date_s = string(date);
		return date_s;
	}
};

#endif /* UTILS_H_ */
