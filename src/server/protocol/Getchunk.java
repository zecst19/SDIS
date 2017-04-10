package server.protocol;

import fileSystem.FSChunk;
import server.Log;
import server.MNetwork;
import server.Server;
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
        if (protocol.getSenderId() != network.peerID){
            for (int i = 0; i < Log.bLogs.size(); i++){
                if (Log.bLogs.get(i).getFileId().equals(protocol.getFileId())){
                    if (Log.bLogs.get(i).getChunkNo() == protocol.getChunkNo()){
                        thread.start();
                        return;
                    }
                }
            }
            System.out.println("CHUNK not present");
        }
        else {
            System.out.println("Ignoring GETCHUNK");
        }
    }

    public void run(){
        System.out.println("Handling GETCHUNK");

        //reply with CHUNK if no CHUNK received
        try {
            int r = this.randomDelay();
            System.out.println(r);
            Thread.sleep(r);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }

        System.out.println("Finished Waiting");
        boolean first = false;
        Protocol p;

        try {
            if (chunkQueue.isEmpty()){
                first = true;
            }
            else if ((p = chunkQueue.take()) != null){
                System.out.println("#######################################################");
                System.out.println(p.getSenderId());
                if (p.getFileId().equals(protocol.getFileId())){
                    if (p.getChunkNo() == protocol.getChunkNo()){
                        first = true;
                    }
                }
            }
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }


        if (first){
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            Protocol chunk = new Protocol();
            chunk.setNetwork(network);
            chunk.setMessageType(chunk.CHUNK);
            chunk.setVersion(network.VERSION);
            chunk.setSenderId(network.peerID);
            chunk.setFileId(protocol.getFileId());
            chunk.setChunkNo(protocol.getChunkNo());
            FSChunk fc = new FSChunk(Server.homedir + "backupfiles/" + protocol.getFileId() + "-" + protocol.getChunkNo());
            chunk.setBody(fc.getBody());

            chunk.sendMessage(network.MDR);
        }
    }
}
