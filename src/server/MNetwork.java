package server;

public class MNetwork {
    public String[] addresses;      //{228.0.0.0,     228.1.1.1,   228.2.2.2}
    public int[] ports;             //{4678,          3215,        9876}
    public int peerID;
    public final int CHUNK_SIZE = 64000;
    public final String VERSION = "1.0";
    public final int MC = 0, MDB = 1, MDR = 2;

    public MNetwork(int peer_id, String mc_addr, int mc_port, String mdb_addr, int mdb_port, String mdr_addr, int mdr_port){
        this.peerID = peer_id;
        this.addresses[0] = mc_addr;
        this.addresses[1] = mdb_addr;
        this.addresses[2] = mdr_addr;
        this.ports[0] = mc_port;
        this.ports[1] = mdb_port;
        this.ports[2] = mdr_port;
    }
}
