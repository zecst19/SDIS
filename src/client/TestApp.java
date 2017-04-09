package client;

import server.Server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.IOException;

public class TestApp {

    private static String addr;
    private static int port;
    private static String sub_protocol;
    private static String file;
    private static int rep_degree;
    private static Request request;


    public static void main(String[] args) throws IOException{

        if(args.length < 2 || args.length > 4){
            //java TestApp 192.168.0.201:9636 BACKUP example.txt 2
            System.out.println("Usage: java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>");
            System.exit(1);
        }

        String[] peer_ap = args[0].split(":");
        addr = peer_ap[0];
        port = Integer.parseInt(peer_ap[1]);
        sub_protocol = args[1];

        if (args.length >= 3){
            file = args[2];
        }
        if (args.length == 4){
            rep_degree = Integer.parseInt(args[3]);
        }

        //String msg;

        if(sub_protocol.equals("BACKUP")){
            if(args.length != 4){
                System.out.println("Wrong number of arguments for BACKUP protocol.");
                System.exit(1);
            }

            request = new Request("BACKUP", file, rep_degree);

        }
        else if(sub_protocol.equals("RESTORE")){
            if(args.length != 3){
                System.out.println("Wrong number of arguments for RESTORE protocol.");
                System.exit(1);
            }

            request = new Request("RESTORE", file);

        }
        else if(sub_protocol.equals("DELETE")){
            if(args.length != 3){
                System.out.println("Wrong number of arguments for DELETE protocol.");
                System.exit(1);
            }

            request = new Request("Delete", file);
        }
        else if(sub_protocol.equals("RECLAIM")){
            if(args.length != 3){
                System.out.println("Wrong number of arguments for RECLAIM protocol.");
                System.exit(1);
            }

            int space = Integer.parseInt(file);
            request = new Request("RECLAIM", space);

        }
        else if(sub_protocol.equals("STATE")){
            if(args.length != 2){
                System.out.println("Wrong number of arguments for STATE protocol.");
                System.exit(1);
            }

            request = new Request("STATE");

        }
        else{
            System.out.println("Invalid sub_protocol!");
            System.exit(1);
        }

        //send request
        DatagramSocket socket = new DatagramSocket();

        byte[] sbuf = request.toString().getBytes();

        InetAddress address = InetAddress.getByName(addr);
        DatagramPacket packet = new DatagramPacket(sbuf, sbuf.length, address, port);
        socket.send(packet);

        //get response
        byte[] rbuf = new byte[64300];
        packet = new DatagramPacket(rbuf, rbuf.length);

        DatagramSocket socket1 = new DatagramSocket(port);
        socket1.receive(packet);

        int size = packet.getLength();
        byte[] data = new byte[size];

        for (int i = 0; i < size; i++){
            data[i] = rbuf[i];
        }

        // display response
        String received = new String(data, "ISO-8859-1");

        String[] parts = received.split("\r\n\r\n");

        Response resp;
        if (parts.length == 2){
            resp = new Response(parts[0], parts[1].getBytes("ISO-8859-1"));
        }
        else {
            resp = new Response(parts[0]);
        }

        if (resp.getData() != null){
            try {
                Path f = Paths.get(Server.homedir + resp.getMessage());
                Files.write(f, resp.getData());
                System.out.println("Finished Restoring File");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (resp.getState() != null){
            //TODO: display STATE
        }
        else {
            System.out.println(resp.getMessage());
        }

        socket.close();
        socket1.close();
    }
}
