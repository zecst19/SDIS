package server.thread;

import server.MNetwork;
import server.protocol.Protocol;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RequestWorker implements Runnable {
    private final Thread thread;
    private volatile boolean running = true;
    private MNetwork network;
    public static BlockingQueue<Protocol> responseQueue = new LinkedBlockingQueue<>();

    public RequestWorker(MNetwork n){
        this.thread = new Thread(this, "RequestWorker");
        this.network = n;
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

        //testing only
        String putchunk1 =  "PUTCHUNK   1.0  1  878d8276f9e332b22ebdbcd61384647d9d65df41790ff231fda7842081efb721  5 2   \r\n\r\nrandomshuffffffffffaçsldkfjapfghonapoegioqupfoiajpqwoip2938r9";
        Protocol request = new Protocol(putchunk1);

        while (running){
            if (true/* request = backup */){
                //TODO: split chunks bigger than 64kB
                //send PUTCHUNK over MDB
                if (network.peerID == 1){
                    request.sendMessage(network.MDB);
                }

                //wait
                Protocol p;


                int actualReplication = 0;

                for (int i = 0; i < 5; i++){
                    if (i > 0){
                        System.out.println("Trying again (" + i + ")");
                    }
                    //TODO: replace this and store information about where each chunk is
                    if (actualReplication < request.getReplicationDeg()){
                        //send PUTCHUNK again

                        long start = System.currentTimeMillis();
                        long end = start + (2^i)*1000;
                        while(System.currentTimeMillis() < end) {
                            if ((p = responseQueue.poll()) != null){    //queue will be filled with STORED messages
                                if (p.getFileId().equals(request.getFileId())){
                                    if (p.getChunkNo() == request.getChunkNo()){
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
