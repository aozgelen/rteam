#ifndef _PLUGINMANAGER_
#define _PLUGINMANAGER_

#include <string>
#include <vector>
#include <map>

extern "C" {                                                                    
#include <lua.h>                                                                
#include <lualib.h>                                                             
#include <lauxlib.h>                                                            
}

using namespace::std;

class PluginManager {
    public:
	static PluginManager *Instance();
	string load(string filename);
	string unload(string filename);
	string reload(string filename);
	int incoming(string data);
	int outgoing(string data);
	bool hooked(string type);
	string execute(string command);
	void report_errors(lua_State *L, int status);
	~PluginManager();

    private:
	PluginManager(){};
	PluginManager(const PluginManager &);
	PluginManager& operator=(const PluginManager &);

	map<string, lua_State *>plugins;
	map<string, string>commands;
	map<string, vector<string> >hooks;


};

#endif
