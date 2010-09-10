# Toolchain/Environment
export SHELL 	= /bin/bash
export CPP    	= g++

LIB_DIR 	= ../lib

# Dependency: MetroUtil
METROUTIL     	= $(LIB_DIR)/libMetrobotics.a
METROUTIL_DIR	= ./MetroUtil
METROUTIL_LIB 	= $(LIB_DIR)
METROUTIL_INC 	= $(METROUTIL_DIR)/include

#Dependency: Localization
LOCALIZE	= $(LIB_DIR)/libLocalization.a
LOCALIZE_DIR	= ./Localization
LOCALIZE_LIB	= $(LIB_DIR)
LOCALIZE_INC	= $(LOCALIZE_DIR)/include

#Dependency: PathPlanner
PATHPLAN	= $(LIB_DIR)/libPathPlanner.a
PATHPLAN_DIR	= ./PathPlanner
PATHPLAN_LIB	= $(LIB_DIR)
PATHPLAN_INC	= $(PATHPLAN_DIR)/include

INCLUDE_DIR 	= include
SRC_DIR		= src

VPATH 		= $(SRC_DIR) 

# Options
INC_FLAGS	= -I$(INCLUDE_DIR) -I$(METROUTIL_INC) -I$(LOCALIZE_INC) -I$(PATHPLAN_INC)
CPPFLAGS 	= $(INC_FLAGS) `pkg-config --cflags playerc++`
CXXFLAGS	= -c -g #-Wall
LIBS   		= -L$(METROUTIL_LIB) -lMetrobotics -L$(LOCALIZE_LIB) -lLocalization -L$(PATHPLAN_LIB) -lPathPlanner -lboost_system-mt -lglut -lGLU `pkg-config --libs playerc++`

# Files
OUTPUT    	= controller
SOURCES   	= $(wildcard *.cpp)
OBJECTS   	= $(patsubst %.cpp, %.o, $(SOURCES))

# General targets
.PHONY: $(OUTPUT) clean purge

# build metroutil, localization, pathplanner and the controller
$(OUTPUT): $(METROUTIL) $(LOCALIZE) $(PATHPLAN) $(OBJECTS)
	$(CPP) -o $@ $(OBJECTS) $(LIBS)

# Dependency: MetroUtil
$(METROUTIL):
	@echo Building MetroUtil
	@$(MAKE) -e --directory=$(METROUTIL_DIR)
	@if [ "$$?" != 0 ]; then \
		echo "MetroUtil failed to build"; \
		exit 1; \
	fi
	@echo Deploying library
	@$(MAKE) -e --directory=$(METROUTIL_DIR) deploy

# Dependence: Localization
$(LOCALIZE): 
	@echo Building Localization library
	@$(MAKE) --directory=$(LOCALIZE_DIR)
	@if [ "$$?" != 0 ]; then \
		echo "Localization failed to build"; \
		exit 1; \
	fi
	@echo Deploying library
	@$(MAKE) --directory=$(LOCALIZE_DIR) deploy

# Dependence: PathPlanner
$(PATHPLAN): 
	@echo Building PathPlanner library
	@$(MAKE) --directory=$(PATHPLAN_DIR)
	@if [ "$$?" != 0 ]; then \
		echo "PathPlanner failed to build"; \
		exit 1; \
	fi
	@echo Deploying library
	@$(MAKE) --directory=$(PATHPLAN_DIR) deploy

# Remove unnecessary output files
clean:
	@$(MAKE) -e --directory=$(METROUTIL_DIR) clean
	@$(MAKE) --directory=$(LOCALIZE_DIR) clean
	rm -rf $(OUTPUT)
	rm -rf $(OBJECTS)

# Remove all output files
purge:
	@$(MAKE) -e --directory=$(METROUTIL_DIR) purge
	@$(MAKE) --directory=$(LOCALIZE_DIR) clean
	@$(MAKE) -e clean
	rm -rf $(OUTPUT)
	rm -rf $(LIB_DIR)/*

