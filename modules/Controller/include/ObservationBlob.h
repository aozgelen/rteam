#ifndef OBSERVATION_BLOB
#define OBSERVATION_BLOB

#define COLOR_PINK 0
#define COLOR_YELLOW 1
#define COLOR_BLUE 2
#define COLOR_GREEN 3
#define COLOR_ORANGE 4

class observationBlob {
public:
  observationBlob(int c, int l, int r, int t, int b, double angle) 
    : color(c), left(l), right(r), top(t), bottom(b), used(false), bearing(angle) {
    x = static_cast<int>((right - left)/2);
    y = static_cast<int>((bottom - top)/2);
    area = ( right - left ) * ( bottom - top ) ;
  }
  
  void print() const {
    cout << "blob is at (" << x << "," << y << "), color:" ; 
    printColor();
    cout << ", area:" << area << ", top:" << top << ", bottom:" << bottom 
	 << ", left:" << left << ", right:" << right << endl;
  }

  void printColor() const {
    switch( color ){
    case COLOR_PINK:
      cout << "pink" ; 
      break; 
    case COLOR_YELLOW: 
      cout << "yellow" ; 
      break;
    case COLOR_BLUE: 
      cout << "blue" ; 
      break;
    case COLOR_GREEN: 
      cout << "green" ; 
      break;
    case COLOR_ORANGE: 
      cout << "orange" ; 
      break;
    }
  }

  double Bearing() const { return bearing; }
  int Color() const { return color; }
  int Left() const { return left; }
  int Right() const { return right; }
  int Top() const { return top; }
  int Bottom() const { return bottom; }
  int Area() const { return area; }
  int X() const { return x; }
  int Y() const { return y; }
  bool Used() const { return used; }
  void setInUse() { used = true; }

private:
  int color; 
  int left; 
  int right; 
  int top; 
  int bottom; 
  double bearing; 

  int area; 
  int x; 
  int y; 
  bool used; 
};

#endif
