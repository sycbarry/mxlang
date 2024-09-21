assert = $(if $2,$(if $1,,$(error $2)))
all: check-variable test
JUnit.ver  = 1.10.1
JUnit.jar  = junit-platform-console-standalone-$(JUnit.ver).jar
Maven.http = https://mvnrepository.com/artifact/org.junit.platform/junit-platform-console-standalone
JUnit.mvn  = $(Maven.http)/$(JUnit.ver)/$(JUnit.jar)

check-variable:
	$(call assert,$(JAVA_HOME),JAVA_HOME is not defined)

clean:
	-rm -rf target/

dirs: clean
	-mkdir -p target
	-mkdir -p lib

compile-java: dirs
	$(JAVA_HOME)/bin/javac -cp ".:lib/gson-2.10.2-SNAPSHOT.jar:lib/fontbox-3.0.0.jar:lib/pdfbox-app-3.0.0.jar:lib/$(JUnit.jar):lib/kuberentes-0.1.0-jar-with-dependencies.jar" \
		-d bin \
			src/main/java/lang/*.java \
			src/main/java/lang/packages/utils/*.java \
			src/main/java/lang/packages/report/*.java \
			src/main/java/lang/packages/mxtest/*.java \
			src/main/java/lang/modules/*.java \
			src/main/java/lang/ui/*.java
			#src/main/java/lang/packages/mas/*.java \

junit-download:
	curl -s -z lib/$(JUnit.jar) \
          -o lib/$(JUnit.jar) \
          $(JUnit.mvn)

compile-test: compile-java #junit-download
	$(JAVA_HOME)/bin/javac \
		-d target/test-classes \
		-cp lib/$(JUnit.jar):lib/gson-2.10.2-SNAPSHOT.jar:lib/fontbox-3.0.0.jar:lib/pdfbox-app-3.0.0.jar:lib/kuberentes-0.1.0-jar-with-dependencies.jar:target/classes \
		src/test/java/*.java

test: compile-test
	$(JAVA_HOME)/bin/java -jar lib/$(JUnit.jar) -cp ".:lib/gson-2.10.2-SNAPSHOT.jar:lib/fontbox-3.0.0.jar:lib/pdfbox-app-3.0.0.jar:lib/kuberentes-0.1.0-jar-with-dependencies.jar" \
           --class-path target/classes:target/test-classes \
           --scan-class-path

clean:
	rm -rf target/
	rm -rf bin/



build: clean compile-java
	mkdir bin/com/sms/lang/mxmodules/ 2> /dev/null
	cp -r devmodules/* bin/com/sms/lang/mxmodules
	mkdir bin/com/sms/lang/static/fonts/
	cp -r fonts/* bin/com/sms/lang/static/fonts/
	mvn clean package
	mvn compile
	mvn package
	echo "#! /usr/bin/env java -jar" > target/mxlang
	cat target/mxlang-0.2.0.jar >> target/mxlang
	chmod +x target/mxlang
	cat example >> target/test.mx



#
