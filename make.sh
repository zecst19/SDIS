#!/bin/bash

cd src

printf "client..."
javac client/*.java
printf "        DONE!\n"
printf "fileSystem..."
javac fileSystem/*.java
printf "    DONE!\n"
printf "server..."
javac server/*.java
javac server/thread/*.java
javac server/protocol/*.java
printf "        DONE!\n"
