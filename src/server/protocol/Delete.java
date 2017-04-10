package server.protocol;

import server.Log;
import server.MNetwork;
import server.Server;
import server.thread.Worker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static server.Server.homedir;

public class Delete extends Worker {

    public Delete(MNetwork n, Protocol p){
        super(n, p);
    }

    public void start(){
        System.out.println("SenderID: " + protocol.getSenderId());
        System.out.println("My ID: " + network.peerID);
        if (protocol.getSenderId() != network.peerID){
            thread.start();
        }
        else {
            System.out.println("Ignoring DELETE");
        }
    }

    public void run(){
        //TODO: test this

        for (int i = 0; i < Log.bLogs.size(); i++){
            if (Log.bLogs.get(i).getFileId().equals(protocol.getFileId())){
                Path file = Paths.get(Server.homedir + "backupfiles/" + protocol.getFileId() + "-" + Log.bLogs.get(i).getChunkNo());
                System.out.println(Server.homedir + "backupfiles/" + protocol.getFileId() + "-" + Log.bLogs.get(i).getChunkNo());
                try {
                    Files.deleteIfExists(file);
                    System.out.println("Deleted File Chunk");
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
