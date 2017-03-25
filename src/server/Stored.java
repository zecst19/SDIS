package server;

public class Stored extends Worker {

    public Stored(MNetwork n, Protocol p){
        super(n, p);
    }

    public void start(){
        thread.start();
    }

    public void run(){

    }
}
