# Minimal Makefile calling Apache Maven

.PHONY: all install clean

all:
	mvn install

install: all
	@echo ""

clean:
	mvn clean
