#!/bin/bash

rm -rf target/
rm -rf bin/

javac -cp ".:lib/gson-2.10.2-SNAPSHOT.jar:lib/fontbox-3.0.0.jar:lib/pdfbox-app-3.0.0.jar:lib/junit-platform-console-standalone-1.10.1.jar:/lib/kubernetes-client-6.10.0.jar" \
    -d bin \
        src/main/java/lang/*.java \
        src/main/java/lang/packages/utils/*.java \
        src/main/java/lang/packages/report/*.java \
        src/main/java/lang/packages/mxtest/*.java \
        src/main/java/lang/modules/*.java \
        src/main/java/lang/ui/*.java
        #src/main/java/lang/packages/mas/*.java \


# copy over all mx modules developed locally into final binary folder.
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
cat example > target/test.mx


#jar -cf \
#    mxlang.jar \
#    fonts \
#    com/sms/lang/*.java \
#    com/sms/lang/packages/utils/*.java \
#    com/sms/lang/modules/*.java \
#    com/sms/lang/packages/mxtest/*.java \
#    com/sms/lang/ui/*.java \
#    gson-2.10.2-SNAPSHOT.jar \
#    pdfbox-app-3.0.0.jar \
#    fontbox-3.0.0.jar
#java -cp "bin:gson-2.10.2-SNAPSHOT.jar:pdfbox-app-3.0.0.jar:fontbox-3.0.0.jar" com.sms.lang.Lang test.mx
# ./clean.sh
