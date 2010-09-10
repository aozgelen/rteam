#ifndef _BEHAVIOR_H
#define _BEHAVIOR_H

#include "libplayerc++/playerc++.h"

// Abstract class from which all behaviors are derived.
class Behavior
{
	public:
		Behavior(PlayerCc::PlayerClient& pc)
		:mPlayerClient(pc) { }
		virtual ~Behavior() {}

		// The interface required for all behaviors.
		virtual void Restart() = 0;
		virtual void Stop() = 0;
		virtual void Update() = 0;

	protected:
		// Binding to Player server
		PlayerCc::PlayerClient& mPlayerClient;
};

#endif
