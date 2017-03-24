package server;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.net.*;
import java.util.Enumeration;
import java.util.concurrent.ThreadLocalRandom;

public class Peer {
    private int peerId;

    public Peer(){

    }

    public Peer(int id){
        this.peerId = id;
    }

    public String generateFileId(String filename) throws Exception, UnknownHostException, SocketException {
        String cwd = System.getProperty("user.dir");

        //"src/server".length = 10 <--
        String homedir = cwd.substring(0, cwd.length()-10) + "localfiles/";
        Path file = Paths.get(homedir + filename);

        //Getting file metadata
        BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class);
        String created =  attributes.creationTime().toString();
        String lastAccess = attributes.lastAccessTime().toString();
        String dateModified =  attributes.lastModifiedTime().toString();
        long size = attributes.size();

        //Getting local mac address
        //eth0 if not on VM; enp0s3 otherwise
        Enumeration<InetAddress> netIface = NetworkInterface.getByName("eth0").getInetAddresses();

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


    public int randomDelay(){
        return ThreadLocalRandom.current().nextInt(0, 400 + 1);
    }

    public static void main(String args[]) throws Exception {

        Peer p = new Peer();
        String fileId = p.generateFileId("example.txt");

        System.out.println(fileId);
    }
}