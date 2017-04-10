#!/bin/bash

cd src

java server.Server 1.0 $1 $2:9636 228.0.0.0 4678 228.1.1.1 3215 228.2.2.2 9876
