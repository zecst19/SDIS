package server.thread;

import server.protocol.Protocol;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RequestWorker implements Runnable {
    private final Thread thread;
    private volatile boolean running = true;
    public static BlockingQueue<Protocol> queue = new LinkedBlockingQueue<>();

    public RequestWorker(){
        this.thread = new Thread(this);
    }

    public void start(){
        thread.start();
    }

    public void stop(){
        this.running = false;
    }

    public void run(){
        //TODO: protocol actions are to be initiated here (backup, restore, delete, manage storage, retrieve info)
        //still have to choose between udp, tcp or rmi

        //send putchunk -> waits for a second (while receiving messages ->
        //  ->if (nr of stored messages < replication) -> waits for 2 seconds (...) repeat up to 5 times
        while (running){
            if (true/* request = backup */){
                //TODO: split chunks bigger than 64kB
                //send PUTCHUNK over MC
                Protocol put = new Protocol();

                //wait
                Protocol p;


                int actualReplication = 0;

                for (int i = 0; i < 5; i++){
                    //TODO: replace this and store information about where each chunk is
                    if (actualReplication < put.getReplicationDeg()){
                        //send PUTCHUNK again

                        long start = System.currentTimeMillis();
                        long end = start + 2^i;
                        while(System.currentTimeMillis() < end) {
                            if ((p = queue.poll()) != null){    //queue will be filled with STORED messages
                                if (p.getFileId().equals(put.getFileId())){
                                    if (p.getChunkNo() == put.getChunkNo()){
                                        actualReplication++;
                                    }
                                }
                            }

                            //might have to sleep at the end
                        }
                    }
                }
            }
        }




        //or send restore

        //or send delete

        //or else
    }
}
