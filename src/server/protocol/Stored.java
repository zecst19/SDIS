package server.protocol;

import server.Log;
import server.MNetwork;
import server.thread.Request;
import server.thread.RequestWorker;
import server.thread.Worker;

public class Stored extends Worker {

    public Stored(MNetwork n, Protocol p){
        super(n, p);
    }

    public void start(){
        if (protocol.getSenderId() != network.peerID){
            thread.start();
        } else {
            System.out.println("Ignoring Stored");
        }
    }

    public void run(){
        System.out.println("Handling STORED");

        for (int i = 0; i < Log.logs.size(); i++){
            if (Log.logs.get(i).getFileId().equals(protocol.getFileId())){
                if (Log.logs.get(i).getChunkNo() == protocol.getChunkNo()){
                    Log.logs.get(i).incReplication();
                    break;
                }
            }
        }

        try {
            RequestWorker.responseQueue.put(protocol);
            System.out.println("Sending STORED to RequestWorker");
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
