package server.protocol;

import server.MNetwork;
import server.thread.Listener;
import server.thread.RequestWorker;
import server.thread.Worker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Chunk extends Worker {

    public Chunk(MNetwork n, Protocol p){
        super(n, p);
    }

    public void start(){
        System.out.println("SenderID: " + protocol.getSenderId());
        System.out.println("My ID: " + network.peerID);
        if (protocol.getSenderId() != network.peerID){
            thread.start();
        }
        else {
            System.out.println("Ignoring CHUNK");
        }
    }

    public void run(){
        System.out.println("Handling CHUNK");
        try {/*
            Getchunk.chunkQueue.put(protocol);
            System.out.println("Sending CHUNK back to Getchunk to let him know he's not the first");
*/
            //and
            RequestWorker.responseQueue.put(protocol);
            System.out.println("Sending CHUNK to ResponseQueue");
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
