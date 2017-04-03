package server;

import server.protocol.Protocol;
import server.thread.Listener;
import server.thread.RequestWorker;

public class Server {
    public static void main(String[] args) {
        //Server <Protocol Version> <ServerId> <Server Access Point> <MC addr> <MC port> <MDB addr> <MDB port> <MDR addr> <MDR port>
        //UDP/TCP:  <Server Access Point> --> <IP address>:<port>
        //RMI:      <Server Access Point> --> name of remote object providing testing service

        if (args.length !=  9) {
            //java Server 1.0 1 192.168.0.201:9636 228.0.0.0 4678 228.1.1.1 3215 228.2.2.2 9876
            //java Server 1.0 2 192.168.0.202:9636 228.0.0.0 4678 228.1.1.1 3215 228.2.2.2 9876

            System.out.println("Usage: java Server <Protocol Version> <ServerId> <Server Access Point> <MC addr> <MC port> <MDB addr> <MDB port> <MDR addr> <MDR port>");

            return;
        }

        MNetwork network = new MNetwork(Integer.parseInt(args[1]), args[3], Integer.parseInt(args[4]), args[5], Integer.parseInt(args[6]), args[7], Integer.parseInt(args[8]));
        System.out.println(network);

        RequestWorker clientRequest = new RequestWorker(network);
        Listener listenerMC  = new Listener(network.MC, network);
        Listener listenerMDB = new Listener(network.MDB, network);
        Listener listenerMDR = new Listener(network.MDR, network);


        clientRequest.start();
        listenerMC.start();
        listenerMDB.start();
        listenerMDR.start();



        //send Request to RequestWorker
        //testing only
        String putchunk1 =  "PUTCHUNK   1.0  1  878d8276f9e332b22ebdbcd61384647d9d65df41790ff231fda7842081efb721  5 1   \r\n\r\nrandomshtuffffffffffaçsldkfjapfghonapoegioqupfoiajpqwoip2938r9";
        String putchunk2 =  "PUTCHUNK   1.0  1  878d8276f9e332b22ebdbcd61384647d9d65df41790ff231fda7842081efb721  5 1   \r\n\r\n";
        String stored =     "STORED     1.0  1  878d8276f9e332b22ebdbcd61384647d9d65df41790ff231fda7842081efb721  5     \r\n\r\n";
        String getchunk =   "GETCHUNK   1.0  1  878d8276f9e332b22ebdbcd61384647d9d65df41790ff231fda7842081efb721  5     \r\n\r\n";
        String chunk =      "CHUNK      1.0  1  878d8276f9e332b22ebdbcd61384647d9d65df41790ff231fda7842081efb721  5     \r\n\r\nrandomshtuffffffffffaçsldkfjapfghonapoegioqupfoiajpqwoip2938r9";
        String delete =     "DELETE     1.0  1  878d8276f9e332b22ebdbcd61384647d9d65df41790ff231fda7842081efb721        \r\n\r\n";
        String removed =    "REMOVED    1.0  1  878d8276f9e332b22ebdbcd61384647d9d65df41790ff231fda7842081efb721  5     \r\n\r\n";

        //TODO: this code will be moved into client application
        Protocol request = new Protocol(getchunk);
        if (network.peerID == 1) {
            try {
                Thread.sleep(500);
                RequestWorker.requestQueue.put(request);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            //request.sendMessage(network.MDB);

        }
    }
}
