#include <iostream>
#include <map>
#include <vector>
#include <sstream>

#include "pluginmanager.h"

using namespace::std;

PluginManager *PluginManager::Instance(){
  static PluginManager instance;
  
  return &instance;
}

void PluginManager::report_errors(lua_State *L, int status)
{
    if ( status!=0 ) {
	std::cerr << "-- " << lua_tostring(L, -1) << std::endl;
	lua_pop(L, 1); // remove error message
    }
}

string PluginManager::load(string filename){
  lua_State *pL = lua_open();                                                   
  luaL_openlibs(pL);
  string keyword, value;
  int s = luaL_dofile(pL, filename.c_str());

//  cout << "In loader: " << filename << endl;

  if( s != 0){
    //report_errors(pL, s);
    return("Failed to load:" + filename);
  }

    lua_settop(pL, 0);

    lua_getglobal(pL, "usage");

    if(!lua_istable(pL, -1)){
      return("Error: Plugin " + filename + " has not defined usage");
    }

    if(!plugins[filename]){
	plugins[filename] = pL;
    }else{
      return 0;
    }

    //cout << "Iterating through usage keywords:" << endl;

    lua_pushnil(pL);

    while(lua_next(pL, -2)){
      if(lua_isstring(pL, -1)){
	keyword = lua_tostring(pL, -2);
	value = lua_tostring(pL, -1);
	
//	cout << keyword << ":" << value << endl;
	commands[keyword] = filename;

      }else{
//	cout << "Not string" << endl;
      }

      lua_pop(pL, 1);
      
    }

    lua_pop(pL, 1);

    lua_getglobal(pL, "hooks");

    if(!lua_istable(pL, -1)){
      return("Error: Plugin has not defined hooks");
    }

    lua_pushnil(pL);

//    cout << "Processing Hooks" << endl << endl;

    while(lua_next(pL, -2)){
      if(lua_isstring(pL, -2)){
	keyword = lua_tostring(pL, -2);

	if(lua_toboolean(pL, -1)){
//	  cout << "For hook keyword: " << keyword << " I got a true" << endl;
	    hooks[keyword].push_back(filename);
	}else{
//	  cout << "For hook keyword: " << keyword << " I got a false" << endl;
	}

	
//	cout << keyword << ":" << value << endl;
	commands[keyword] = filename;

      }else{
//	cout << "Not string" << endl;
      }

      lua_pop(pL, 1);
      
    }

    lua_pop(pL, 1);


  return("Plugin " + filename + " was loaded successfully.");

}


string PluginManager::reload(string filename){
  unload(filename);
  load(filename);

  // check if either failed
  return(filename + " was reloaded.");
}

string PluginManager::unload(string filename){

  // remember to detatch hooks

  map< string ,string >::iterator it;
  bool found = false;

  for( it = commands.begin(); it != commands.end(); ++it){
    if(it->second == "filename"){
      commands.erase(it);

      if(!found)
	found = true;
    }
  }

  if(found){
    lua_close(plugins[ filename ]);
    plugins.erase(filename);
  }else{
    //return("Failed to unload: " + filename);
  }

  return(filename + " was unloaded.");
}

string PluginManager::execute(string cmd){

  map< string ,string >::iterator it;
  string command, tmp;
  stringstream cmdstream(cmd);
  lua_State *pL;

  cmdstream >> command;

  //cout << "In executer and parsed out: " << command << endl;

  //for( it = commands.begin(); it != commands.end(); ++it){
   // cout << it->first << " -> " << it->second << endl;
  //}

  //cout << command << " - Calling lua file: " << commands[command] << endl;

  pL = plugins[ commands[command] ];

  if(pL == NULL){
    return command + ": command not found.";
  }

  lua_getglobal(pL, "process");

  if(!lua_isfunction(pL, -1)){
    return "Error: Plugin " + commands[command] + "has not defined function process";
  }

  lua_pushstring(pL, cmd.c_str());
  lua_call(pL, 1, 1);

  if(lua_isstring(pL, 1)){
    tmp = lua_tostring(pL, 1);
    lua_pop(pL, 1);

    return tmp;
  }else{
    lua_pop(pL, 1);

    return "Error: Plugin " + commands[command] + " returned something other than string\
	from function process";
  }
    
}

int PluginManager::incoming(string data){
  vector<string>::iterator filename_it;
  map<string, vector<string> >::iterator hooks_it;
  lua_State *pL;
  string tmp;

  for( hooks_it = hooks.begin(); hooks_it != hooks.end(); ++hooks_it){
    cout << "For " << hooks_it->first << endl;

    for( filename_it = hooks_it->second.begin();
	    filename_it != hooks_it->second.end(); ++filename_it){
      cout << "Calling incoming on: " << *filename_it
	  << " with data: " << data << endl;

      pL = plugins[*filename_it];

      lua_getglobal(pL, "incoming");

      if(!lua_isfunction(pL, -1)){
	cout << "Error: Plugin " + *filename_it + "has not defined function incoming";
	return 1;
      }

      lua_pushstring(pL, data.c_str());
      lua_call(pL, 1, 1);

      if(lua_isstring(pL, 1)){
	tmp = lua_tostring(pL, 1);
	lua_pop(pL, 1);
	cout << tmp << endl;
      }else{
	cout << "Error in incoming" << endl;
	return 1;
      }

    }

  }

  return 0;
}


int PluginManager::outgoing(string data){
  vector<string>::iterator filename_it;
  map<string, vector<string> >::iterator hooks_it;
  lua_State *pL;

  for( hooks_it = hooks.begin(); hooks_it != hooks.end(); ++hooks_it){
    cout << "For " << hooks_it->first << endl;


    for( filename_it = hooks_it->second.begin();
	    filename_it != hooks_it->second.end(); ++filename_it){
      cout << "Calling outgoing on: " << *filename_it
	  << " with data: " << data << endl;

      pL = plugins[*filename_it];

    }

  }
  
  return 0;
}

bool PluginManager::hooked(string type){

  if(!hooks[type].empty()){
    return true;
  }else{
    return false;
  }

}

PluginManager::~PluginManager(){
  map<string, lua_State *>::iterator plugin_it;
  map<string, string>::iterator commands_it;

  for( commands_it = commands.begin(); commands_it != commands.end(); ++commands_it){
      commands.erase(commands_it);
  }

  for( plugin_it = plugins.begin(); plugin_it != plugins.end(); ++plugin_it){
    if(plugins[ plugin_it->first ] != NULL){
	lua_close(plugins[ plugin_it->first ]);
	plugins.erase(plugin_it);
    }
  }

}
