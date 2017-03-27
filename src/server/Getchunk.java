package server;

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
