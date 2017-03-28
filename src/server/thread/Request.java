package server.thread;

import server.protocol.Protocol;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Request implements Runnable {
    private final Thread thread;
    private volatile boolean running = true;
    public static BlockingQueue<Protocol> queue = new LinkedBlockingQueue<>();

    public Request(){
        this.thread = new Thread(this);
    }

    public void start(){
        thread.start();
    }

    public void stop(){
        this.running = false;
    }

    public void run(){

    }
}
