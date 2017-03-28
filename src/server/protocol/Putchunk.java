package server.protocol;

import server.MNetwork;
import server.thread.Worker;

public class Putchunk extends Worker {


    public Putchunk(MNetwork n, Protocol p){
        super(n, p);
    }

    public void start(){
        if (protocol.getSenderId() != network.peerID){
            //TODO: check if chunk is already stored as well
            thread.start();
        }
    }

    public void run(){
        //TODO: handle request (copy chunk into filesystem)


        //reply with STORED
        Protocol stored = new Protocol();
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

        this.sendMessage(stored.toString(), network.MC);
    }
}
