package server;

public class Removed extends Worker{

    public Removed(MNetwork n, Protocol p){
        super(n, p);
    }

    public void start(){
        thread.start();
    }

    public void run(){

    }
}
