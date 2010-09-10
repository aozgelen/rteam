/*
 * FakeMonteCarlo.cpp
 *
 *  Created on: Apr 7, 2010
 *      Author: appleapple
 */

#include "FakeMonteCarlo.h"
#include "DataSerializer.h"

FakeMonteCarlo::FakeMonteCarlo(Map * map, string fileName) {
	this->map = map;
	this->fileName = "/MS/OPEN-R/MW/DATA/P/data";
	ofstream outputFile(this->fileName.c_str(), ios::out | ios::trunc
			| ios::binary);
	DataSerializer::writeMap(outputFile, map);
	outputFile.close();
}

void FakeMonteCarlo::updateFilter(Move delta, const vector<Observation>& obs) {
	ofstream outputFile(fileName.c_str(), ios::out | ios::binary | ios::app);

//	string s("new update");
//	DataSerializer::writeString(outputFile, s);

	DataSerializer::writeUpdate(outputFile, delta, obs);
	outputFile.close();
}
