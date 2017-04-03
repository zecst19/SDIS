package client;

public class Peer {
	
	private static int server_id;
	
	private static String mc_addr;
	private static String mdb_addr;
	private static String mdr_addr;
	
	private static int mc_port;
	private static int mdb_port;
	private static int mdr_port;
	
	public Peer(int server_id, String mc_addr, int mc_port, String mdb_addr, int mdb_port, String mdr_addr, int mdr_port){
		
		this.server_id = server_id;
		
		this.mc_addr = mc_addr;
		this.mdb_addr = mdb_addr;
		this.mdr_addr = mdr_addr;
		
		this.mc_port = mc_port;
		this.mdb_port = mdb_port;
		this.mdr_port = mdr_port;

  }
}
