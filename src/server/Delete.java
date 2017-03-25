package server;

public class Delete extends Worker {

    public Delete(MNetwork n, Protocol p){
        super(n, p);
    }

    public void start(){
        thread.start();
    }

    public void run(){

    }
}
