#include <gtest/gtest.h>
#include <fstream>
#include "../src/MonteCarlo.h"
#include "../src/MonteCarloDebugger.h"


namespace MonteCarloDebuggerUnitTest {

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

TEST(MonteCarloDebuggerUnitTest, DisplaysMonteCarlo)
{
	Map * map = createMap();
	MonteCarlo * mc = new MonteCarlo(map);
	MonteCarloDebugger * debugger = new MonteCarloDebugger();
	mc->setDebugger(debugger);

	vector<Observation> obs;
	obs.push_back(Observation("marker id", map, .5));
	mc->updateFilter(Move(1, 0.1, 0.01), obs);
	mc->getPosition();
//	debugger->display();

	delete(mc);
}
}
