
#include <gtest/gtest.h>
#include "../src/Map.h"
#include "../src/MapMarker.h"
#include "../src/Observation.h"

TEST(ObservationTest, LikelyhoodForMarkerSeenInPlaceIs1) {
	Map * map = new Map(100, 100);
	string id("test");

	MapMarker marker(id, 0, 0);
	map->addMarker(marker);

	Observation obs(id, map, 0);
	double likelyhood = obs.calculateLikelihoodForPosition(Position (-100, 0, 0));

	ASSERT_DOUBLE_EQ(1.0, likelyhood);
}


TEST(ObservationTest, LikelyhoodDoesNotDependOnDistanceToMarker) {
	Map * map = new Map(100, 100);
	string id("test");

	MapMarker marker(id, 0, 0);
	map->addMarker(marker);

	Observation obs(id, map, 0);
	double likelyhood1 = obs.calculateLikelihoodForPosition(Position (-10, -1, -PI/4));
	double likelyhood2 = obs.calculateLikelihoodForPosition(Position (-100, -1, -PI/4));

	printf("likelyhood1: %f\n", likelyhood1);
	printf("likelyhood2: %f\n", likelyhood2);

//	ASSERT_DOUBLE_EQ(1.0, likelyhood);
}
