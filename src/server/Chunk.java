package server;

public class Chunk extends Worker {

    public Chunk(MNetwork n, Protocol p){
        super(n, p);
    }

    public void start(){
        thread.start();
    }

    public void run(){

    }
}
