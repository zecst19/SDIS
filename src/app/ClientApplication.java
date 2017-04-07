package app;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.IOException;

public class ClientApplication {
  public static void main(String[] args) throws IOException{

    if(args.length < 4 || args.length > 6){
      System.out.println("Usage: java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>");
      System.exit(1);
    }

    String sub_protocol = args[3];

    Registry registry = null;
    registry = LocateRegistry.getRegistry();
    Interface interface = null;
    interface = (Interface) registry.lookup(peer_ap);


    if(sub_protocol == "BACKUP"){
      if(args.length != 6){
        System.out.println("Wrong number of arguments for BACKUP protocol.");
        System.exit(1);
      }

      msg = interface.backup();
      System.out.println(msg);

    }else if(sub_protocol == "RESTORE"){
      if(args.length != 5){
        System.out.println("Wrong number of arguments for RESTORE protocol.");
        System.exit(1);
      }

      msg = interface.restore();
      System.out.println(msg);

    }else if(sub_protocol == "DELETE"){
      if(args.length != 5){
        System.out.println("Wrong number of arguments for DELETE protocol.");
        System.exit(1);
      }

      msg = interface.delete();
      System.out.println(msg);

    }else if(sub_protocol == "RECLAIM"){
      if(args.length != 5){
        System.out.println("Wrong number of arguments for RECLAIM protocol.");
        System.exit(1);
      }

      msg = interface.reclaim();
      System.out.println(msg);

    }else if(sub_protocol == "STATE"){
      if(args.length != 4){
        System.out.println("Wrong number of arguments for STATE protocol.");
        System.exit(1);
      }

      msg = interface.state();
      System.out.println(msg);

    }else{
      System.out.println("Invalid sub_protocol!");
      System.exit(1);
    }

  }
}
