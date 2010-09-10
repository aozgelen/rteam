#include "loiter.h"
#include <climits>
#include <ctime>
#include <cmath>
#include <iostream>
#include <iomanip>
using namespace PlayerCc;
using namespace std;

static const double GRID_WIDTH  = 5.0;
static const double GRID_HEIGHT = 5.0;
static const double EPSILON = 0.01;
static const double BOTTOMTHRESHOLD = 5;  // in pixels

static bool is_moving(Position2dProxy& pp)
{
  return pp.GetXSpeed() || pp.GetYSpeed() || pp.GetYawSpeed();
}

static bool is_wall_visible(BlobfinderProxy& bfp){
  //cout << "checking if wall is visible" << endl;
  int noBlobs = bfp.GetCount(); 
  bool wall = false; 
  if( noBlobs != 0 ){
    bool floor = false ;
    for( int i=0; i < noBlobs; i++ ){
      playerc_blobfinder_blob_t bl = bfp.GetBlob(i); 
      cout << "found blob - left:" << bl.left 
	   << "\ttop: " << bl.top
	   << "\tright: " << bl.right
	   << "\tbottom: " << bl.bottom
	   << "\tx: " << bl.x
	   << "\ty: " << bl.y
	   << "\tcolor: " << bl.color
	   << "\tarea: " << bl.area << endl;
      if ( bl.bottom > bfp.GetHeight() - BOTTOMTHRESHOLD ){
	floor = true; 
	break;
      }
    }
    if ( !floor ) {
      cout << "floor not visible! there must be a wall in front of me" << endl;
      wall = true;
    }
  }
  return wall; 
}

Loiter::Loiter(PlayerClient& pc)
  :Behavior(pc), mPosition2D(&mPlayerClient, 0), mBlobfinder(&mPlayerClient, 0)
{
  srand(time(0));
  mInitState = false;
  xPos = yPos = 0;
}

Loiter::~Loiter()
{
  Stop();
}

void Loiter::Restart()
{
  mInitState = false;
  xPos = yPos = 0;
}

void Loiter::Stop()
{
  mPosition2D.SetSpeed(0, 0);
  mInitState = false;
}

void Loiter::Update()
{
  if( is_wall_visible(mBlobfinder) ) {
    Stop(); 
    cout << "Wall in front" << endl ; 
    //exit(1);
  };
  mIsMobile = is_moving(mPosition2D);
  if (!mInitState && mIsMobile) {
    mPosition2D.SetSpeed(0, 0);
  } else if (!mInitState && !mIsMobile) {
    // go to a new position on the grid
    xPos = ((rand()/(float(RAND_MAX)+1)) * GRID_WIDTH)+0.5;
    yPos = ((rand()/(float(RAND_MAX)+1)) * GRID_HEIGHT)+0.5;
    cerr << "LOITER: going to new position: ("
	 << setprecision(2) << xPos << ", "
	 << setprecision(2) << yPos << ")" << endl;
    mPosition2D.GoTo(xPos, yPos, INT_MAX);
    mInitState = true;
  } else if (mInitState && !mIsMobile &&
	     ((fabs(mPosition2D.GetXPos() - xPos) >= EPSILON) ||
	      (fabs(mPosition2D.GetYPos() - yPos) >= EPSILON))) {
    mPosition2D.GoTo(xPos, yPos, INT_MAX);
  } else if ((fabs(mPosition2D.GetXPos() - xPos) < EPSILON) &&
	     (fabs(mPosition2D.GetYPos() - yPos) < EPSILON)) {
    mPosition2D.SetSpeed(0, 0);
    mInitState = false;
  }
}
