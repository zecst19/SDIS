package server.protocol;

import server.MNetwork;
import server.thread.Worker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Getchunk extends Worker {
    public static BlockingQueue<Protocol> chunkQueue;

    public Getchunk(MNetwork n, Protocol p){
        super(n, p);
        chunkQueue = new LinkedBlockingQueue<>();
    }

    public void start(){
        System.out.println("SenderID: " + protocol.getSenderId());
        System.out.println("My ID: " + network.peerID);
        //TODO: and if this peer has copy of requested chunk
        if (protocol.getSenderId() != network.peerID /*  */){
            thread.start();
        }
        else {
            System.out.println("Ignoring GETCHUNK");
        }
    }

    public void run(){
        //TODO: handle request (retrieve chunk from filesystem)
        System.out.println("Handling GETCHUNK");

        //reply with CHUNK if no CHUNK received
        try {
            Thread.sleep(this.randomDelay());
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }

        System.out.println("Finished Waiting");
        if (chunkQueue.isEmpty()){
            Protocol chunk = new Protocol();
            chunk.setNetwork(network);
            chunk.setMessageType(chunk.CHUNK);
            chunk.setVersion(network.VERSION);
            chunk.setSenderId(network.peerID);
            chunk.setFileId(protocol.getFileId());
            chunk.setChunkNo(protocol.getChunkNo());
            chunk.setBody(protocol.getBody());


            chunk.sendMessage(network.MDR);
        }
    }
}
