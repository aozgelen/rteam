CPP           = g++
CPPFLAGS      = -c -Wall -g
LINK          = g++
LDFLAGS_LINUX = -llua -lpthread -lncurses
LDFLAGS       = $(LDFLAGS_LINUX)
RM            = rm


.SUFFIXES:
.SUFFIXES: .o .cpp

.cpp.o:
	$(CPP) $(CPPFLAGS) $*.cpp -o $(SRC_DIR)$*.o

all: skygrid clone

skygrid: skygrid.o commServer.o pose.o robot.o render.o pluginmanager.o warden.o
	$(LINK) $(LDFLAGS) -o $@ $^

clone: clone.o commClient.o
	$(LINK) -o $@ $^

clean:
	$(RM) -rf *.o skygrid

skygrid.o:	skygrid.cpp definitions.h commServer.h pose.h robot.h render.h warden.h pluginmanager.h render.cpp
clone.o:	commClient.cpp commClient.h
commServer.o:	commServer.cpp commServer.h
pose.o:		pose.cpp pose.h
robot.o:	robot.cpp robot.h pose.h
pluginmanager.o:pluginmanager.cpp pluginmanager.h
warden.o:	pluginmanager.cpp warden.cpp warden.h
render.o:	warden.cpp render.cpp render.h
