#include "wallAvoid.h"
#include <climits>
#include <ctime>
#include <cmath>
#include <iostream>
#include <iomanip>
using namespace PlayerCc;
using namespace std;

static const double BOTTOMTHRESHOLD = 5;  // in pixels

static bool is_moving(Position2dProxy& pp)
{
  return pp.GetXSpeed() || pp.GetYawSpeed();
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
	   << "\tcolor: " << bl.color                     // this is returning HEX value -> change mode
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

WallAvoid::WallAvoid(PlayerClient& pc)
  :Behavior(pc), mPosition2D(&mPlayerClient, 0), mBlobfinder(&mPlayerClient, 0)
{
  srand(time(0));
}

WallAvoid::~WallAvoid() 
{
  Stop();
}

void WallAvoid::Restart(){}

void WallAvoid::Stop()
{
  mPosition2D.SetSpeed(0, 0, 0);
}

void WallAvoid::Update()
{
  if( is_wall_visible(mBlobfinder) ) {
    Stop(); 
    cout << "Wall in front! Turning around" << endl ; 
    // make a turn to left for now
    mPosition2D.SetSpeed(0,0,15);
    usleep(100000);
    mPosition2D.SetSpeed(0,0,0);
  }
  else{
    mPosition2D.SetSpeed(0.15,0,0);
  }
}
