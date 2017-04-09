package server.thread;

import server.Log;
import server.MNetwork;
import server.Server;
import server.protocol.Protocol;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RequestWorker implements Runnable {
    private final Thread thread;
    private volatile boolean running = true;
    private MNetwork network;
    public static BlockingQueue<Protocol> responseQueue;
    public static BlockingQueue<Protocol> requestQueue;

    public RequestWorker(MNetwork n){
        this.thread = new Thread(this, "RequestWorker");
        this.network = n;
        responseQueue = new LinkedBlockingQueue<>();
        requestQueue = new LinkedBlockingQueue<>();
    }

    public void start(){
        thread.start();
    }

    public void stop(){
        this.running = false;
    }

    public void run() {
        //still have to choose between udp, tcp or rmi

        responseQueue.clear();

        Protocol request;

        try {
            while ((request = requestQueue.take()) != null ){
                if (request.getMessageType().equals(request.PUTCHUNK)){
                    //send PUTCHUNK over MDB
                    request.sendMessage(network.MDB);

                    //wait
                    Protocol p;

                    for (int i = 0; i < 5; i++){
                        if (i > 0){
                            System.out.println("Trying again (" + i + ")");
                        } else {
                            System.out.println("Waiting for STORED");
                        }

                        int actualReplication = 0;
                        request.sendMessage(network.MDB);

                        long start = System.currentTimeMillis();
                        long end = start + (2^i)*1000;
                        while(System.currentTimeMillis() < end) {
                            if ((p = responseQueue.poll()) != null){    //queue will be filled with STORED messages
                                if (p.getFileId().equals(request.getFileId())
                                        && p.getChunkNo() == request.getChunkNo()
                                        && p.getMessageType().equals(p.STORED)){

                                    boolean firstStored = true;
                                    for (int j = 0; j < Log.lLogs.size(); j++){
                                        if (Log.lLogs.get(j).getFileId().equals(p.getFileId())){
                                            if (Log.lLogs.get(j).getChunkNo() == p.getChunkNo()){

                                                Log.lLogs.get(j).incReplication();
                                                actualReplication = Log.lLogs.get(j).getReplication();
                                                System.out.println("replication++");
                                                firstStored = false;
                                                break;
                                            }
                                        }
                                    }
                                    if (firstStored == true){
                                        Log.lLogs.add(new Log.LocalLog(p.getFileId(), p.getChunkNo(), p.getReplicationDeg()));
                                    }
                                }
                            }
                        }

                        if (actualReplication >= request.getReplicationDeg()){
                            System.out.println("Finished BACKUP");
                            Server.replyQueue.put(new Protocol());
                            break;
                        }
                    }
                }//or send restore
                else if (request.getMessageType().equals(request.GETCHUNK)){
                    request.sendMessage(network.MC);

                    //wait
                    Protocol p;

                    while ((p = responseQueue.take()) != null ){

                        if (p.getFileId().equals(request.getFileId())
                                && p.getChunkNo() == request.getChunkNo()
                                && p.getMessageType().equals(p.CHUNK)){

                            Server.replyQueue.put(p);
                            System.out.println("Finished fetching CHUNK");
                            break;
                        }
                    }
                }
                else if (request.getMessageType().equals(request.DELETE)){
                    //send DELETE over MC
                    request.sendMessage(network.MC);

                    for (int i = 0; i < Log.lLogs.size(); i++){
                        if (Log.lLogs.get(i).getFileId().equals(request.getFileId())){
                            Log.lLogs.remove(i);
                        }
                    }
                }
                else {
                    System.out.println("Invalid Protocol");
                }
            }
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }

        System.out.println("RequestWorker finished");
        this.stop();
    }
}
