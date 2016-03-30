@ECHO OFF
REM MSI Install Start
REM ******************************************************
REM * 0. MiPlatform_SetupUpdater320.exe Install          *
REM * 1. MiPlatform_InstallBase320 MSI Install. 	 *
REM * 2. MiPlatform_InstallEngine320U MSI Install.	 *
REM ******************************************************

SET PATH=%PATH%;%WINDIR%\System;%WINDIR%\System32

MiPlatform_SetupUpdater320.exe /qb

MSIEXEC.exe /i "MiPlatform_InstallBase320.msi"  /qb

ECHO Install base 설치 완료후....
pause

MSIEXEC.exe /i "ChartFX5040.msi"  /qb

MSIEXEC.exe /i "VsReport.msi"  /qb