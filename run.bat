@echo off
mvn compile exec:java -Dexec.mainClass="de.csmp.jeiscp.app.ThreadedConsoleApp" -Dexec.args="%1 %2 %3 %4 %5"