/*
 * PathTester.cpp
 *
 *  Created on: Aug 30, 2010
 *      Author: robotics
 */

#include "PathTester.h"
#include <iostream> 
using namespace std; 

void PathTester::operator()() {
  while (true) {
    
    cout << "Enter start x: " ; 
    int xs; 
    cin >> xs; 
    cout << "Enter start y: " ; 
    int ys; 
    cin >> ys;

    Node ns(1, xs, ys); 
    planner->setSource(ns);

    cout << "Enter target x: " ; 
    int xt; 
    cin >> xt; 
    cout << "Enter target y: " ; 
    int yt; 
    cin >> yt;

    Node nt(1, xt, yt); 
    planner->setTarget(nt);
    
    planner->calcPath(); 
  }
}
