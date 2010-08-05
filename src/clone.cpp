#include <iostream>
#include <string>

using namespace std

#include "commClient.h"

int main(int argc, char *argv[]){
  string init = "INIT surveyor beefy 0 0";

  commClient comm("192.168.1.47", 6667);

  comm.send_msg(init.size(), init.c_str());

  return 0;

}
