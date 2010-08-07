#include "render.h"

bool was_resized = false;
pthread_mutex_t paint_mutex;
pthread_mutex_t paint_refresh;
queue<string> painter_queue;

// 
// construct_msg: Constructs strings into standard format
// Parameters:
//
string construct_msg(string type, string message, long robot_id, string name){
  time_t rawtime;
  struct tm *tm;
  time ( &rawtime );
  tm = localtime ( &rawtime );
  ostringstream buffer;
  char time_buffer[] = "00:00:00";
  string tmp;

  snprintf(time_buffer, sizeof(time_buffer),
	  "%02d:%02d:%02d", tm->tm_hour, tm->tm_min, tm->tm_sec);

  buffer << time_buffer << " ";

  buffer << "[ " << type << " ]" << " ";
  buffer << message << " ";

  if(type == "SENT"){
    buffer << "[ TO ]" << " ";
  }else if(type == "RECIEVED"){
    buffer << "[ BY ]" << " ";
  }else if(type == "STATUS"){
    buffer << "[ STATUS ]" << " ";
  }else if(type == "PUSHED"){
    buffer << "[ ONTO ]" << " ";
  }else if(type == "POPPED"){
    buffer << "[ OFF ]" << " ";
  }else{
    buffer << "[ ERROR ]" << " ";
  }

  if(robot_id != 0){
      buffer << name << " ";
      buffer << robot_id;
  }else{
      buffer << "SKYGRID" << " ";
      buffer << 0;
  }

  return buffer.str();

}

void push_paint(string type, string message, long robot_id, string name){
  string msg = construct_msg(type, message, robot_id, name);

  pthread_mutex_lock( &paint_mutex );

  if(!msg.empty()){
    painter_queue.push( msg );
  }else{
    //cout << "Got empty string" << endl;
  }

  pthread_mutex_unlock( &paint_mutex );
  
}

const string pop_paint(){
  string msg;                                                                   
  pthread_mutex_lock( &paint_mutex );

  if ( painter_queue.empty() ) {
    msg.clear();
  }
  else {
    msg = painter_queue.front();
    painter_queue.pop();
  }

  pthread_mutex_unlock( &paint_mutex );

  return( msg );
}

bool paint_empty() {                                                       
  bool empty = false;

  pthread_mutex_lock( &paint_mutex );

  if(painter_queue.empty()){
    empty = true;
  }

  pthread_mutex_unlock( &paint_mutex );

  return empty;
}

void print_error(WINDOW *win, string err_msg){
    time_t rawtime;
    struct tm * tm;

    time ( &rawtime );
    tm = localtime ( &rawtime );
    
    wattrset(win, A_DIM | COLOR_PAIR(8));
    wprintw(win, " %02d:%02d:%02d ",
	  tm->tm_hour, tm->tm_min, tm->tm_sec);
    wattrset(win, COLOR_PAIR(3) | A_BOLD);
    wprintw(win, "[ ERROR ]");

    wattrset(win, COLOR_PAIR(9) | A_BOLD | A_DIM);
    wprintw(win, " %s\n", err_msg.c_str());

    // call general msg colorer
}

void color_msg(WINDOW *win, string message){

  char buffer[255];
  char *tmp;
  size_t found;

  time_t rawtime;
  struct tm *tm;
  time ( &rawtime );
  tm = localtime ( &rawtime );

  strncpy(buffer, message.c_str(), 255);

  // Print time
  tmp = buffer;
  found = message.find(" ");
  buffer[found] = '\0';

  wattrset(win, A_DIM | COLOR_PAIR(8));
  wprintw(win, "%s ", tmp);

  // Print type
  ++found;
  tmp = &buffer[found];
  found = message.find("]", found);
  ++found;
  buffer[found] = '\0';

  wattrset(win, A_BOLD|COLOR_PAIR(5));
  wprintw(win, "%s ", tmp);

  // Print command
  ++found;
  tmp = &buffer[found];
  found = message.find(" ", found);
  buffer[found] = '\0';

  wattrset(win, A_BOLD|COLOR_PAIR(4));
  wprintw(win, "%s ", tmp);

  // Print rest of command
  ++found;
  tmp = &buffer[found];
  found = message.find("[", found);
  --found;
  buffer[found] = '\0';

  wattrset(win, A_BOLD | COLOR_PAIR(8));
  wprintw(win, "%s ", tmp);
  
  // Print action
  ++found;
  tmp = &buffer[found];
  found = message.find("]", found);
  ++found;
  buffer[found] = '\0';

  wattrset(win, A_BOLD|COLOR_PAIR(5));
  wprintw(win, "%s ", tmp);

  // Print username
  ++found;
  tmp = &buffer[found];
  found = message.find(" ", found);
  buffer[found] = '\0';

  wattrset(win, A_BOLD|COLOR_PAIR(10));
  wprintw(win, "%s ", tmp);

  // Print ID
  ++found;
  tmp = &buffer[found];
  wattrset(win, A_BOLD|COLOR_PAIR(2));
  wprintw(win, "%s ", tmp);

  wprintw(win, "\n\n");

}

// Catch SIGWINCH Signal
void catch_resize(int sig){
  pthread_mutex_lock( &paint_refresh );
  was_resized = !was_resized;
  pthread_mutex_unlock( &paint_refresh );
}
