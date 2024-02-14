#! /bin/bash

gradle build

clear

java -cp ./build/classes/java/main/ csx55.overlay.node.Registry $1 
