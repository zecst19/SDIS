package server.protocol;

import server.MNetwork;
import server.thread.Worker;

public class Putchunk extends Worker {


    public Putchunk(MNetwork n, Protocol p){
        super(n, p);
    }

    public void start(){
        System.out.println("SenderID: " + protocol.getSenderId());
        System.out.println("My ID: " + network.peerID);
        if (protocol.getSenderId() != network.peerID){
            //TODO: check if chunk is already stored as well
            thread.start();
        }
        else {
            System.out.println("Ignoring PUTCHUNK");
        }
    }

    public void run(){
        //TODO: handle request (copy chunk into filesystem)
        System.out.println("Handling PUTCHUNK");

        //reply with STORED
        Protocol stored = new Protocol();
        stored.setNetwork(network);
        stored.setMessageType(stored.STORED);
        stored.setVersion(network.VERSION);
        stored.setSenderId(network.peerID);
        stored.setFileId(protocol.getFileId());
        stored.setChunkNo(protocol.getChunkNo());

        try {
            Thread.sleep(this.randomDelay());
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }

        stored.sendMessage(network.MC);
    }
}
