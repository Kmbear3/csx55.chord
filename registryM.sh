#! /bin/bash

source ~/.bashrc && module purge && module load courses/cs455

gradle build

clear

java -cp ./build/classes/java/main/ csx55.chord.Discovery $1 
