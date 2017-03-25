package server;

public abstract class Worker implements Runnable {
    protected Thread thread;
    protected MNetwork network;
    protected Protocol protocol;

    public Worker(MNetwork n, Protocol p){
        this.network = n;
        this.protocol = p;
        this.thread = new Thread(this);
    }

    //TODO: each protocol processed by a Peer will be handled within a worker thread

}
