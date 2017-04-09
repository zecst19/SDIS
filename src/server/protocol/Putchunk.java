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
            for (int i = 0; i < Log.bLogs.size(); i++){
                if (Log.bLogs.get(i).getFileId().equals(protocol.getFileId())){
                    if (Log.bLogs.get(i).getChunkNo() == protocol.getChunkNo()){
                        System.out.println("CHUNK already STORED");
                        return;
                    }
                }
            }

            thread.start();
        }
        else {
            System.out.println("Ignoring PUTCHUNK");
        }
    }

    public void run(){
        System.out.println("Handling PUTCHUNK");

        FileHelper fh = new FileHelper();

        try {
            fh.writeChunk(new FSChunk(protocol.getFileId(), protocol.getChunkNo(), protocol.getBody()));

            try {
                System.out.println("########" + new String(protocol.getBody(), "ISO-8859-1") + "#######");
                System.out.println("###111#####" + new String(new FSChunk(Server.homedir + "backupfiles/" +protocol.getFileId() + "-" + protocol.getChunkNo()).getBody(), "ISO-8859-1") + "#######");
            }
            catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }

        Log.bLogs.add(new Log.BackupLog(protocol.getFileId(), protocol.getChunkNo(), protocol.getReplicationDeg()));

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
