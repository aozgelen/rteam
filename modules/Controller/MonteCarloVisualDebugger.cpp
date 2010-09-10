/*
 * MonteCarloVisualDebugger.cpp
 *
 *  Created on: Apr 15, 2010
 *      Author: appleapple
 */

#include <GL/gl.h>
#include <GL/glut.h>
#include "MonteCarloVisualDebugger.h"

vector<Particle> sParticles;
double sConfidence;

MonteCarloVisualDebugger::MonteCarloVisualDebugger() {

}

void MonteCarloVisualDebugger::debug() {
	sParticles = particles;
	printf("particles: %d", particles.size());
//	draw();
}


void MonteCarloVisualDebugger::init(void) {
	glClearColor(0.0, 0.0, 0.0, 0.0);
	glShadeModel ( GL_FLAT);
}

void MonteCarloVisualDebugger::reshape(int w, int h) {
	glViewport(0, 0, (GLsizei) w, (GLsizei) h);
	glMatrixMode( GL_PROJECTION);
	glLoadIdentity();
	gluOrtho2D(-1 * w / 2, (GLdouble) w / 2, -50.0, (GLdouble) h - 50);
}
