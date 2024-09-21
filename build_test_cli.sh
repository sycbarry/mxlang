#!/bin/bash


rm -rf src/main/java/lang/*.class
rm -rf src/main/java/lang/modules/*.class
rm -rf src/main/java/lang/ui/*.class

# cp all jar files
javac -cp ".:lib/gson-2.10.2-SNAPSHOT.jar:lib/fontbox-3.0.0.jar:lib/pdfbox-app-3.0.0.jar:lib/junit-4.13.2.jar:lib/kuberentes-0.1.0-jar-with-dependencies.jar" \
    -d bin \
    src/main/java/lang/*.java \
    src/main/java/lang/packages/utils/*.java \
    src/main/java/lang/packages/mas/*.java \
    src/main/java/lang/packages/report/*.java \
    src/main/java/lang/packages/mxtest/*.java \
    src/main/java/lang/modules/*.java \
    src/main/java/lang/ui/*.java

cp ./fonts/* bin/lang/packages/report

# copy over all mx modules developed locally into final binary folder.
# mkdir bin/mxmodules/ 2> /dev/null
# cp -r devmodules/* bin/mxmodules

java -cp "bin:lib/gson-2.10.2-SNAPSHOT.jar:lib/pdfbox-app-3.0.0.jar:lib/fontbox-3.0.0.jar:lib/junit-platform-console-standalone-1.9.3.jar:lib/kuberentes-0.1.0-jar-with-dependencies.jar" lang.Lang test.mx
# ./clean.sh
