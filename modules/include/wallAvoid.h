#ifndef _WALLAVOID_H
#define _WALLAVOID_H

#include "behavior.h"

class WallAvoid : public Behavior
{
	public:
		WallAvoid(PlayerCc::PlayerClient& pc);
		~WallAvoid();
		
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
