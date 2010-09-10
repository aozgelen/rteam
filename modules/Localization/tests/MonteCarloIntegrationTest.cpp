#include <gtest/gtest.h>
#include <fstream>
#include "../src/Map.h"
#include "../src/MonteCarlo.h"
#include <Bitmap.h>

//Replace these by TEST to reinstate the tests
//TEST(MonteCarloIntegrationTest, LocalizesAfterManyObservations)
void LocalizesAfterManyObservations()
{
	//create a map
	Map map(5000, 2000);
	vector<MapMarker> markers;

	markers.push_back(MapMarker("id", 100, 100));
	markers.push_back(MapMarker("id", 2000, 100));
	markers.push_back(MapMarker("id", 4900, 100));
	markers.push_back(MapMarker("id", 100, 1900));
	markers.push_back(MapMarker("id", 2000, 1900));
	markers.push_back(MapMarker("id", 4900, 1900));

	for (int i = 0; i < markers.size(); i++) {
		map.addMarker(markers[i]);
	}

	//create a MonteCarlo with that map
	MonteCarlo mc(&map);

	//choose a position
	Position position(100, 1700, 0);

	//make a bitmap to visually show the position and estimate
	Bitmap bmp(550, 250);

	Utils::initRandom();
	for (int i = 0; i < 1000; i++) {
		vector<Observation> obs;
		position.setX(position.getX() + Utils::getRandom(3, 7));

		//simulate input from vision
		for (int j = 0; j < markers.size(); j++) {

			//see just the markers that are in the front of the agent.
			double bearing = markers[j].getBearing(position);
			if (bearing < PI / 2 && bearing > -1 * PI / 2 && Utils::getRandom()
					< .4) {
				bearing = bearing + Utils::getRandom(-.20, .20);
				obs.push_back(Observation(string("id"), &map, bearing));
			}
		}

		mc.updateFilter(Move(5, 0, 0), obs);
		Position estimate = mc.getPosition();

		if (mc.getConfidence() > .1) {
			bmp.setPixel(estimate.getX() / 10, estimate.getY() / 10, RGB(200,
					200, 200));
		}
	}

	//write final position and estimate position to the bitmap
	Position estimate = mc.getPosition();
	bmp.rectangle(estimate.getX() / 10 - 2, estimate.getY() / 10 - 2,
			estimate.getX() / 10 + 2, estimate.getY() / 10 + 2, RGB(255, 0, 0));
	bmp.rectangle(position.getX() / 10 - 2, position.getY() / 10 - 2,
			position.getX() / 10 + 2, position.getY() / 10 + 2, RGB(0, 255, 0));

	ofstream out("localization.bmp");
	bmp.write(out);
	out.close();
}

void updateWithObservations(double angle) {

}



//TEST(MonteCarloIntegrationTest, DataFromAibo)
void DataFromAibo()
{
	Map * map = new Map(850, 1230);
	map->addMarker(MapMarker("purple marker", 0, 0));
	map->addMarker(MapMarker("purple marker", 850, 0));
	map->addMarker(MapMarker("purple marker", 0, 1230));
	map->addMarker(MapMarker("purple marker", 850, 1230));
	map->addMarker(MapMarker("purple marker", 850, 500));

	Utils::initRandom();
	MonteCarlo * mc = new MonteCarlo(map);
	Position position = mc->getPosition();
	double confidence;
	vector<Observation> obs;
	obs.push_back(Observation("purple marker", map, Utils::toRadians(5.109811)));
	mc->updateFilter(Move(0, 0, 0), obs);
	position = mc->getPosition();
	confidence = mc->getConfidence();

	obs.clear();
	obs.push_back(Observation("purple marker", map, Utils::toRadians(6.499817)));
	mc->updateFilter(Move(0, 0, 0), obs);
	position = mc->getPosition();
	confidence = mc->getConfidence();

	obs.clear();
	obs.push_back(Observation("purple marker", map, Utils::toRadians(66.727634)));
	mc->updateFilter(Move(0, 0, 0), obs);
	position = mc->getPosition();
	confidence = mc->getConfidence();

	printf("Confidence: %f\n", confidence);
	printf("Position: %f, %f, %f\n", position.getX(), position.getY(), position.getTheta());
}

//TEST(MonteCarloIntegrationTest, MakeBitmap)
//{
//	Bitmap bmp(500, 1000);
//	ofstream out("localizationaaa.bmp");
//	bmp.write(out);
//	out.close();
//}
