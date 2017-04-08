package server.protocol;

import server.MNetwork;
import server.thread.Worker;

public class Restore extends Worker {

    public Restore(MNetwork n, Protocol p){
        super(n, p);
    }

    public void start(){
        System.out.println("SenderID: " + protocol.getSenderId());
        System.out.println("My ID: " + network.peerID);
        if (protocol.getSenderId() != network.peerID){
            thread.start();
        }
        else {
            System.out.println("Ignoring RESTORE");
        }
    }

    public void run(){
        //TODO: delete chunk from filesystem
    }
}
