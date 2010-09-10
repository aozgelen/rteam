/**
 * pose.h
 *
 * manashirov&sklar/28-june-2010
 *
 */

#ifndef _POSE_H
#define _POSE_H

class pose {

 private:
  int x;
  int y;
  float theta;
  float confidence;

 public:  
  pose();
  pose( int x0, int y0, float theta0 );
  pose( int x0, int y0, float theta0, float confidence0 );
  ~pose();

  void set( int x0, int y0, float theta0 );
  void set ( int x0, int y0, float theta0, float confidence0 );

  void set_x( int x0 );
  int get_x();

  void set_y( int y0 );
  int get_y();

  void set_theta( float theta0 );
  float get_theta();

  void set_confidence( float confidence0 );
  float get_confidence();

  char *toString();

}; // end of pose class

#endif

