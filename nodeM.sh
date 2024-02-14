#! /bin/bash

# 1: Servername to connect to
# 2: Serverport to connect to

source ~/.bashrc && module purge && module load courses/cs455

clear 

java -cp ./app/build/libs/app.jar csx55.overlay.node.MessagingNode $1 $2