/*
 * This implementation of a map only adds the actual markers found on a RoboCup field.
 * As the vision module can recognize more markers, they can be added here to be considered by the
 * localization module.
 */

#ifndef ROBOCUPMAP_H_
#define ROBOCUPMAP_H_

#include "Map.h"

class RobocupMap : public Map{
public:
	RobocupMap();

//	static const int YB_MARKER = 1;
//	static const int BY_MARKER = 2;
//	static const int BG_LEFT = 3;
//	static const int BG_RIGHT = 4;
//	static const int YG_LEFT = 5;
//	static const int YG_RIGHT = 6;
};

#endif /* ROBOCUPMAP_H_ */
