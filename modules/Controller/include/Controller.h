#ifndef CONTROLLER_H
#define CONTROLLER_H

#include "robot.h"

class Controller {
public:
  Controller(Robot * r): rbt(r){}  
  void operator()();
private:
  Robot * rbt;
};

#endif /* CONTROLLER_H */
