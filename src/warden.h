#ifndef _WARDEN_H_
#define _WARDEN_H_

#include <string>
#include <vector>
#include <map>

using std::string;
using std::vector;
using std::map;

struct robot_rules {
  vector<string> arguments;
  int delay_seconds;
  bool random;
};

class Warden {
    public:
	static Warden *Instance();
	string eval(string msg);
	~Warden();
    private:
	Warden(){};
	Warden(const Warden &);
	Warden& operator=(const Warden &);

	string format_message(string str);
	void tokenize_message(string str);
	void insert_rule(){};
	void enforce(map< string, vector<string> >);

	string message;
	map< string, robot_rules > robots;
};

#endif
