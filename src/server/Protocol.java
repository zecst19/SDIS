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
    private int chunckNo;
    private int replicationDeg;
    private byte[] body;

    public Protocol(){}

    public Protocol(String message){

        String[] headAndBody = message.split("\r\n\r\n");
        String header = headAndBody[0];
        this.body = headAndBody[1].getBytes();

        String[] parts = header.split(" ");
        ArrayList<String> elements = new ArrayList<String>();//same as parts but without empty strings in between;
        for (int i = 0; i < parts.length; i++){
            if (!parts[i].equals("")){
                elements.add(parts[i]);
            }
        }

        //TODO: attribute value to messagetype, version, senderId and fileId

        switch (elements.get(0)){
            case PUTCHUNK:
                handlePutchunk(String header, byte[] data);
                break;
            case STORED:
                handleStored(String header);
                break;
            case GETCHUNK:
                handleGetchunk(String header);
                break;
            case CHUNK:
                handleChunk(String header, byte[] data);
                break;
            case DELETE:
                handleDelete(String header);
                break;
            case REMOVED:
                handleRemoved(String header);
                break;
            default:
                break;
        }





    }

    public void handlePutchunk(String header, byte[] data){

    }

    public void handleStored(String header){

    }

    public void handleGetchunk(String header){

    }

    public void handleChunk(String header, byte[] data){

    }

    public void handleDelete(String header){

    }

    public void handleRemoved(String header{

    }

    public static void main(String args[]){
        Protocol p = new Protocol("messagetype    version   restofshtuff\r\n\r\nrandomshuffffffffffaçsldkfjaponapoegioqupfoiajpqwoip2938r9");
    }
}