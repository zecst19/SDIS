package server.protocol;

import server.MNetwork;
import server.thread.Worker;

public class Getchunk extends Worker {

    public Getchunk(MNetwork n, Protocol p){
        super(n, p);
    }

    public void start(){
        thread.start();
    }

    public void run(){

    }
}
