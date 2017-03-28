package server.protocol;

import server.MNetwork;
import server.thread.Request;
import server.thread.Worker;

public class Stored extends Worker {

    public Stored(MNetwork n, Protocol p){
        super(n, p);
    }

    public void start(){
        thread.start();
    }

    public void run(){
        try {
            Request.queue.put(protocol); //TODO: can't be this easy
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
