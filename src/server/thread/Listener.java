package server.thread;

import server.MNetwork;
import server.protocol.*;

import javax.swing.plaf.synth.SynthEditorPaneUI;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Listener implements Runnable {

    private final Thread thread;
    private volatile boolean running = true;
    private MNetwork network;
    private int MNType;
    private InetAddress mAddress;
    private MulticastSocket mSocket;

    public Listener(int MNType, MNetwork n) {
        this.network = n;
        this.MNType = MNType;
        thread = new Thread(this, "Listener");
    }

    public void subscribe() throws IOException{
        this.mAddress = InetAddress.getByName(network.addresses[this.MNType]);
        this.mSocket = new MulticastSocket(network.ports[this.MNType]);
        this.mSocket.joinGroup(this.mAddress);
    }

    public void start(){
        thread.start();
        String type;
        if (this.MNType == 0){
            type = "MC";
        } else if (this.MNType == 1){
            type = "MDB";
        } else {type = "MDR";}

        System.out.println("Started listener " + type);
    }

    public void stop(){
        this.running = false;
    }

    public void run() {

        try {
            this.subscribe();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        while (running){
            //listen to and handle requests
            byte[] msg = new byte[network.CHUNK_SIZE];
            DatagramPacket mPacket = new DatagramPacket(msg, msg.length);
            try {
                mSocket.receive(mPacket);
            }
            catch (IOException e){
                e.printStackTrace();
            }
            Protocol p = new Protocol(new String(mPacket.getData()));

            //all conditions are verified on worker threads
            if (p.getMessageType().equals(p.PUTCHUNK)){
                //deploy worker
                System.out.println("Received PUTCHUNK");
                new Putchunk(network, p).start();
            }
            else if (p.getMessageType().equals(p.STORED)){
                //deploy worker
                System.out.println("Received STORED");
                new Stored(network, p).start();
            }
            else if (p.getMessageType().equals(p.GETCHUNK)){
                //deploy worker
                System.out.println("Received GETCHUNK");
                new Getchunk(network, p).start();
            }
            else if (p.getMessageType().equals(p.CHUNK)){
                //deploy worker
                System.out.println("Received CHUNK");
                new Chunk(network, p).start();
            }
            else if (p.getMessageType().equals(p.DELETE)){
                //deploy worker
                new Delete(network, p).start();
            }
            else if (p.getMessageType().equals(p.REMOVED)){
                //deploy worker
                new Removed(network, p).start();
            }
            else this.stop();

        }
    }

    public String generateFileId(String filename) throws IOException, NoSuchAlgorithmException {
        String cwd = System.getProperty("user.dir");

        //"src/server".length = 10 <-------------------|
        String homedir = cwd.substring(0, cwd.length()-10) + "localfiles/";
        Path file = Paths.get(homedir + filename);

        //Getting file metadata
        BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class);
        String created =  attributes.creationTime().toString();
        String lastAccess = attributes.lastAccessTime().toString();
        String dateModified =  attributes.lastModifiedTime().toString();
        long size = attributes.size();

        //Getting local mac address
        //TODO: find out if there's a better way to do this
        //eth0 if not on VM; enp0s9 otherwise
        Enumeration<InetAddress> netIface = NetworkInterface.getByName("enp0s9").getInetAddresses();

        InetAddress localAddress = netIface.nextElement();
        while(netIface.hasMoreElements())
        {
            localAddress = netIface.nextElement();
            if(localAddress instanceof Inet4Address && !localAddress.isLoopbackAddress())
            {
                break;
            }
        }

        NetworkInterface network = NetworkInterface.getByInetAddress(localAddress);
        byte[] mac = network.getHardwareAddress();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++){
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        String owner = sb.toString();

        //Creating fileId unique string
        String fileId = filename + owner + created + dateModified + size;
        System.out.println(fileId);


        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(fileId.getBytes(StandardCharsets.UTF_8));

        StringBuffer strbuf = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            strbuf.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
        }

        return strbuf.toString();
    }
}
