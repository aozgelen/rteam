CPP           = g++
CPPFLAGS      = -c -Wall -g
LINK          = g++
LDFLAGS_LINUX = -lpthread -lncurses
LDFLAGS       = $(LDFLAGS_LINUX)
RM            = rm


.SUFFIXES:
.SUFFIXES: .o .cpp

.cpp.o:
	$(CPP) $(CPPFLAGS) $*.cpp -o $(SRC_DIR)$*.o

all: skygrid clone

skygrid: skygrid.o commServer.o pose.o robot.o render.o
	$(LINK) $(LDFLAGS) -o $@ $^

clone: clone.o commClient.o
	$(LINK) -o $@ $^

clean:
	$(RM) -rf *.o skygrid

skygrid.o:	skygrid.cpp definitions.h commServer.h pose.h robot.h render.h render.cpp
clone.o:	commClient.cpp commClient.h
commServer.o:	commServer.cpp commServer.h
pose.o:		pose.cpp pose.h
robot.o:	robot.cpp robot.h pose.h
render.o:	render.cpp render.h
