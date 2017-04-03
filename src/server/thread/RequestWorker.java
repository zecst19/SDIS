package server.thread;

import server.MNetwork;
import server.protocol.Protocol;

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
        //TODO: protocol actions are to be initiated here (backup, restore, delete, manage storage, retrieve info)
        //still have to choose between udp, tcp or rmi

        System.out.println("Yhere");

        Protocol request;

        try {
            while ((request = requestQueue.take()) != null ){
                System.out.println("Yellow");
                if (request.getMessageType().equals(request.PUTCHUNK)){
                    //TODO: split chunks bigger than 64kB
                    //send PUTCHUNK over MDB
                    request.sendMessage(network.MDB);

                    //wait
                    Protocol p;


                    int actualReplication = 0;

                    for (int i = 0; i < 5; i++){
                        if (i > 0){
                            System.out.println("Trying again (" + i + ")");
                        } else {
                            System.out.println("Waiting for STORED");
                        }
                        //TODO: replace this and store information about where each chunk is

                        //send PUTCHUNK again
                        long start = System.currentTimeMillis();
                        long end = start + (2^i)*1000;
                        while(System.currentTimeMillis() < end) {
                            if ((p = responseQueue.poll()) != null){    //queue will be filled with STORED messages
                                if (p.getFileId().equals(request.getFileId())
                                        && p.getChunkNo() == request.getChunkNo()
                                        && p.getMessageType().equals(request.getMessageType())){

                                    actualReplication++;
                                    System.out.println("replication++");
                                }
                            }

                            //might have to sleep at the end
                        }

                        if (actualReplication >= request.getReplicationDeg()){
                            System.out.println("Finished");
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

                            System.out.println("CHUNK is here ready to be handled");
                            //TODO: send results to client
                        }
                    }
                }

                //or send delete

                //or else
            }
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }

        System.out.println("RequestWorker finished");
        this.stop();
    }
}
