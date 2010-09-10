/*
 * MonteCarloVisualDebugger.h
 *
 *  Created on: Apr 15, 2010
 *      Author: appleapple
 */

#ifndef MONTECARLOVISUALDEBUGGER_H_
#define MONTECARLOVISUALDEBUGGER_H_

#include <MonteCarloDebugger.h>

class MonteCarloVisualDebugger : public MonteCarloDebugger{
public:
	MonteCarloVisualDebugger();

	void debug();

	void static init(void);
	void static reshape(int w, int h);
//	void static draw(void);

private:
};

#endif /* MONTECARLOVISUALDEBUGGER_H_ */
