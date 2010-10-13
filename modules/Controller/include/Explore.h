#ifndef _EXPLORE_H
#define _EXPLORE_H

#include "behavior.h"

class Explore : public Behavior
{
 public:
  Explore(PlayerCc::PlayerClient& pc);
  ~Explore();
  
  virtual void Restart();
  virtual void Stop();
  virtual void Update();
  
 protected:
  // Player client proxies.
  PlayerCc::Position2dProxy mPosition2D;
  PlayerCc::BlobfinderProxy mBlobfinder;
  
  // Internal properties.
  bool mInitState;
  bool mIsMobile;
};

#endif
