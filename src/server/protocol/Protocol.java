package server.protocol;

import server.MNetwork;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Protocol {

    public final String PUTCHUNK = "PUTCHUNK";
    public final String STORED =   "STORED";
    public final String GETCHUNK = "GETCHUNK";
    public final String CHUNK =    "CHUNK";
    public final String DELETE =   "DELETE";
    public final String REMOVED =  "REMOVED";

    private final String CRLF =     "\r\n\r\n"; //actually CRLFCRLF but who cares

    private String messageType;
    private String version;
    private int senderId;
    private String fileId;
    private int chunkNo = -1;
    private int replicationDeg = -1;
    private byte[] body = null;
    private MNetwork network;

    public Protocol(){}

    public Protocol(String message) {

        String[] headAndBody = message.split(CRLF);
        String header = headAndBody[0];

        if (headAndBody.length != 1){
            try {
                this.body = headAndBody[1].getBytes("ISO-8859-1");
            }
            catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }

        String[] parts = header.split(" ");
        ArrayList<String> elements = new ArrayList<String>();//same as parts but without empty strings in between;
        for (int i = 0; i < parts.length; i++){
            if (!parts[i].equals("")){
                elements.add(parts[i]);
            }
        }

        this.messageType = elements.get(0);
        this.version = elements.get(1);
        this.senderId = Integer.parseInt(elements.get(2));
        this.fileId = elements.get(3);

        //existing protocols are: PUTCHUNK, STORED, GETCHUNK, CHUNK, DELETE and REMOVED
        if (!this.messageType.equals(DELETE)){//all fields necessary for DELETE are already filled out

            this.chunkNo = Integer.parseInt(elements.get(4));

            if (this.messageType.equals(PUTCHUNK)){
                this.replicationDeg = Integer.parseInt(elements.get(5));
            }
        }
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public void setChunkNo(int chunkNo) {
        this.chunkNo = chunkNo;
    }

    public int getReplicationDeg() {
        return replicationDeg;
    }

    public void setReplicationDeg(int replicationDeg) {
        this.replicationDeg = replicationDeg;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public MNetwork getNetwork() {
        return network;
    }

    public void setNetwork(MNetwork network) {
        this.network = network;
    }

    public String toString() {
        String hexBody = "", header, body;

        if (this.body != null){
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.body.length; i++){
                sb.append(String.format("%02X%s", this.body[i], (i < this.body.length - 1) ? "-" : ""));
            }
            hexBody = "\nBody: |" + sb.toString() + "|";
        }

        header = this.messageType + " " + this.version + " " + this.senderId + " " + this.fileId;
        if (!this.messageType.equals(DELETE)){

            header += " " + this.chunkNo;

            if (this.messageType.equals(PUTCHUNK)){
                header += " " + this.replicationDeg;
            }
        }

        if (this.messageType.equals(PUTCHUNK) || this.messageType.equals(CHUNK)){
            try {
                String stringBody = new String(this.body, "ISO-8859-1");
            }
            catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
            return header + CRLF + this.body;
        }
        else {
            return header + CRLF;
        }
    }

    public void printMessage() {
        String hexBody = "";

        if (this.body != null){
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.body.length; i++){
                sb.append(String.format("%02X%s", this.body[i], (i < this.body.length - 1) ? "-" : ""));
            }
            hexBody = "\nBody: |" + sb.toString() + "|";
        }

        System.out.println("Message Type: " + this.messageType + "\nVersion: " + this.version +
                "\nSenderId: " + this.senderId + "\nFileId: |" + this.fileId + "|\nChunkNo: " +
                this.chunkNo + "\nReplicationDeg: " + this.replicationDeg + hexBody);

    }

    public void sendMessage(int mType){
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(network.addresses[mType]);
            DatagramPacket packet = new DatagramPacket(this.toString().getBytes(),
                    this.toString().length(), address, network.ports[mType]);
            socket.send(packet);

            System.out.println("SENDMESSAGE: " + this.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}