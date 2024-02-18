#! /bin/bash

gradle build

clear

java -cp ./build/classes/java/main/ csx55.threads.node.Registry $1 
