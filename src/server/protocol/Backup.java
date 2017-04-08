package server.protocol;

import server.MNetwork;
import server.thread.Worker;

public class Backup extends Worker {

    public Backup(MNetwork n, Protocol p){
        super(n, p);
    }

    public void start(){
        System.out.println("SenderID: " + protocol.getSenderId());
        System.out.println("My ID: " + network.peerID);
        if (protocol.getSenderId() != network.peerID){
            thread.start();
        }
        else {
            System.out.println("Ignoring BACKUP");
        }
    }

    public void run(){
        //TODO: delete chunk from filesystem
    }
}
