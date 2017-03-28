package server.protocol;

import server.MNetwork;
import server.thread.Request;
import server.thread.RequestWorker;
import server.thread.Worker;

public class Stored extends Worker {

    public Stored(MNetwork n, Protocol p){
        super(n, p);
    }

    public void start(){
        thread.start();
    }

    public void run(){
        System.out.println("Handling STORED");
        try {
            RequestWorker.responseQueue.put(protocol); //TODO: can't be this easy
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
