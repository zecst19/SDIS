import java.util.ArrayList;

public class Protocol {

    private final String PUTCHUNK = "PUTCHUNK";
    private final String STORED =   "STORED";
    private final String GETCHUNK = "GETCHUNK";
    private final String CHUNK =    "CHUNK";
    private final String DELETE =   "DELETE";
    private final String REMOVED =  "REMOVED";

    private String messageType;
    private String version;
    private int senderId;
    private String fileId;
    private int chunckNo = -1;
    private int replicationDeg = -1;
    private byte[] body = null;

    public Protocol(){}

    public Protocol(String message){

        String[] headAndBody = message.split("\r\n\r\n");
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

            this.chunckNo = Integer.parseInt(elements.get(4));

            if (this.messageType.equals(PUTCHUNK)){
                this.replicationDeg = Integer.parseInt(elements.get(5));
            }
        }
    }

    public String toString() {
        String hexBody = "";

        if (this.body != null){
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.body.length; i++){
                sb.append(String.format("%02X%s", this.body[i], (i < this.body.length - 1) ? "-" : ""));
            }
            hexBody = "\nBody: |" + sb.toString() + "|";
        }

        return "Message Type: " + this.messageType + "\nVersion: " + this.version +
                "\nSenderId: " + this.senderId + "\nFileId: |" + this.fileId + "|\nChunkNo: " +
                this.chunckNo + "\nReplicationDeg: " + this.replicationDeg + hexBody;

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

        Protocol p = new Protocol(putchunk2);


        System.out.println(p.toString());
    }
}