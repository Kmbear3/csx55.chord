package csx55.chord.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import csx55.chord.wireformats.DownloadRequest;
import csx55.chord.wireformats.DownloadResponse;
import csx55.chord.wireformats.ForwardFile;
import csx55.chord.wireformats.MigrateFile;


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

    public static byte[] readFromDisk(String filepath){
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filepath));
            return bytes;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void writeToDisk(String outputFilePath, byte[] bytes){

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

    public void downloadFile(String filename, FingerTable fingerTable) {
        PeerEntry peer = fingerTable.lookup(filename.hashCode());

        if(peer.equals(fingerTable.me)){
            byte[] file = readFromDisk(storeagePath+filename);
            String userDirectory = System.getProperty("user.dir");
            writeToDisk(userDirectory+filename, file);
        }
        else{
            fowardDownloadRequest(filename, fingerTable.me, peer);
        }
    }

    private void fowardDownloadRequest(String filename, PeerEntry me, PeerEntry peer) {
        DownloadRequest download = new DownloadRequest(filename, me);
        try{
            peer.sendMessage(download.getBytes());
        }
        catch(IOException e){
            System.err.println("Error with forwarding download request " + e.getMessage());
        }
    }

    public void receiveDownloadRequest(DownloadRequest downloadRequest, FingerTable fingerTable) {

        PeerEntry peer = fingerTable.lookup(downloadRequest.getFilename().hashCode());
        ArrayList<PeerEntry> hops = downloadRequest.getHops();
        hops.add(fingerTable.me);

        try{ 
            //If I'm the successor, store the file
            if(peer.equals(fingerTable.me)){
                String filePath = storeagePath+downloadRequest.getFilename();
                File f = new File(filePath);
                if(f.exists() && !f.isDirectory()) { 
                    boolean fileFound = true;
                    byte[] file = readFromDisk(filePath);
                    DownloadResponse response = new DownloadResponse(downloadRequest.getFilename(), hops, fileFound, file);
                    hops.get(0).sendMessage(response.getBytes());

                } else{
                    boolean fileFound = false;
                    DownloadResponse response = new DownloadResponse(downloadRequest.getFilename(), hops, fileFound, null);
                    hops.get(0).sendMessage(response.getBytes());
                }
            }else{
                // Forward the file to the next peer in table
                DownloadRequest forwardRequest = new DownloadRequest(downloadRequest.getFilename(), hops);
                peer.sendMessage(forwardRequest.getBytes());
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
    }

    public void receiveDownload(DownloadResponse downloadResponse, FingerTable fingerTable) {
        if(downloadResponse.getFileStatus()){
            String filename = downloadResponse.getFilename();
            byte[] file = downloadResponse.getFile();

            String userDirectory = System.getProperty("user.dir");
            // System.out.println("Received download: " + userDirectory);
            // System.out.println("Bytes: " + file);

            writeToDisk(userDirectory+"/"+filename, file);

            // PRint all hops
            ArrayList<PeerEntry> hops = downloadResponse.getHops();
            for(int i = 0; i < hops.size(); i ++){
                System.out.println(hops.get(i).peerID);
            }

        }else{
            // Need to test this! 
            System.out.println("ERROR: Unable to locate file: " + downloadResponse.getFilename());
        }
    }

    public void receiveMigratedFile(MigrateFile migrateFile, FingerTable fingerTable) {

        // Should probably check to see if I'm the one to store the file
        writeToDisk(this.storeagePath+migrateFile.getFileName(), migrateFile.getFile());
    }

    public void migrateFiles(FingerTable fingerTable) {
        try{
            File parentDirectory = new File(storeagePath);
            File[] files = parentDirectory.listFiles();

            for(File file : files){
                byte[] fileBytes = readFromDisk(this.storeagePath+file.getName());
                MigrateFile migratingFile = new MigrateFile(file.getName(), fileBytes);
                fingerTable.succ.sendMessage(migratingFile.getBytes());
            }
        }catch(IOException e){
            System.err.println("Error trying to migrate files: " + e.getMessage());
        }
    }
}



