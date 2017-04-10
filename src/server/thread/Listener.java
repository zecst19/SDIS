package server.thread;

import server.MNetwork;
import server.protocol.*;

import javax.swing.plaf.synth.SynthEditorPaneUI;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
            byte[] msg = new byte[network.CHUNK_SIZE + network.MAX_HEADER_SIZE];
            DatagramPacket mPacket = new DatagramPacket(msg, msg.length);

            try {
                mSocket.receive(mPacket);
            }
            catch (IOException e){
                e.printStackTrace();
            }

            int size = mPacket.getLength();
            byte[] data = new byte[size];

            for (int i = 0; i < size; i++){
                data[i] = msg[i];
            }

            try {
                Protocol p = new Protocol(new String(data, "ISO-8859-1"));

                //all conditions are verified on worker threads
                if (p.getMessageType().equals(p.PUTCHUNK)){
                    //deploy worker
                    new Putchunk(network, p).start();
                }
                else if (p.getMessageType().equals(p.STORED)){
                    //deploy worker
                    new Stored(network, p).start();
                }
                else if (p.getMessageType().equals(p.GETCHUNK)){
                    //deploy worker
                    new Getchunk(network, p).start();
                }
                else if (p.getMessageType().equals(p.CHUNK)){
                    //deploy worker
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
            catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }
    }
}
