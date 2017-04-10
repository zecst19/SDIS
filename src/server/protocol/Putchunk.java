package server.protocol;

import server.Log;
import server.MNetwork;
import server.Server;
import server.thread.Worker;
import fileSystem.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Putchunk extends Worker {


    public Putchunk(MNetwork n, Protocol p){
        super(n, p);
    }

    public void start(){
        System.out.println("SenderID: " + protocol.getSenderId());
        System.out.println("My ID: " + network.peerID);
        if (protocol.getSenderId() != network.peerID){
            thread.start();
        }
        else {
            System.out.println("Ignoring PUTCHUNK");
        }
    }

    public void run(){
        System.out.println("Handling PUTCHUNK");
        if (Log.bLogs.isEmpty()){
            FileHelper fh = new FileHelper();

            try {
                fh.writeChunk(new FSChunk(protocol.getFileId(), protocol.getChunkNo(), protocol.getBody()));
            }
            catch (IOException e){
                e.printStackTrace();
            }

            Log.BackupLog l = new Log.BackupLog(protocol.getFileId(), protocol.getChunkNo(), protocol.getReplicationDeg());
            l.peers.add(protocol.getSenderId());
            Log.bLogs.add(l);
        }
        else {
            boolean duplicated = false;
            for (int i = 0; i < Log.bLogs.size(); i++){
                if (Log.bLogs.get(i).getFileId().equals(protocol.getFileId())){
                    if (Log.bLogs.get(i).getChunkNo() == protocol.getChunkNo()){
                        System.out.println("CHUNK already STORED");
                        duplicated = true;
                        break;
                    }
                }
            }
            if (!duplicated){
                FileHelper fh = new FileHelper();

                try {
                    fh.writeChunk(new FSChunk(protocol.getFileId(), protocol.getChunkNo(), protocol.getBody()));
                }
                catch (IOException e){
                    e.printStackTrace();
                }

                Log.BackupLog l = new Log.BackupLog(protocol.getFileId(), protocol.getChunkNo(), protocol.getReplicationDeg());
                l.peers.add(protocol.getSenderId());
                Log.bLogs.add(l);
            }
        }
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
        return;
    }
}
