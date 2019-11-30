@echo off

rem *************************************************************************
rem  This script is for conveniently running the CC PDF reader
rem *************************************************************************

rem SET THE FOLLOWING PATHS

set JAVA_HOME=C:\installs\java\jdk1.8.0_181
rem set CLASSPATH=./lib/*.jar
set CLASSPATH=.\lib\*;.\resources

rem  Run tool
%JAVA_HOME%\bin\java dr.reader.Runner