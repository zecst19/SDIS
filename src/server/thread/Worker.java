package server.thread;

import server.MNetwork;
import server.protocol.Protocol;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Worker implements Runnable {
    protected Thread thread;
    protected MNetwork network;
    protected Protocol protocol;

    public Worker(MNetwork n, Protocol p){
        this.network = n;
        this.protocol = p;
        this.thread = new Thread(this, "Worker");
    }

    public int randomDelay(){
        return ThreadLocalRandom.current().nextInt(0, 400 + 1);
    }


}
