#! /bin/bash

gradle build

clear

java -cp ./app/build/libs/app.jar csx55.overlay.node.Registry $1 
