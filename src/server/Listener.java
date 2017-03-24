package server;

/**
 * Created by Gustavo on 24/03/2017.
 */
public class Listener implements Runnable {

    private final Thread thread;
    private volatile boolean running = true;

    public Listener(String mc_address, String name){
        thread = new Thread(this, name);
    }

    public void start(){
        thread.start();
        //subscribe to multicast channel
    }

    public void stop(){
        this.running = false;
    }

    public void run(){

        while (running){
            //listen to and handle requests
        }
    }
}
