#! /bin/bash

cd /D "%~dp0"
java.exe -Djava.library.path="." com.zoffcc.applications.trifa.MainActivity %* > a.txt 2>&1 trifa.log
