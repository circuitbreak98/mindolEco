@ECHO OFF
ECHO UnInstall MiPlatform320 Unicode

REM System32 Folder Setting 
IF EXIST %SYSTEMROOT%\System\msiexec.exe (
	SET MSIEXEC=%SYSTEMROOT%\System\msiexec.exe
) ELSE IF EXIST %SYSTEMROOT%\System32\msiexec.exe (
	SET MSIEXEC=%SYSTEMROOT%\System32\msiexec.exe
) ELSE (
	ECHO NOT FOUND
	EXIT 1
)
REM SET SYSDIR=C:\WINDOWS\system32

REM   - MiPlatform_InstallEngine320U
ECHO UnInstall..  MiPlatform InstallEngine320U
pause
%MSIEXEC% /x {65673658-248C-49AC-9EC4-25682074A312}

REM   - MiPlatform_InstallBase320  
ECHO UnInstall..  MiPlatform InstallBase320
pause
%MSIEXEC% /x {3964575C-D828-4587-AED1-E538EAAFC083}

REM   - Updater
ECHO UnInstall..  MiPlatform Updater320
pause
%MSIEXEC% /x {5A5AB07F-2E03-405D-8DAF-1DB38D1DE14A}

ECHO End...
pause