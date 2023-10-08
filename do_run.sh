#! /bin/bash

java -Djava.library.path="." com.zoffcc.applications.trifa.MainActivity "$1" 2>&1 | tee trifa.log
