#! /bin/bash

source ~/.bashrc && module purge && module load courses/cs455

gradle build

clear

java -cp ./app/build/libs/app.jar csx55.overlay.node.Registry $1 
