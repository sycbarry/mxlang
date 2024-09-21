#!/bin/bash

rm -rf com/sms/lang/*.class
rm -rf com/sms/lang/modules/*.class
rm -rf com/sms/lang/ui/*.class

javac -cp ".:gson-2.10.2-SNAPSHOT.jar:fontbox-3.0.0.jar:pdfbox-app-3.0.0.jar" \
    -d bin \
    com/sms/lang/*.java \
    com/sms/lang/modules/*.java \
    com/sms/lang/ui/*.java

# copy over all mx modules developed locally into final binary folder.
cp -r devmodules/* bin/com/sms/lang/modules/

java -cp "bin:gson-2.10.2-SNAPSHOT.jar:pdfbox-app-3.0.0.jar:fontbox-3.0.0.jar" com.sms.lang.ui.TextEditorWithRunButton
