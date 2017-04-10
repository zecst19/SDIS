#!/bin/bash

cd src

if [ $# -eq 2 ]; then
	java client.TestApp $1:9636 $2
elif [ $# -eq 3 ]; then
	java client.TestApp $1:9636 $2 $3
elif [ $# -eq 4 ]; then
	java client.TestApp $1:9636 $2 $3 $4
fi
