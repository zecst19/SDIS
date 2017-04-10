package server.protocol;

import server.Log;
import server.MNetwork;
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

        for (int i = 0; i < Log.bLogs.size(); i++){
            if (Log.bLogs.get(i).getFileId().equals(protocol.getFileId())){
                if (Log.bLogs.get(i).getChunkNo() == protocol.getChunkNo()){

                    boolean duplicated = false;
                    for (int j = 0; j < Log.bLogs.get(i).peers.size(); j++){
                        if (Log.bLogs.get(i).peers.get(j) == protocol.getSenderId()){
                            duplicated = true;
                            break;
                        }
                    }

                    if (!duplicated){
                        Log.bLogs.get(i).peers.add(protocol.getSenderId());
                        break;
                    }
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
