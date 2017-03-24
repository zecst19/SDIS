package server;

public class Server {
    public static void main(String[] args) {
        //Server <Protocol Version> <ServerId> <Server Access Point> <MC addr> <MC port> <MDB addr> <MDB port> <MDR addr> <MDR port>
        //UDP/TCP:  <Server Access Point> --> <IP address>:<port>
        //RMI:      <Server Access Point> --> name of remote object providing testing service

        if (args.length !=  9) {
            //java Server 1.0 1 192.168.0.101:9636 228.0.0.0 4678 228.1.1.1 3215 228.2.2.2 9876
            System.out.println("Usage: java Server <Protocol Version> <ServerId> <Server Access Point> <MC addr> <MC port> <MDB addr> <MDB port> <MDR addr> <MDR port>");

            return;
        }

        MNetwork network = new MNetwork(Integer.parseInt(args[1]), args[3], Integer.parseInt(args[4]), args[5], Integer.parseInt(args[6]), args[7], Integer.parseInt(args[8]));

        Listener listenerMC  = new Listener(0, network);
        Listener listenerMDB = new Listener(1, network);
        //Listener listenerMDR = new Listener(2, network);

        listenerMC.start();
        listenerMDB.start();
        //listenerMDR.start();
    }
}
