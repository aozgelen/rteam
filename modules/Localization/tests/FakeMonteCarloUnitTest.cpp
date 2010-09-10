/*
 * FakeMonteCarloUnitTest.cpp
 *
 *  Created on: Apr 7, 2010
 *      Author: appleapple
 */

#include <gtest/gtest.h>
#include <fstream>
#include "../src/FakeMonteCarlo.h"
#include "../src/DataSerializer.h"

string id1("id1");
string id2("id2");

Map * createMap() {
	Map * map = new Map(100, 200);

	MapMarker marker1(id1, 15, 25);
	MapMarker marker2(id2, 15, 25);
	MapMarker marker3(id1, 47, 98);
	map->addMarker(marker1);
	map->addMarker(marker2);
	map->addMarker(marker3);

	return map;
}

void readMonteCarloInput(string fileName, Map * &map, vector<McUpdate> &updates) {
	ifstream file;
	file.open(fileName.c_str(), fstream::in);

	DataSerializer::readMap(file, map);

	while (file.peek() != EOF) {
		Move move;
		vector<Observation> obs;
		McUpdate u;
		DataSerializer::readUpdate(file, u.move, u.obs);
		updates.push_back(u);
	}

	file.close();
}

TEST(FakeMonteCarloUnitTest, MapInfoWrittenToFile)
{
	Map * map = createMap();
	string fileName="MonteCarloInput";
	FakeMonteCarlo * mc = new FakeMonteCarlo(map, fileName);
	delete(mc);

	Map * newMap;
	vector<McUpdate> update;
	readMonteCarloInput(fileName, newMap, update);

	ASSERT_EQ(3, newMap->getMarkers().size());
}

TEST(FakeMonteCarloUnitTest, UpdateInfoWrittenToFile)
{
	Map * map = createMap();
	string fileName="MonteCarloInput";
	FakeMonteCarlo * mc = new FakeMonteCarlo(map, fileName);
	vector<Observation> obs;
	obs.push_back(Observation("marker id", map, .5));
	mc->updateFilter(Move(1, 0.1, 0.01), obs);
	delete(mc);

	Map * newMap;
	vector<McUpdate> updates;
	readMonteCarloInput(fileName, newMap, updates);

	ASSERT_EQ(1, updates.size());
	ASSERT_EQ(1, updates[0].move.getX());
	ASSERT_EQ(.1, updates[0].move.getY());
	ASSERT_EQ(.01, updates[0].move.getTheta());

	ASSERT_EQ("marker id", updates[0].obs[0].getMarkerId());
	ASSERT_EQ(.5, updates[0].obs[0].getBearing());
}
