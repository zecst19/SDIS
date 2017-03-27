package server;

public class Request implements Runnable {
    private final Thread thread;
    private volatile boolean running = true;

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
        //TODO: protocol actions are to be initiated here (backup, restore, delete, manage storage, retrieve info)
        //still have to choose between udp, tcp or rmi

        //send putchunk -> waits for a second (while receiving messages ->
        //  ->if (nr of stored messages < replication) -> waits for 2 seconds (...) repeat up to 5 times
        //TODO: implementar BlockingQueue

        //or send restore

        //or send delete

        //or else
    }
}
