#include <iostream>
#include <cstdio>
#include <cstdlib>
#include <sstream>
#include <string>
#include "robot.h"

using namespace::std;

//INIT <type> <name> <num-provides> <provides>
int init_client( char *msgbuf, long *robot_id) {
  robot test;
  string p(msgbuf);
  istringstream istream(p);

  string command;
  string type;
  string name;
  int num_provides;
  int provides;

  cout << "Got string: " << p << endl;

  istream >> command >> type >> name >> num_provides >> provides;

  cout << "Command: " << command << endl;
  cout << "Type: " << type << endl;
  cout << "Name: " << name << endl;
  cout << "Num of Provides: " << num_provides << endl;
  cout << "Provides: " << provides << endl;

  *msgbuf = 'X';

  return 0;

} 

int main(int argc, char *argv[])
{

  long id = 1111;
  char *msg = (char *)malloc(500);
  string test = "ACK";
  strncpy(msg, "INIT gui ELGUI 0 0", 500);
  init_client(msg, &id);
  robot smith;

  test += " ";
  test += "19234";

  cout << "Modded: " << msg << endl;

  cout << "Test: " << test << endl;

  smith.push_msg("Push");
  smith.push_msg("Pop");

  cout << smith.pop_msg() << endl;

  if(smith.peek_msg()){
      cout << smith.pop_msg() << endl;
  }else{
    cout << "Empty!" << endl;
  }
    
  free(msg);

  return 0;
}
