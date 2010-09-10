/*
 * MapUnitTest.cpp
 *
 *  Created on: Jan 19, 2010
 *      Author: appleapple
 */

#include <gtest/gtest.h>
#include "../src/Map.h"
#include "../src/MapMarker.h"

TEST(MapUnitTest, AddAndRetrieveMarkersFromAMap) {
	Map map(100, 200);
	string id("test");

	MapMarker marker1(id, 15, 25);
	MapMarker marker2("different id", 15, 25);
	MapMarker marker3(id, 47, 98);
	map.addMarker(marker1);
	map.addMarker(marker2);
	map.addMarker(marker3);

	vector<MapMarker> markers = map.getMarkerById(id);
	ASSERT_EQ(2, markers.size());
	ASSERT_EQ(id, markers[0].getId());
}
