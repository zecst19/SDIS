package server;

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

    public Protocol(){}

    public Protocol(String message){

        String[] headAndBody = message.split(CRLF);
        String header = headAndBody[0];

        if (headAndBody.length != 1){
            this.body = headAndBody[1].getBytes();
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

    public String getVersion() {
        return version;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getFileId() {
        return fileId;
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public int getReplicationDeg() {
        return replicationDeg;
    }

    public byte[] getBody() {
        return body;
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

    public static void main(String args[]){

        //TODO: fix protocols that result in error
        String putchunk1 =  "PUTCHUNK   1.0  1  878d8276f9e332b22ebdbcd61384647d9d65df41790ff231fda7842081efb721  5 2   \r\n\r\nrandomshuffffffffffaçsldkfjapfghonapoegioqupfoiajpqwoip2938r9";
        String putchunk2 =  "PUTCHUNK   1.0  1  878d8276f9e332b22ebdbcd61384647d9d65df41790ff231fda7842081efb721  5 2   \r\n\r\n";
        String stored =     "STORED     1.0  1  878d8276f9e332b22ebdbcd61384647d9d65df41790ff231fda7842081efb721  5     \r\n\r\n";
        String getchunk =   "GETCHUNK   1.0  1  878d8276f9e332b22ebdbcd61384647d9d65df41790ff231fda7842081efb721  5     \r\n\r\n";
        String chunk =      "CHUNK      1.0  1  878d8276f9e332b22ebdbcd61384647d9d65df41790ff231fda7842081efb721  5     \r\n\r\nrandomshuffffffffffaçsldkfjapfghonapoegioqupfoiajpqwoip2938r9";
        String delete =     "DELETE     1.0  1  878d8276f9e332b22ebdbcd61384647d9d65df41790ff231fda7842081efb721        \r\n\r\n";
        String removed =    "REMOVED    1.0  1  878d8276f9e332b22ebdbcd61384647d9d65df41790ff231fda7842081efb721  5     \r\n\r\n";

        Protocol p = new Protocol(putchunk1);


        p.printMessage();
        System.out.println(p.toString());
    }
}