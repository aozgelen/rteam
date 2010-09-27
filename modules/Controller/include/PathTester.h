#ifndef PATH_TESTER_H
#define PATH_TESTER_H

#include "PathPlanner.h"

class PathTester {
public:
  PathTester(PathPlanner * pp): planner(pp){}

  void operator()();
private:
  PathPlanner * planner;
};

#endif /* PATH_TESTER_H */
