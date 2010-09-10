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

all: skygrid

skygrid: skygrid.o commServer.o pose.o robot.o
	$(LINK) $(LDFLAGS) -o $@ $^

clean:
	$(RM) -rf *.o skygrid

skygrid.o:	skygrid.cpp definitions.h commServer.h pose.h robot.h
commServer.o:	commServer.cpp commServer.h
pose.o:		pose.cpp pose.h
robot.o:	robot.cpp robot.h pose.h
