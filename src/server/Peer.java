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
    private String addrMC, addrMDB, addrMDR;    //228.0.0.0     228.1.1.1   228.2.2.2
    private int portMC, portMDB, portMDR;       //4678          3215        9876

    public Peer(){

    }

    public Peer(String mc_addr, int mc_port, String mdb_addr, int mdb_port, String mdr_addr, int mdr_port){
        this.addrMC = mc_addr;
        this.portMC = mc_port;
        this.addrMDB = mdb_addr;
        this.portMDB = mdb_port;
        this.addrMDR = mdr_addr;
        this.portMDR = mdr_port;
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

    public void sendMC(Protocol p){

    }

    public Protocol readMC(){

        return new Protocol();
    }

    public void sendMDB(Protocol p){

    }

    public Protocol readMDB(){

        return new Protocol();
    }

    public void sendMDR(Protocol p){

    }

    public Protocol readMDR(){

        return new Protocol();
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