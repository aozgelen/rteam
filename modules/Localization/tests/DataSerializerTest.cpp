#include <gtest/gtest.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include "../src/DataSerializer.h"

void Assert_Approx(double d1, double d2, double eps) {
	double delta = fabs(d1 - d2);
	ASSERT_LE(delta, eps);
}

TEST(DataSerializerUnitTest, ReadAndWriteAnInteger)
{
	stringstream stream(stringstream::in | stringstream::out);
	int test;
	DataSerializer::writeInteger(stream, 10);
	DataSerializer::readInteger(stream, test);
	ASSERT_EQ(10, test);
}

TEST(DataSerializerUnitTest, ReadAndWriteALargeInteger)
{
	stringstream stream(stringstream::in | stringstream::out);
	int test;
	DataSerializer::writeInteger(stream, 130);
	DataSerializer::readInteger(stream, test);
	ASSERT_EQ(130, test);
}

TEST(DataSerializerUnitTest, ReadAndWriteADouble)
{
	stringstream stream(stringstream::in | stringstream::out);
	double test;
	DataSerializer::writeDouble(stream, 10.3);
	DataSerializer::readDouble(stream, test);
	ASSERT_EQ(10.3, test);
}

TEST(DataSerializerUnitTest, ReadAndWriteAString)
{
	stringstream stream(stringstream::in | stringstream::out);
	string test;
	DataSerializer::writeString(stream, "string");
	DataSerializer::readString(stream, test);
	ASSERT_EQ("string", test);
}

TEST(DataSerializerUnitTest, ReadAndWriteAMarker)
{
	stringstream stream(stringstream::in | stringstream::out);
	MapMarker *test;
	DataSerializer::writeMarker(stream, new MapMarker("id", 10.1, 1000.1));
	DataSerializer::readMarker(stream, test);
	ASSERT_EQ(10.1, test->getX());
	ASSERT_EQ(1000.1, test->getY());
	ASSERT_EQ("id", test->getId());
}

TEST(DataSerializerUnitTest, ReadAndWriteAMap)
{
	stringstream stream(stringstream::in | stringstream::out);
	Map * test = new Map(100, 200);
	Map * copy;
	test->addMarker(MapMarker("1", 10, 11));
	test->addMarker(MapMarker("2", 20, 21));
	DataSerializer::writeMap(stream, test);
	DataSerializer::readMap(stream, copy);
	ASSERT_EQ(test->getHeight(), copy->getHeight());
	ASSERT_EQ(test->getLength(), copy->getLength());
	ASSERT_EQ(test->getMarkers().size(), copy->getMarkers().size());
	ASSERT_EQ(test->getMarker(0).getId(), copy->getMarker(0).getId());
	ASSERT_EQ(test->getMarker(1).getId(), copy->getMarker(1).getId());
}

TEST(DataSerializerUnitTest, ReadAndWriteAMove)
{
	stringstream stream(stringstream::in | stringstream::out);
	Move test;
	DataSerializer::writeMove(stream, Move(10.1, 20.2, 1.15));
	DataSerializer::readMove(stream, test);
	ASSERT_EQ(10.1, test.getX());
	ASSERT_EQ(20.2, test.getY());
	ASSERT_EQ(1.15, test.getTheta());
}

TEST(DataSerializerUnitTest, ReadAndWriteAnObservation)
{
	stringstream stream(stringstream::in | stringstream::out);
	Observation * test;
	string markerId = "id";
	DataSerializer::writeObservation(stream, new Observation(markerId, NULL,
			5.45));
	DataSerializer::readObservation(stream, test, NULL);
	ASSERT_EQ(5.45, test->getBearing());
	ASSERT_EQ(markerId, test->getMarkerId());
}

TEST(DataSerializerUnitTest, ReadAndWriteAnUpdate)
{
	stringstream stream(stringstream::in | stringstream::out);

	vector<Observation> obs;
	for (int i = 0; i < 10; i++) {
		string markerId = "id";
		markerId.append(i, 't');
		obs.push_back(Observation(markerId, NULL, i * .1));
	}

	Move move(.1, .01, .001);
	DataSerializer::writeUpdate(stream, move, obs);

	Move moveCopy;
	vector<Observation> obsCopy;
	DataSerializer::readUpdate(stream, moveCopy, obsCopy);

	Assert_Approx(move.getX(), moveCopy.getX(), 0.001);
	Assert_Approx(move.getY(), moveCopy.getY(), 0.001);
	Assert_Approx(move.getTheta(), moveCopy.getTheta(), 0.001);

	ASSERT_EQ(obs.size(), obsCopy.size());
	for (int i=0; i< 10; i++) {
		Observation oCopy = obsCopy[i];
		Observation o = obs[i];
		ASSERT_EQ(o.getMarkerId(), oCopy.getMarkerId());
		Assert_Approx(o.getBearing(), oCopy.getBearing(), .001);
	}
}

TEST(DataSerializerIntegrationTest, ReadDataFromRobot)
{
	ifstream file("DATA");
	Map * map;
	DataSerializer::readMap(file, map);

	ASSERT_EQ(850, map->getLength());
	ASSERT_EQ(1230, map->getHeight());
	ASSERT_EQ(5, map->getMarkers().size());

	for (int i=0; i<map->getMarkers().size(); i++) {
		MapMarker marker = map->getMarkers()[i];
		printf("%f, %f\n", marker.getX(), marker.getY());
	}

	while (!(file.peek() == EOF)) {
		Move move;
		vector<Observation> obs;
		DataSerializer::readUpdate(file, move, obs);

		printf("******************\n");
		printf("Move: %f, %f, %f\n", move.getX(), move.getY(), move.getTheta());
		printf("Observations: %d\n", obs.size());

		for (int i=0; i<obs.size(); i++) {
			printf("Obs %d: (%s at %f)\n", i, obs[i].getMarkerId().c_str(), obs[i].getBearing());
		}
	}
}

