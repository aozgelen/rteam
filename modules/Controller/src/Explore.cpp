#include "Explore.h"

using namespace PlayerCc;

Explore::Explore(PlayerClient& pc)
  :Behavior(pc), mPosition2D(&mPlayerClient, 0), mBlobfinder(&mPlayerClient, 0) {}

Explore::~Explore(){}

void Explore::Restart() {
  
}

void Explore::Stop() {
  
}

void Explore::Update() {}
