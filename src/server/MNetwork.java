package server;

/**
 * Created by Gustavo on 24/03/2017.
 */
public class MNetwork {
    public String addrMC, addrMDB, addrMDR;    //228.0.0.0     228.1.1.1   228.2.2.2
    public int portMC, portMDB, portMDR;       //4678          3215        9876

    public MNetwork(String mc_addr, int mc_port, String mdb_addr, int mdb_port, String mdr_addr, int mdr_port){
        this.addrMC = mc_addr;
        this.portMC = mc_port;
        this.addrMDB = mdb_addr;
        this.portMDB = mdb_port;
        this.addrMDR = mdr_addr;
        this.portMDR = mdr_port;
    }
}
