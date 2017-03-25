package server;

public class Putchunk extends Worker {


    public Putchunk(MNetwork n, Protocol p){
        super(n, p);
    }

    public void start(){
        thread.start();
    }

    public void run(){

    }
}
