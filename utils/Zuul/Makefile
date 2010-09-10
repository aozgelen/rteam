CPP           = g++
CPPFLAGS      = -c -g
LINK          = g++
LDFLAGS_LINUX = -lpthread
LDFLAGS       = $(LDFLAGS_LINUX)
RM            = rm


.SUFFIXES:
.SUFFIXES: .o .cpp

.cpp.o:
	$(CPP) $(CPPFLAGS) $*.cpp -o $*.o

all: server

server: server.o commServer.o pose.o robot.o
	$(LINK) $(LDFLAGS) -o $@ $^

clean:
	$(RM) -rf *.o server

server.o:	server.cpp state.h cmd.h commServer.h pose.h robot.h
commServer.o:	commServer.cpp commServer.h
pose.o:		pose.cpp pose.h
robot.o:	robot.cpp robot.h pose.h
