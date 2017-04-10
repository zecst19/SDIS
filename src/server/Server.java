package server;

import client.Response;
import fileSystem.FSChunk;
import fileSystem.FileHelper;
import server.protocol.Protocol;
import server.thread.Listener;
import server.thread.RequestWorker;
import client.Request;


import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Server {
    public static final int PORT = 9636;
    public static final String homedir = "/home/gustavo/";
    public static BlockingQueue<Protocol> replyQueue;

    public Server() {}

    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException {
        //Server <Protocol Version> <ServerId> <Server Access Point> <MC addr> <MC port> <MDB addr> <MDB port> <MDR addr> <MDR port>
        //UDP/TCP:  <Server Access Point> --> <IP address>:<port>
        //RMI:      <Server Access Point> --> name of remote object providing testing service

        if (args.length != 9) {
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
        Listener listenerMC = new Listener(network.MC, network);
        Listener listenerMDB = new Listener(network.MDB, network);
        Listener listenerMDR = new Listener(network.MDR, network);


        clientRequest.start();
        listenerMC.start();
        listenerMDB.start();
        listenerMDR.start();

        //TODO: finish
        Request cReq = new Request();
        Response resp;
        Protocol request;

        String filename = "example.txt";

        replyQueue = new LinkedBlockingDeque<>();

        while (true) {
            //get request
            byte[] rbuf = new byte[512];
            DatagramSocket socket = new DatagramSocket(PORT);
            DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
            socket.receive(packet);

            int size = packet.getLength();
            byte[] data = new byte[size];

            for (int i = 0; i < size; i++){
                data[i] = rbuf[i];
            }

            String str = new String(data, "ISO-8859-1");
            cReq.fromString(str);
            System.out.println(str);

            if (cReq.getType().equals("BACKUP")) {//BACKUP
                filename = cReq.getFilename();
                int replication = cReq.getReplicationDegree();
                FileHelper fh = new FileHelper();
                fh.readLocalFile(filename);

                for (int i = 0; i < fh.getFullFile().length; i++) {

                    String stringBody = new String(fh.getFullFile()[i].getBody(), "ISO-8859-1");
                    String requestString = "PUTCHUNK 1.0 " + network.peerID + " " + fh.generateFileId(filename) + " " + i + " " + replication + "\r\n\r\n" + stringBody;
                    request = new Protocol(requestString);
                    RequestWorker.requestQueue.put(request);
                }

                int replies = 0;
                while (replyQueue.take() != null) {
                    replies++;
                    if (replies < fh.getFullFile().length) {
                        continue;
                    } else {
                        break;
                    }
                }
                resp = new Response("Every CHUNK has been backed up");
            }
            else if (cReq.getType().equals("RESTORE")) {//RESTORE
                filename = cReq.getFilename();
                FileHelper fh = new FileHelper();

                int nrChunks = 0;
                for (int i = 0; i < Log.lLogs.size(); i++) {
                    if (Log.lLogs.get(i).getFileId().equals(fh.generateFileId(filename))) {
                        nrChunks++;
                    }
                }

                for (int i = 0; i < nrChunks; i++) {

                    String requestString = "GETCHUNK 1.0 " + network.peerID + " " + fh.generateFileId(filename) + " " + i + "\r\n\r\n";
                    request = new Protocol(requestString);
                    RequestWorker.requestQueue.put(request);
                }

                Protocol reply;
                FileHelper file = new FileHelper();

                FSChunk[] chunks = new FSChunk[nrChunks];
                int replies = 0;
                while ((reply = replyQueue.take()) != null) {
                    chunks[replies] = new FSChunk(reply);

                    replies++;
                    if (replies < nrChunks) {
                        continue;
                    } else break;
                }

                file.setFullFile(chunks);

                byte[] body = file.restichFile();

                resp = new Response(filename, body);
            }
            else if (cReq.getType().equals("DELETE")){//DELETE
                filename = cReq.getFilename();
                FileHelper fh = new FileHelper();

                String requestString = "DELETE 1.0 " + network.peerID + " " + fh.generateFileId(filename) + "\r\n\r\n";
                request = new Protocol(requestString);
                RequestWorker.requestQueue.put(request);

                resp = new Response("File has been deleted");
            }
            else if (cReq.getType().equals("RECLAIM")){//RECLAIM
                //TODO: this
                resp = new Response("TODO");
            }
            else if (cReq.getType().equals("STATE")){//STATE
                //TODO: and this
                resp = new Response("TODO");
            }
            else {
                resp = new Response("Something went wrong");
            }

            //send response
            byte[] reply = resp.toString().getBytes("ISO-8859-1");
            InetAddress address = packet.getAddress();
            DatagramSocket socket1 = new DatagramSocket();
            DatagramPacket packet1 = new DatagramPacket(reply, reply.length, address, PORT);
            socket1.send(packet1);

            socket.close();
            socket1.close();

        }
    }
}

