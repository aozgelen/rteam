/*
 * DataSerializer.h
 *
 *  Created on: Apr 8, 2010
 *      Author: appleapple
 */

#ifndef DATASERIALIZER_H_
#define DATASERIALIZER_H_

#include <iostream>
#include "MapMarker.h"
#include "Observation.h"
using namespace std;

struct McUpdate{
	Move move;
	vector<Observation> obs;
};

class DataSerializer {
public:
	static void writeInteger(ostream& stream, int i);
	static void readInteger(istream& stream, int &i);
	static void writeDouble(ostream& stream, double d);
	static void readDouble(istream& stream, double &d);
	static void writeString(ostream& stream, string s);
	static void readString(istream& stream, string &s);
	static void writeMarker(ostream& stream, MapMarker * marker);
	static void readMarker(istream& stream, MapMarker * &marker);

	static void writeMap(ostream& stream, Map * map);
	static void readMap(istream& stream, Map * &map);

	static void writeObservation(ostream& stream, Observation *observation);
	static void readObservation(istream& stream, Observation * &observation,
			Map * map);

	static void writeMove(ostream& stream, Move move);
	static void readMove(istream& stream, Move &move);

	static void writeUpdate(ostream& stream, Move move, vector<Observation> obs);
	static void readUpdate(istream& stream, Move &move, vector<Observation> &obs);

};

#endif /* DATASERIALIZER_H_ */
