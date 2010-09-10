CPP 		= g++
PROJECT_LIB	= ../../lib/
ARCHIVER 	= ar

OUTPUT 		= libPathPlanner.a
SOURCE_DIR 	= src
INCLUDE_DIR 	= include
LIB_DIR 	= lib

#dependency: localization
LOCALIZE_DIR 	= ../Localization
LOCALIZE_INC	= $(LOCALIZE_DIR)/include
LOCALIZE_LIB 	= $(PROJECT_LIB)

# Flags passed to the preprocessor.
CPPFLAGS 	= -I$(INCLUDE_DIR) -I$(LOCALIZE_INC)

# Flags passed to the C++ compiler.
CXXFLAGS 	= -g -c #-Wall    
#LIBS		= -L$(LOCALIZE_LIB) -llocalization

# Source files to compile with relative path
#SOURCES = $(shell ls src/*cpp)
SOURCE_PATHS 	= $(shell find src -name "*cpp")
SOURCES 	= $(foreach source, $(SOURCE_PATHS), $(subst src/, , $(source) ))
SOURCE_DIRS 	= $(dir $(SOURCE_PATHS))

# Object files to link with no path
OBJECTS 	= $(notdir $(SOURCES:.cpp=.o))

# House-keeping build targets.
all : $(OUTPUT) deploy

purge : clean 
	rm -f *.a

clean :
	rm -f *.o

VPATH 		= $(SOURCE_DIR) $(TEST_DIR) $(SOURCE_DIRS)

# compile source files
%.o : $(SOURCE_DIR)/%.cpp
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) $^

#create library (this should only be run with the tests...)
$(OUTPUT) : $(OBJECTS)
	$(ARCHIVER) -rc $@ $(OBJECTS)

#creates the library and copies it to the libs folder of the project
deploy: $(OUTPUT)
	mv $? $(PROJECT_LIB)
