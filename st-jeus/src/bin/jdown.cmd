@echo off

call jeus50.properties.bat

%JEUS_HOME%/bin/jeusadmin %JEUS_NODE% -U%JEUS_ADMIN% -P%JEUS_PASSWORD% jeusexit