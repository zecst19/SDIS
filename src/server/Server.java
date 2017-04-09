package server;

import fileSystem.FSChunk;
import fileSystem.FileHelper;
import server.protocol.Protocol;
import server.thread.Listener;
import server.thread.RequestWorker;
import server.Message;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    public static final String homedir = "/home/gustavo/";
    public static BlockingQueue<Protocol> replyQueue;

    public Server() {}

    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException {
        //Server <Protocol Version> <ServerId> <Server Access Point> <MC addr> <MC port> <MDB addr> <MDB port> <MDR addr> <MDR port>
        //UDP/TCP:  <Server Access Point> --> <IP address>:<port>
        //RMI:      <Server Access Point> --> name of remote object providing testing service

        if (args.length !=  9) {
            //java Server 1.0 1 192.168.0.201:9636 228.0.0.0 4678 228.1.1.1 3215 228.2.2.2 9876
            //java Server 1.0 2 192.168.0.202:9636 228.0.0.0 4678 228.1.1.1 3215 228.2.2.2 9876

            System.out.println("Usage: java Server <Protocol Version> <ServerId> <Server Access Point> <MC addr> <MC port> <MDB addr> <MDB port> <MDR addr> <MDR port>");

            return;
        }

        //Create files and directories
        File backupfiles = new File(homedir + "backupfiles");
        backupfiles.mkdir();
        File localfiles = new File(homedir + "localfiles");
        localfiles.mkdir();

        Log.bLogs = new ArrayList<>();
        Log.lLogs = new ArrayList<>();

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

        //TODO: finish
        Protocol request;

        replyQueue = new LinkedBlockingDeque<>();

//        while (true /* listening to client requests */){
            //TODO: parse info from client request

            if (network.peerID == 1){//BACKUP
                //....
                String filename = "example.txt";    //filename will be read from client request
                int replication = 1;                //replication will be read from client request
                FileHelper fh = new FileHelper();
                fh.readLocalFile(filename);

                for (int i = 0; i < fh.getFullFile().length; i++){

                    String stringBody = new String(fh.getFullFile()[i].getBody(), "ISO-8859-1");
                    String requestString = "PUTCHUNK 1.0 " + network.peerID + " " + fh.generateFileId(filename) + " " + i + " " + replication + "\r\n\r\n" + stringBody;
                    request = new Protocol(requestString);
                    RequestWorker.requestQueue.put(request);
                }

                int replies = 0;
                while (replyQueue.take() != null ){
                    replies++;
                    if (replies < fh.getFullFile().length){
                        continue;
                    }
                    else break;
                }
                //TODO: send this message to client
                System.out.println("Every CHUNK has been backed up");
            }
            if (network.peerID == 1){//RESTORE
                String filename = "example.txt";    //filename will be read from client request
                FileHelper fh = new FileHelper();

                int nrChunks = 0;
                for (int i = 0; i < Log.lLogs.size(); i++){
                    if (Log.lLogs.get(i).getFileId().equals(fh.generateFileId(filename))){
                        nrChunks++;
                    }
                }

                for (int i = 0; i < nrChunks; i++){

                    String requestString = "GETCHUNK 1.0 " + network.peerID + " " + fh.generateFileId(filename) + " " + i + "\r\n\r\n";
                    request = new Protocol(requestString);
                    RequestWorker.requestQueue.put(request);
                }

                Protocol reply;
                FileHelper file = new FileHelper();

                FSChunk[] chunks = new FSChunk[nrChunks];
                int replies = 0;
                while ((reply = replyQueue.take()) != null ){
                    chunks[replies] = new FSChunk(reply);

                    replies++;
                    if (replies < nrChunks){
                        continue;
                    }
                    else break;
                }

                file.setFullFile(chunks);

                byte[] body = file.restichFile();

                try {
                    Path f = Paths.get("/home/gustavo/" + "example.txt");
                    Files.write(f, body);
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                //TODO: send body to client
            }
/*            else if (true){//DELETE
                String filename = "example.txt";    //filename will be read from client request
                FileHelper fh = new FileHelper();

                String requestString = "DELETE 1.0 " + network.peerID + " " + fh.generateFileId(filename) + "\r\n\r\n";
                request = new Protocol(requestString);
                RequestWorker.requestQueue.put(request);

                //TODO: send this message to client
                System.out.println("File has been deleted");
            }
            else if (true ){//RECLAIM
                //TODO: this
            }
            else if (true ){//STATE
                //TODO: and this
            }
            else {
                //TODO: send this message to client
                System.out.println("Invalid Protocol");
            }*/
 //       }


      try {
           Server obj = new Server();
           Message stub = (Message) UnicastRemoteObject.exportObject(obj, 0);

           // Bind the remote object's stub in the registry
           Registry registry = LocateRegistry.getRegistry();
           registry.bind("Hello", stub);

           System.err.println("Server ready");
       } catch (Exception e) {
           System.err.println("Server exception: " + e.toString());
           e.printStackTrace();
       }
   }

}

