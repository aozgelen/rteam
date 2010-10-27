#ifndef _WARDEN_H_
#define _WARDEN_H_

#include <string>
#include <vector>
#include <map>

using std::string;
using std::vector;
using std::map;

class Warden {
    public:
	static Warden *Instance();
	string eval(string msg);
	string load(string file);
	string unload(string file);
	string reload(string file);
	string help(string file);
	void quit();
	~Warden();
    private:
	Warden(){};
	Warden(const Warden &);
	Warden& operator=(const Warden &);

	string format_message(string str);
	string parse_message(string str);
	void insert_rule(){};

	string message;
};

#endif
