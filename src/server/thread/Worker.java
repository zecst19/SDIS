package server.thread;

import server.MNetwork;
import server.protocol.Protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Worker implements Runnable {
    protected Thread thread;
    protected MNetwork network;
    protected Protocol protocol;

    public Worker(MNetwork n, Protocol p){
        this.network = n;
        this.protocol = p;
        this.thread = new Thread(this);
    }

    public void sendMessage(String message, int mType){
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(network.addresses[mType]);
            DatagramPacket packet = new DatagramPacket(message.getBytes(),
                    message.length(), address, network.ports[mType]);
            socket.send(packet);

            //System.out.println(msg);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int randomDelay(){
        return ThreadLocalRandom.current().nextInt(0, 400 + 1);
    }


}
