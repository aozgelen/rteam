#include <iostream>
#include <cstring>
#include "Warden.h"
#include "pluginmanager.h"

#define PluginManager	PluginManager::Instance()

using namespace::std;


Warden *Warden::Instance(){
    static Warden instance;

    return &instance;
}

string Warden::eval(string msg){
  string formatted = format_message(msg);
  string response;
  PluginManager->load("./stopwatch.lua");
  response = PluginManager->execute(formatted);
  
  tokenize_message(formatted);
  //cout << formatted << endl;
  
  return response;

}

string Warden::format_message(string msg){
  string::iterator it;
  transform ( msg.begin(), msg.end(), msg.begin(), ptr_fun(::toupper) );

  for(it = msg.begin(); it != msg.end(); ++it){

    if(*it == ' '){
      if(*(it + 1) == ' '){
	msg.erase(it);
	--it;
      }
    }else if(*it == ','){
      if(*(it - 1) == ' '){
	msg.erase(it - 1);
	it-=2;
      }else if(*(it + 1) == ' '){
	msg.erase(it + 1);
	it-=2;
      }
    }

  }

  return msg;

}

void Warden::tokenize_message(string message){

  string keyword;
  string argument;
  vector<string> parameters;
  map< string, vector<string> > robot_rule;
  size_t found;

  char *argptr;
  char *msgptr = new char[message.size() + 1];
  char *p;

  strcpy(msgptr, message.c_str());

  p = strtok (msgptr," ");

  while (p != NULL){
    cout << "KEYWORD: " << p << endl;
    keyword.append(p);

    cout << "Processing Arguments..." << endl;

    p = strtok(NULL," ");

    if( p != NULL){
      if(strstr(p, ",")){
	  cout << "ARGUEMENTS: " << p << endl;

	  argptr = p;

	  while( *argptr != ' ' && *argptr != '\0'){

	    if(*argptr == ','){
	      cout << "\tPUSHED Argument: " << argument << endl;
	      parameters.push_back(argument);
	      argument.erase();
	    }else{
	      argument.push_back(*argptr);
	    }

	    ++argptr;
	    if(*argptr == '\0'){
	      cout << "\tPUSHED Argument: " << argument << endl;
	      parameters.push_back(argument);
	    }
	  }
	      

      }else{
	cout << "ARGUMENT: " << p << endl;
	argument.erase();
	argument.append(p);
        cout << "\tPUSHED Argument: " << argument << endl;
        parameters.push_back(argument);
      }
    }

    // Stuff DataStructure and goto next block of tokens
    robot_rule[keyword] = parameters;

    keyword.erase();
    parameters.clear();
	
    p = strtok(NULL," ");
  }

  delete[] msgptr;

  cout << endl << endl;

  //enforce(robot_rule);

}

void Warden::enforce(map< string, vector<string> > robot_rule){

  map< string ,vector<string> >::iterator map_it;
  vector<string>::iterator vector_it;
  map<string ,robot_rules>::iterator robo_it;

  robot_rules rule;

  vector<string> id;

  for( map_it = robot_rule.begin(); map_it != robot_rule.end(); ++map_it){

    if(map_it->first.compare("FROM") == 0){
      continue;
    }

    cout << map_it->first << endl;

    for(vector_it = (map_it)->second.begin(); vector_it != (map_it)->second.end(); ++vector_it){

      //rule.

        robots[map_it->first] =  rule;
	// robots[111111111] = rule;

	cout << "\t -" << *vector_it << endl;
    }
  }
}

Warden::~Warden(){

}
