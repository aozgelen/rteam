#ifndef _RENDER_H_
#define _RENDER_H_

#include <sstream>
#include <fstream>
#include <cstring>
#include <ctime>
#include <regex.h>
#include <signal.h>
#include <ncurses.h>
#include <string>
#include <queue>
#include <pthread.h>
#include <iostream>

#define MAIN_TITLE "Skygrid 1.0"

using namespace std;

struct thread_args{
  bool interactive;
  bool log_enabled;
  char logfile[32];
  char ip[16];
  int port;
};

string construct_msg(string type, string message, long robot_id, string name);
void push_paint(string type, string message, long robot_id, string name);
const string pop_paint();
bool paint_empty();
void print_error(WINDOW *win, string err_msg);
void color_msg(WINDOW *win, string message);
void catch_resize(int sig);
void *pencil( void *arg );
void *paintbrush( void *arg );
void interactive_setup();

#endif
