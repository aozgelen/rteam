/*
 * DataSerializer.cpp
 *
 *  Created on: Apr 8, 2010
 *      Author: appleapple
 */

#include "DataSerializer.h"

void DataSerializer::writeInteger(ostream& stream, int i) {
	//	stream.write((char *) &i, sizeof(int));

	//write the integer as little endian of 4 bytes
	char bytes[4];
	for (int j = 0; j < 4; j++) {
		bytes[j] = i % 256;
		i = i / 256;
	}

	stream.write(bytes, 4);
}

void DataSerializer::readInteger(istream& stream, int& i) {
	//stream.read((char *) &i, sizeof(int));

	//read the integer as little endian of 4 bytes
	char bytes[4];
	stream.read(bytes, 4);
	i = 0;

	for (int j = 3; j >= 0; j--) {
		unsigned char c = bytes[j];
		i = i * 256 + c;
	}
}

void DataSerializer::writeDouble(ostream & stream, double d) {
	//TODO Test on aibo if the double is stored the same so we don't have
	//to convert to integers.

	stream.write((char *) &d, sizeof(double));

	//write the double as a 3 decimal int
//	d = d * 1000;
//	unsigned int i = d;
//	writeInteger(stream, i);
}

void DataSerializer::readDouble(istream & stream, double & d) {
	stream.read((char *) &d, sizeof(double));

	//the double is stored as an int / 1000
//	int i;
//	readInteger(stream, i);
//	d = i / 1000.0;
}

void DataSerializer::writeString(ostream & stream, string s) {
	int size = s.size();
	writeInteger(stream, size);
	stream.write(s.c_str(), size);
}

void DataSerializer::readString(istream & stream, string &s) {
	int size;
	readInteger(stream, size);
	char * c_str = new char[size + 1];
	stream.read((char *) c_str, size);
	c_str[size] = 0;
	s.assign(c_str);
}

void DataSerializer::writeMarker(ostream & stream, MapMarker * marker) {
	writeString(stream, marker->getId());
	writeDouble(stream, marker->getX());
	writeDouble(stream, marker->getY());
}

void DataSerializer::readMarker(istream & stream, MapMarker * &marker) {
	double x, y;
	string id;
	readString(stream, id);
	readDouble(stream, x);
	readDouble(stream, y);
	marker = new MapMarker(id, x, y);
}

void DataSerializer::writeMap(ostream & stream, Map * map) {
	int markersSize = map->getMarkers().size();

	writeInteger(stream, map->getHeight());
	writeInteger(stream, map->getLength());
	writeInteger(stream, markersSize);

	for (int i = 0; i < markersSize; i++) {
		writeMarker(stream, &map->getMarker(i));
	}
}

void DataSerializer::readMap(istream & stream, Map * &map) {
	int height, length;
	readInteger(stream, height);
	readInteger(stream, length);
	map = new Map(length, height);

	int markersSize;
	readInteger(stream, markersSize);

	for (int i = 0; i < markersSize; i++) {
		MapMarker * marker;
		readMarker(stream, marker);
		map->addMarker(*marker);
	}
}

void DataSerializer::writeObservation(ostream & stream,
		Observation * observation) {
	writeString(stream, observation->getMarkerId());
	writeDouble(stream, observation->getBearing());
}

void DataSerializer::readObservation(istream & stream,
		Observation * &observation, Map * map) {
	string id;
	double bearing;
	double variation = 0;
	readString(stream, id);
	readDouble(stream, bearing);

	observation = new Observation(id, map, bearing, variation);
}

void DataSerializer::writeMove(ostream & stream, Move move) {
	writeDouble(stream, move.getX());
	writeDouble(stream, move.getY());
	writeDouble(stream, move.getTheta());
}

void DataSerializer::readMove(istream & stream, Move & move) {
	double x, y, theta;
	readDouble(stream, x);
	readDouble(stream, y);
	readDouble(stream, theta);

	move = Move(x, y, theta);
}

void DataSerializer::writeUpdate(ostream & stream, Move move, vector<
		Observation> obs) {
	int size = obs.size();
	writeMove(stream, move);
	writeInteger(stream, size);

	for (int i = 0; i < size; i++) {
		writeObservation(stream, &obs[i]);
	}
}

void DataSerializer::readUpdate(istream & stream, Move & move, vector<
		Observation> &obs) {

	int size;
	readMove(stream, move);
	readInteger(stream, size);

	for (int i = 0; i < size; i++) {
		Observation *o;
		readObservation(stream, o, NULL);
		obs.push_back(*o);
	}
}
