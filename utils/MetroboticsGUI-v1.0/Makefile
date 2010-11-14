# Pablo Munoz - Metrobotics
# Java Compiler
JCC=javac
DESTDIR = ../
# Disable debugging information
JFLAGS = -g


default: metrobotics

metrobotics:
	cd src/ ;\
	$(JCC) $(JFLAGS) metrobotics/*.java ; \
	jar -cfe $(DESTDIR)metroboticsGUI.jar metrobotics.appMain *; \
	cd ../ ;\

clean: 
	cd src/ ;\
	$(RM) metrobotics/*class; \
	$(RM) -r javaclient/*class; \
	$(RM) $(DESTDIR)metroboticsGUI.jar; \
	cd ../ ;\



