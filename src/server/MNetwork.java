package server;

public class MNetwork {
    public static String[] addresses;      //{228.0.0.0,     228.1.1.1,   228.2.2.2}
    public static int[] ports;             //{4678,          3215,        9876}
    public int peerID;
    public final int CHUNK_SIZE = 64000;
    public final int MAX_HEADER_SIZE = 300;//aproximation because there is no limit in spaces in between arguments
    public final String VERSION = "1.0";
    public int MC, MDB, MDR;

    public MNetwork(int peer_id, String mc_addr, int mc_port, String mdb_addr, int mdb_port, String mdr_addr, int mdr_port){
        this.peerID = peer_id;
        this.addresses = new String[3];
        this.ports = new int[3];
        this.addresses[0] = mc_addr;
        this.addresses[1] = mdb_addr;
        this.addresses[2] = mdr_addr;
        this.ports[0] = mc_port;
        this.ports[1] = mdb_port;
        this.ports[2] = mdr_port;

        this.MC = 0;
        this.MDB = 1;
        this.MDR = 2;
    }

    public String toString(){
        return "PeerID: " + this.peerID + "\nMC<addr:port> " + this.addresses[MC] + ":" + this.ports[MC]
                + "\nMDB<addr:port> " + this.addresses[MDB] + ":" + this.ports[MDB]
                + "\nMDR<addr:port> " + this.addresses[MDR] + ":" + this.ports[MDR];
    }
}
