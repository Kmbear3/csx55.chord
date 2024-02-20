#! /bin/bash

# 1: Servername to connect to
# 2: Serverport to connect to

source ~/.bashrc && module purge && module load courses/cs455

clear 

java -cp ./build/classes/java/main/ csx55.threads.node.ComputeNode $1 $2