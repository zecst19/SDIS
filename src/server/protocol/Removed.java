package server.protocol;

import server.MNetwork;
import server.thread.Worker;

public class Removed extends Worker {

    public Removed(MNetwork n, Protocol p){
        super(n, p);
    }

    public void start(){
        thread.start();
    }

    public void run(){

    }
}
