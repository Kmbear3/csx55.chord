# Chord Peer to Peer Network
This project is an implementation of the Chord Peer to Peer Network. There are 2 main components in this project: Discovery Node and Peer Node. The Discover Node maintains information about the active participants in the P2P network. The Peer Nodes maintain Predecessor and Successor node information as well as a finger table for file routing purposes. The Peer Nodes act as clients, where any Peer is able to Read/ Write files to the system.

* execute all commands under csx55.chord/

## to clean 
gradle clean 

## to compile: 
gradle build

## How to run:
- java -cp ./app/build/libs/app.jar csx55.chord.Discovery <portnum>
- java -cp ./app/build/libs/app.jar csx55.chord.Peer <discover-ip> <discovery-portnum>


## Usage

Commands - Discovery Node:

- `peer-nodes` - Prints a list of peers in the system.

Peer Node:

- `neighbors` - displays predecessor and successor information. 
- `files` - shows files that the peer possesses 
- `finger-table` - shows the finger table 
- `upload <file-path>` - uploads a file into the P2P network. 
- `download <file name>` - downloads file 




