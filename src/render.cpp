#include "render.h"

WINDOW *title_bar;
WINDOW *output;
WINDOW *status_bar;
WINDOW *filter_menu;
WINDOW *console;

bool was_resized = false;
pthread_mutex_t paint_mutex;
pthread_mutex_t paint_refresh;
queue<string> painter_queue;

extern int num_threads;

int get_term_size(int *width, int *height)
{
        struct winsize ws; 

        if (ioctl(0, TIOCGWINSZ, &ws) < 0)
                return FALSE;

        *width = ws.ws_col;
        *height = ws.ws_row;

        return TRUE;
}

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

void *pencil( void *arg ) {
  string buffer;
  ofstream log;
  thread_args *t_args = (thread_args *)arg;

  if(t_args->log_enabled){
    log.open(t_args->logfile);
  }

  for(;;){
    if(paint_empty())
      continue;

    buffer = pop_paint();

    cout << buffer << endl;

    if(t_args->log_enabled){
      log << buffer << endl;
    }

    usleep(250);
  }

}

/**
 * paintbrush()
 *
 * This is a little paintbrush function that is spawned by a thread in the server.
 * It provides a count to show that the server is still alive.
 *
 */
void *paintbrush( void *arg ) {
  time_t rawtime;
  struct tm *tm;
  int ch;
  bool paused = false;
  bool filter_visible = false;
  bool console_visible = false;
  string buffer;
  int center;
  ofstream log;
  thread_args *t_args = (thread_args *)arg;

  if(t_args->log_enabled){
    log.open(t_args->logfile);
  }

  char paused_msg[] = "[ PAUSED ]";

  regex_t regx;
  int x_coord = 0;
  int y_coord = 0;
  const int input_size = 40;
  const int console_input_size = 40;
  char input_buffer[input_size];
  char console_buffer[console_input_size];

  int x, y;
  int height, width;

  int dummy = 0;

  getmaxyx(stdscr, y, x); 
  center = x - sizeof(MAIN_TITLE);
  center = center / 2;

  regcomp(&regx, ".", REG_EXTENDED);

  while ( 1 ) {
    time ( &rawtime );
    tm = localtime ( &rawtime );

    werase(status_bar);
    wprintw(status_bar, "[ %02d:%02d:%02d ]",
	  tm->tm_hour, tm->tm_min, tm->tm_sec);
    wprintw(status_bar, "%10s %d", " Clients Connected:", num_threads);
    wprintw(status_bar, "%15s %s %s %d",
	    " SERVER IP:", t_args->ip, "SERVER PORT:", t_args->port);

    ch = getch();

    pthread_mutex_lock( &paint_refresh );

    if(was_resized){
      was_resized = !was_resized;
      //get_term_size(&width, &height);
      //resizeterm(width, height);
      endwin();
      refresh();
      touchwin(output);
      refresh();
    }

    pthread_mutex_unlock( &paint_refresh );

    if(filter_visible){
        if(ch == ESCAPE_KEY){ // Escape Key
          filter_visible = false;
          touchwin(output);
          refresh();
        }else if(ch == ENTER_KEY){ // Enter Key
          filter_visible = false;
          input_buffer[x_coord] = '\0';

          if(x_coord == 0){
              regcomp(&regx, ".", REG_EXTENDED | REG_ICASE);
          }else{
              regcomp(&regx, input_buffer, REG_EXTENDED | REG_ICASE);
          }

          touchwin(output);
          refresh();
        }else if(ch == BACKSPACE_KEY){ // Backspace Key
          if(x_coord > 0){
              wmove(filter_menu, 1, 9 + x_coord);
              waddch(filter_menu, ' ');
              input_buffer[x_coord] = '\0';
              --x_coord;
          }else{
	    flash();
	  }
        }else if(ch == CTRL_U){ // Control + U: Erase line
            werase(filter_menu);
            mvwprintw(filter_menu, 1, 2,  "%s", "Filter: ");
            box(filter_menu, 0, 0);
            x_coord = 0;
	}else if(ch != ERR){
          if(x_coord < input_size){
              wmove(filter_menu, 1, 10 + x_coord);
              waddch(filter_menu,ch);
              input_buffer[x_coord] = (char) ch;
              ++x_coord;
          }else{
            flash();
          }
        }

    }else if(console_visible){
        if(ch == ESCAPE_KEY || ch == '~'){ // Escape Key or tilde
          console_visible = false;
          touchwin(output);
          refresh();
        }else if(ch == BACKSPACE_KEY){ // Backspace Key
          if(x_coord > 0){
	      getyx(console, y_coord, dummy);
              wmove(console, y_coord, 1 + x_coord);
              waddch(console, ' ');
              console_buffer[x_coord] = '\0';
              --x_coord;
          }else{
	    flash();
	  }
        }else if(ch == CTRL_U){ // Control + U: Erase line
	    getyx(console, y_coord, x_coord);
	    wmove(console, y_coord, 1);
	    wclrtoeol(console);
	    //refresh();
            x_coord = 0;
        }else if(ch == ENTER_KEY){ // Enter Key
          console_buffer[x_coord] = '\0';
	  ++y_coord;
	  x_coord = 0;
	  wprintw(console, "\nRecieved: %s", console_buffer);
	  wprintw(console, "\n> ");

	  // Pass console buffer to warden

          touchwin(output);
          refresh();

	}else if(ch != ERR){
          if(x_coord < console_input_size){
	      getyx(console, y_coord, dummy);
              wmove(console, y_coord, 2 + x_coord);
              waddch(console, ch);
              console_buffer[x_coord] = (char) ch;
              ++x_coord;
          }else{
            flash();
          }
	}



      }else{
          if(ch == ' '){

            paused = !paused;
	    if(!paused){
	      werase(title_bar);
	      mvwprintw(title_bar, 0, center,  "%s", MAIN_TITLE);
	    }else{
	      werase(title_bar);
	      mvwprintw(title_bar, 0, center - sizeof(paused_msg),
		      "%s", paused_msg);

	      mvwprintw(title_bar, 0, center, "%s %s",
		      MAIN_TITLE, paused_msg);
	    }

          }else if(ch == 'f' || ch == 'F'){
            filter_visible = !filter_visible;
            werase(filter_menu);
            mvwprintw(filter_menu, 1, 2,  "%s", "Filter: ");
            box(filter_menu, 0, 0);
            x_coord = 0;
          }else if(ch == '~'){
            console_visible = !console_visible;
            werase(console);
            mvwprintw(console, 0, 0,  "> ");
	    touchwin(output);
            x_coord = 0;
            y_coord = 0;
	    refresh();

          }else if(ch == 'q' || ch == 'Q'){
            endwin();
	    exit(0);
          }
	 
      }
    
    wnoutrefresh(status_bar);
    touchwin(status_bar);
    touchwin(title_bar);
    touchwin(filter_menu);
    touchwin(console);

    if(!paused){
      wnoutrefresh(output);
    }

    if(filter_visible){
      wnoutrefresh(filter_menu);
    }

    if(console_visible){
	wnoutrefresh(console);
    }

    wnoutrefresh(title_bar);
    wnoutrefresh(status_bar);

    doupdate();

    if(paint_empty())
      continue;

    buffer = pop_paint();

    if(t_args->log_enabled){
      log << buffer << endl;
    }
    
    if(regexec(&regx, buffer.c_str(), 0, 0, 0) == 0){
	  color_msg(output, buffer);
    }

    usleep(1); // give up some cpu time
  }


} // end of paintbrush()

