/*
 * InterfaceToLocalization.cpp
 *
 * Author: George Rabanca
 *
 * Description
 *
 */

#include "InterfaceToLocalization.h"

#define COLOR_PINK 0
#define COLOR_YELLOW 1
#define COLOR_BLUE 2
#define COLOR_GREEN 3
#define COLOR_ORANGE 4

InterfaceToLocalization::InterfaceToLocalization(Map * map, int fieldOfVision,
		PlayerClient * robot) {

  // this way of locking and unlocking the mutex is error prone in the face of possible
  // exceptions thrown in between. instead use boost::mutex:scoped_lock lock(robotMutex) 
  // this doesn't require unlocking and releases lock upon object leaving scope. However,
  // it will only become a danger if there are multiple threads accessing these functions
  // for the same robot.
	robotMutex.lock();

	mc = new MonteCarlo(map);
	mc->setRandomCoefficients(Utils::toRadians(10), 10, Utils::toRadians(20),
			20);
	observationVariance = 0;

	this->map = map;
	this->fov = fieldOfVision;

	this->robot = robot;
	//	cp = new CameraProxy(robot, 0);
	bfp = new BlobfinderProxy(robot, 0);
	p2d = new Position2dProxy(robot, 0);

	destination = Position(0, 0, 0);

	cumulativeMove = Position(0, 0, 0);
	robotMutex.unlock();
}


/* Updates the observations and the mc filter if the robot is on the move or 
   changed it's position since last update 
*/
void InterfaceToLocalization::update() {

	robotMutex.lock();
	robot->Read();
	robotMutex.unlock();

	//	if (!p2d->IsFresh() && !bfp->IsFresh())
	//		return;

	if (!isMoving())
		updateObservations();

	Move lastMove = getLastMove();
	if (obs.size() > 0 || lastMove.getX() + lastMove.getTheta() != 0) {
		mc->updateFilter(lastMove, obs);

		if (lastMove.getX() != 0 || lastMove.getTheta() != 0) {
			printf("Updated filter with move: %f, %f, %f\n", lastMove.getX(),
					lastMove.getY(), lastMove.getTheta());
		}
	}
}

void InterfaceToLocalization::move(Position relativePosition) {
	robotMutex.lock();
	destination = Position(relativePosition.getX() / 100.0,
			relativePosition.getY() / 100, relativePosition.getTheta());

	p2d->SetOdometry(0, 0, 0);
	p2d->GoTo(destination.getX(), destination.getY(), destination.getTheta());

	printf("destination: %f, %f, %f\n", destination.getX(), destination.getY(),
			destination.getTheta());

	cumulativeMove = Position(0, 0, 0);
	robotMutex.unlock();
}

bool InterfaceToLocalization::isMoving() {
	robotMutex.lock();
	bool isMoving = !(destination == Position(0, 0, 0));
	robotMutex.unlock();

	return isMoving;
}

Move InterfaceToLocalization::getLastMove() {
	Move lastMove;

	robotMutex.lock();

	if (destination == Position(0, 0, 0))
		lastMove = Move(0, 0, 0);

	else {
		if (p2d->IsFresh()) {

			p2d->NotFresh();

			double x = p2d->GetXPos() - cumulativeMove.getX();
			double y = p2d->GetYPos() - cumulativeMove.getY();
			double theta = p2d->GetYaw() - cumulativeMove.getTheta();

//			printf("x: %f, y:%f, theta:%f\n", x, y, theta);
//			printf("move: x: %f, y:%f, theta:%f\n", cumulativeMove.getX(), cumulativeMove.getY(), cumulativeMove.getTheta());

			cumulativeMove.moveRelative(Move(x, y, theta));
		}

//		if (positionEqual(destination, cumulativeMove)) {
			lastMove = Move(destination.getX() * 100, destination.getY() * 100,
					destination.getTheta());

			printf("reached destination\n");
			destination = Position(0, 0, 0);
			cumulativeMove = Position(0, 0, 0);

			p2d->SetOdometry(0, 0, 0);
//		}
	}

	robotMutex.unlock();

	return lastMove;
}

