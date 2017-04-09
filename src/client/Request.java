package client;

import java.util.ArrayList;

public class Request {
    private String type;
    private String filename;
    private int replicationDegree;
    private int space;

    public Request(){}

    public Request(String type, String filename, int replicationDegree){
        this.type = type;
        this.filename = filename;
        this.replicationDegree = replicationDegree;
        this.space = 0;
    }

    public Request(String type, String filename){
        this.type = type;
        this.filename = filename;
        this.replicationDegree = 0;
        this.space = 0;
    }

    public Request(String type, int space){
        this.type = type;
        this.filename = "";
        this.replicationDegree = 0;
        this.space = space;
    }

    public Request(String type){
        this.type = type;
        this.filename = "";
        this.replicationDegree = 0;
        this.space = 0;
    }

    public void fromString(String req){
        String[] parts = req.split(" ");
        ArrayList<String> elements = new ArrayList<String>();//same as parts but without empty strings in between;
        for (int i = 0; i < parts.length; i++){
            if (!parts[i].equals("")){
                elements.add(parts[i]);
            }
        }

        this.type = elements.get(0);

        switch (this.type){
            case "BACKUP":
                this.filename = elements.get(1);
                this.replicationDegree = Integer.parseInt(elements.get(2));
                break;
            case "RESTORE":
                this.filename = elements.get(1);
                break;
            case "DELETE":
                this.filename = elements.get(1);
                break;
            case "RECLAIM":
                this.space = Integer.parseInt(elements.get(1));
                break;
            case "STATE":
                break;
            default:
                break;
        }
    }

    public String toString(){
        if (this.filename.equals("")){
            if (this.space == 0){
                return this.type;
            }
            else {
                return this.type + " " + this.space;
            }
        }
        else {
            if (replicationDegree == 0){
                return this.type + " " + this.filename;
            }
            else {
                return this.type + " " + this.filename + " " + this.replicationDegree;
            }
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getReplicationDegree() {
        return replicationDegree;
    }

    public void setReplicationDegree(int replicationDegree) {
        this.replicationDegree = replicationDegree;
    }

    public int getSpace() {
        return space;
    }

    public void setSpace(int space) {
        this.space = space;
    }
}
