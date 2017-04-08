package server.protocol;

import server.MNetwork;
import server.Server;
import server.thread.Worker;

import java.io.File;
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
        Path file = Paths.get(homedir + "backupfiles/" + protocol.getFileId() + "-" + protocol.getChunkNo());
        file.toFile().delete();
    }
}
