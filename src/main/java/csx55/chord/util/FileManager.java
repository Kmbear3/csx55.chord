package csx55.chord.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import csx55.chord.wireformats.ForwardFile;


public class FileManager {

    String storeagePath = "/tmp/";

    public FileManager(int peerID){
        try {
            storeagePath += peerID + "/";
            // Create directory 
            Path path = Paths.get(storeagePath);
            Files.createDirectory(path);

        } catch (IOException e) {
           System.err.println("Failed to create directory!" + e.getMessage());
        }
    }

    public byte[] readFromDisk(String filepath){
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filepath));
            return bytes;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public void writeToDisk(String outputFilePath, byte[] bytes){

        File outputFile = new File(outputFilePath);
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
        }
    }


    public void readStoreFile(String inputPathName) {
        byte[] fileBytes = readFromDisk(inputPathName);
        
        String[] filePath = inputPathName.split("/");
        String fileName = filePath[filePath.length - 1];

        String fullStoragePath = this.storeagePath + fileName;
        writeToDisk(fullStoragePath, fileBytes);
    }

    public void readFowardFile(String inputPathName, PeerEntry peer) {
        byte[] fileBytes = readFromDisk(inputPathName);
        
        String[] filePath = inputPathName.split("/");
        String fileName = filePath[filePath.length - 1];

        ForwardFile file = new ForwardFile(fileName, fileBytes);

        try {
            peer.sendMessage(file.getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void printFiles() {
        File parentDirectory = new File(storeagePath);
        File[] files = parentDirectory.listFiles();

        for(File file : files){
            System.out.println(file.getName() + " " + file.getName().hashCode());
        }
    }

    public void receivedFile(ForwardFile forwardFile, FingerTable fingerTable) {
        String filename = forwardFile.getFilename();

        PeerEntry peer = fingerTable.lookup(filename.hashCode());

        if(peer.equals(fingerTable.me)){
            writeToDisk(storeagePath + filename, forwardFile.getFile());
        }
        else{
            try {
                peer.sendMessage(forwardFile.getBytes());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
    }
    
}



