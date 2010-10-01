#ifndef BLOBFINDER_BLOB_H
#define BLOBFINDER_BLOB_H

#include "libplayerc++/playerc++.h"

class blobfinder_blob : public player_blobfinder_blob {
  int id;
  int color;
  int area;
  int x;
  int y;
  int left;
  int right;
  int top;
  int bottom;
  float range;
public:
  blobfinder_blob(); 
 
  blobfinder_blob(int i, int c, int a, int tx, int ty, int l, int r, int t, int b, float ra) : 
    id(i), color(c), area(a), x(tx), y(ty), left(l), right(r), top(t), bottom(b), range(ra){}
  
  blobfinder_blob(player_blobfinder_blob& pbb): id(pbb.id), color(pbb.color), 
    area(pbb.area), x(pbb.x), y(pbb.y), left(pbb.left), right(pbb.right), 
    top(pbb.top), bottom(pbb.bottom), range(pbb.range){}
};

#endif