void InterfaceToLocalization::updateObservations() {
	robotMutex.lock();

	obs.clear();

	if (!bfp->IsFresh()) {
		robotMutex.unlock();
		return;
	}

	vector<player_blobfinder_blob> pinkBlobs;
	vector<player_blobfinder_blob> yellowBlobs;
	vector<player_blobfinder_blob> blueBlobs;
	vector<player_blobfinder_blob> greenBlobs;
	vector<player_blobfinder_blob> orangeBlobs;

	for (int i = 0; i < bfp->GetCount(); i++) {

		double bearing = getAngle(
				(bfp->GetBlob(i).left + bfp->GetBlob(i).right) / 2);

		int color = getBlobColor(bfp->GetBlob(i));

		if (color == COLOR_PINK)
			pinkBlobs.push_back(bfp->GetBlob(i));
		else if (color == COLOR_YELLOW)
			yellowBlobs.push_back(bfp->GetBlob(i));
		else if (color == COLOR_GREEN)
			greenBlobs.push_back(bfp->GetBlob(i));
		else if (color == COLOR_ORANGE)
			orangeBlobs.push_back(bfp->GetBlob(i));
		else if (color == COLOR_BLUE) {
			blueBlobs.push_back(bfp->GetBlob(i));
		}
	}

	bfp->NotFresh();

	vector<Observation> pinkOverBlue = findMarkersFromBlobs(pinkBlobs,
			blueBlobs, "p/b");
	vector<Observation> blueOverPink = findMarkersFromBlobs(blueBlobs,
			pinkBlobs, "b/p");
	vector<Observation> pinkOverGreen = findMarkersFromBlobs(pinkBlobs,
			greenBlobs, "p/g");
	vector<Observation> greenOverPink = findMarkersFromBlobs(greenBlobs,
			pinkBlobs, "g/p");
	vector<Observation> pinkOverYellow = findMarkersFromBlobs(pinkBlobs,
			yellowBlobs, "p/y");
	vector<Observation> yellowOverPink = findMarkersFromBlobs(yellowBlobs,
			pinkBlobs, "y/p");
	vector<Observation> pinkOverOrange = findMarkersFromBlobs(pinkBlobs,
			orangeBlobs, "p/o");
	vector<Observation> orangeOverPink = findMarkersFromBlobs(orangeBlobs,
			pinkBlobs, "o/p");

	obs.insert(obs.begin(), pinkOverBlue.begin(), pinkOverBlue.end());
	obs.insert(obs.begin(), blueOverPink.begin(), blueOverPink.end());
	obs.insert(obs.begin(), pinkOverGreen.begin(), pinkOverGreen.end());
	obs.insert(obs.begin(), greenOverPink.begin(), greenOverPink.end());
	obs.insert(obs.begin(), pinkOverYellow.begin(), pinkOverYellow.end());
	obs.insert(obs.begin(), yellowOverPink.begin(), yellowOverPink.end());
	obs.insert(obs.begin(), pinkOverOrange.begin(), pinkOverOrange.end());
	obs.insert(obs.begin(), orangeOverPink.begin(), orangeOverPink.end());

	for (int i = 0; i < pinkBlobs.size(); i++) {
		obs.push_back(Observation("pink", map, getAngle((pinkBlobs[i].left
				+ pinkBlobs[i].right) / 2), observationVariance));
	}

	robotMutex.unlock();
}

int InterfaceToLocalization::getBlobColor(player_blobfinder_blob blob) {
	uint32_t color = blob.color;
	int b = color % 256;
	color = color / 256;
	int g = color % 256;
	color = color / 256;
	int r = color % 256;

	if (r == 255 && g == 0 && b == 255)
		return COLOR_PINK;
	else if (r == 255 && g == 255 && b == 0)
		return COLOR_YELLOW;
	else if (r == 0 && g == 255 && b == 0)
		return COLOR_GREEN;
	else if (r == 0 && g == 0 && b == 255)
		return COLOR_BLUE;
	else if (r == 255 && g == 128 && b == 0)
		return COLOR_ORANGE;
	else {
		return -1;
	}
}

vector<Observation> InterfaceToLocalization::findMarkersFromBlobs(vector<
		player_blobfinder_blob>& topBlobs,
		vector<player_blobfinder_blob>& bottomBlobs, string id) {

	vector<Observation> obs;
	vector<player_blobfinder_blob> newTopBlobs;

	for (int i = 0; i < topBlobs.size(); i++) {
		bool matchedBlob = false;
		for (vector<player_blobfinder_blob>::iterator j = bottomBlobs.begin(); j
				!= bottomBlobs.end(); j++) {

			player_blobfinder_blob bottom = *j;
			player_blobfinder_blob top = topBlobs[i];

			if (blobOnTopOf(top, bottom)) {
				Observation observation = Observation(id, map,
						this->getAngle((bottom.right + bottom.left + top.right
								+ top.left) / 4), observationVariance);

				obs.push_back(observation);

				bottomBlobs.erase(j);
				matchedBlob = true;
				break;
			}
		}

		if (!matchedBlob)
			newTopBlobs.push_back(topBlobs[i]);
	}

	topBlobs = newTopBlobs;

	return obs;
}

bool InterfaceToLocalization::blobOnTopOf(player_blobfinder_blob top,
		player_blobfinder_blob bottom) {
	return (top.left < bottom.right && top.right > bottom.left && top.bottom
			< bottom.top + 3 && top.bottom + 50 > bottom.top);
}

double InterfaceToLocalization::getAngle(double x) {
	int xRes = 160;
	int halfXRes = 80;

	double angleLeftOfCenter = -1 * (x - halfXRes) / xRes * Utils::toRadians(
			fov);
	return angleLeftOfCenter;
}

double InterfaceToLocalization::radiansToDegrees(double rad) {
	return rad * 180 / M_PI;
}

bool InterfaceToLocalization::positionEqual(Position p1, Position p2) {
	double absx = fabs(p1.getX() - p2.getX());
	double absy = fabs(p1.getY() - p2.getY());
	double abst = fabs(p1.getTheta() - p2.getTheta());

	return (absx == 0 && absy == 0 && Utils::toRadians(abst) == 0);
}