void interactive_setup(){
  int x, y, center;

      initscr();
      start_color();

      getmaxyx(stdscr, y, x); 

      center = x - sizeof(MAIN_TITLE);
      center = center / 2;

      title_bar = newwin(1, x, 0, 0); 
      output = newwin(y - 1, x, 2, 0); 
      status_bar = newwin(1, x, y - 1, 0); 
      console = newwin(y / 4, x, y - (y/3) + 1, 0); 
      filter_menu =
	  newwin(3, x - x / 4 , (y - 3) / 2, ( x - (x - x / 4)) / 2 );

      init_pair(1, COLOR_BLACK, COLOR_WHITE);
      init_pair(2, COLOR_GREEN, COLOR_BLACK);
      init_pair(3, COLOR_BLACK, COLOR_RED);
      init_pair(4, COLOR_CYAN, COLOR_BLACK);
      init_pair(5, COLOR_MAGENTA, COLOR_BLACK);
      init_pair(6, COLOR_WHITE, COLOR_BLACK);
      init_pair(7, COLOR_YELLOW, COLOR_BLACK);
      init_pair(8, COLOR_WHITE, COLOR_BLACK);
      init_pair(9, COLOR_RED, COLOR_BLACK);
      init_pair(10, COLOR_BLUE, COLOR_BLACK);
      init_pair(11, COLOR_WHITE, COLOR_BLUE);

      scrollok(output, TRUE);
      nodelay(output, TRUE);
      scrollok(console, TRUE);
      noecho();
      nodelay(stdscr, TRUE);
      curs_set(0);

      wbkgd(title_bar, COLOR_PAIR(11) | A_BOLD | A_DIM );
      wbkgd(status_bar, COLOR_PAIR(1) | A_BOLD | A_DIM );
      wbkgd(filter_menu, COLOR_PAIR(11) | A_BOLD | A_DIM );
      wbkgd(console, COLOR_PAIR(11) | A_BOLD | A_DIM );

      mvwprintw(title_bar, 2, center,  "%s", MAIN_TITLE);

      wprintw(status_bar, "[ 00:00:00 ]");
      wprintw(status_bar, "%50s %d", "Clients Connected:", 0);
      wnoutrefresh(title_bar);
      wnoutrefresh(status_bar);

      signal(SIGWINCH, catch_resize);

      doupdate();

}
