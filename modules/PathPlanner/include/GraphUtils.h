#ifndef GRAPH_UTILS_H
#define GRAPH_UTILS_H

#include <stdlib.h>
#include <vector>

using namespace std; 

bool get_line_intersection(double p0_x, double p0_y, double p1_x, double p1_y, 
			   double p2_x, double p2_y, double p3_x, double p3_y, double *i_x, double *i_y) ;


double get_euclidian_distance(double, double, double, double);

int get_manhattan_distance(int, int, int, int);

#endif
