/**
 * pose.cpp
 *
 * manashirov&sklar/28-june-2010
 *
 */

#include <iostream>
#include <cstdlib>
#include <cstdio>
#include "pose.h"




/**
 * Pose constructor
 *
 */
pose::pose() {
  x = 0;
  y = 0;
  theta = 0.0;
  confidence = 0.0;
}
pose::pose( int x0, int y0, float theta0 ) {
  x = x0;
  y = y0;
  theta = theta0;
  confidence = 0.0;
}
pose::pose( int x0, int y0, float theta0, float confidence0 ) {
  x = x0;
  y = y0;
  theta = theta0;
  confidence = 0.0;
} // end of Pose constructor



/**
 * pose destructor
 *
 */
pose::~pose() {
} // end of pose destructor



/**
 * set()
 *
 * This function sets the data values for this class.
 *
 */
void pose::set( int x0, int y0, float theta0 ) {
  x = x0;
  y = y0;
  theta = theta0;
}
void pose::set ( int x0, int y0, float theta0, float confidence0 ) {
  x = x0;
  y = y0;
  theta = theta0;
  confidence = 0.0;
} // end of set()

    
/**
 * set_x()
 *
 * This function sets the value of the "x" data field.
 *
 */
void pose::set_x( int x0 ) {
  x = x0;
} // end of set_x()

/**
 * get_x()
 *
 * This function returns the value of the "x" data field.
 *
 */
int pose::get_x() {
  return( x );
} // end of get_x()


/**
 * set_y()
 *
 * This function sets the value of the "y" data field.
 *
 */
void pose::set_y( int y0 ) {
  y = y0;
} // end of set_y()

/**
 * get_y()
 *
 * This function returns the value of the "y" data field.
 *
 */
int pose::get_y() {
  return( y );
} // end of get_y()


/**
 * set_theta()
 *
 * This function sets the value of the "theta" data field.
 *
 */
void pose::set_theta( float theta0 ) {
  theta = theta0;
} // end of set_theta()

/**
 * get_theta()
 *
 * This function returns the value of the "theta" data field.
 *
 */
float pose::get_theta() {
  return( theta );
} // end of get_theta()


/**
 * set_confidence()
 *
 * This function sets the value of the "confidence" data field.
 *
 */
void pose::set_confidence( float confidence0 ) {
  confidence = confidence0;
} // end of set_confidence()

/**
 * get_confidence()
 *
 * This function returns the value of the "confidence" data field.
 *
 */
float pose::get_confidence() {
  return( confidence );
} // end of get_confidence()


/**
 * toString()
 *
 * This function returns a string that encodes the data fields in this class.
 *
 */
char *pose::toString() {
  char *tmp = (char *)malloc( 25 * sizeof( char ));
  sprintf( tmp,"%4d %4d %7.3f %6.4f\0", x, y, theta, confidence );
  return( tmp );
} // end of toString()
