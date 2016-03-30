@echo off

call jeus50.properties.bat

%JEUS_HOME%/bin/jeus -xml -U%JEUS_ADMIN% -P%JEUS_PASSWORD%