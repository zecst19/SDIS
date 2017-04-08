package fileSystem;

import server.protocol.Protocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FSChunk {
    private String fileID;
    private int chunkNo;
    private byte[] body;
    private int replication;

    public FSChunk(){}

    public FSChunk(String filename){
        File file = new File(filename);
        try {
            FileInputStream fis = new FileInputStream(file);

            this.body = new byte[(int) file.length()];
            fis.read(body);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        String[] parts = filename.split("-");
        this.fileID = parts[0];
        this.chunkNo = Integer.parseInt(parts[1]);

        this.replication = 0;

    }

    public FSChunk(Protocol p){
        this.fileID = p.getFileId();
        this.chunkNo = p.getChunkNo();
        this.body = p.getBody();
        this.replication = 0;
    }

    public FSChunk(String fid, int cn, byte[] b){
        this.fileID = fid;
        this.chunkNo = cn;
        this.body = b;
        this.replication = 0;
    }

    public String getFileID() {
        return fileID;
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public byte[] getBody() {
        return body;
    }

    public int getReplication() {
        return replication;
    }

}
