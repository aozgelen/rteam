CPP 		= g++
PROJECT_LIB	= ../lib/
ARCHIVER 	= ar

OUTPUT 		= libMetrobotics.a
SOURCE_DIR 	= src
INCLUDE_DIR 	= include
LIB_DIR 	= lib

TIMER		= Timer
COMM		= Communication
MATH		= Math

# Flags passed to the preprocessor.
CPPFLAGS 	= -I$(INCLUDE_DIR)/$(TIMER) -I$(INCLUDE_DIR)/$(COMM) -I$(INCLUDE_DIR)/$(MATH) 

# Flags passed to the C++ compiler.
CXXFLAGS 	= -g -c #-Wall

# Source files to compile with relative path
SOURCE_PATHS 	= $(shell find src -name "*cpp")
SRCS 		:= $(foreach source, $(SOURCE_PATHS), $(subst src/, , $(source) ))
SOURCES 	:= $(foreach src, $(SRCS), $(subst Communication/, , $(src) ))
SOURCES 	:= $(foreach src, $(SOURCES), $(subst Timer/, , $(src) ))
SOURCES 	:= $(foreach src, $(SOURCES), $(subst Math/, , $(src) ))
SOURCE_DIRS 	= $(dir $(SOURCE_PATHS))

# Object files to link with no path
OBJECTS 	= $(notdir $(SOURCES:.cpp=.o))

# House-keeping build targets.
all : $(OUTPUT) deploy

purge : clean 
	rm -f *.a

clean :
	rm -f *.o

VPATH 		= $(SOURCE_DIR) $(SOURCE_DIRS)

# compile source files
%.o : %.cpp
	$(CPP) $(CPPFLAGS) $(CXXFLAGS) $^

#create library (this should only be run with the tests...)
$(OUTPUT) : $(OBJECTS)
	$(ARCHIVER) -rc $@ $(OBJECTS)

#creates the library and copies it to the libs folder of the project
deploy: $(OUTPUT)
	mv $? $(PROJECT_LIB)

