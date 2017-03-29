package app;

import java.io.IOException;

public class ClientApplication {
  public static void main(String[] args) throws IOException{

    if(args.length < 4 || args.length > 6){
      System.out.println("Usage: java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>");
      System.exit(1);
    }

    String sub_protocol = args[3];

    if(sub_protocol == "BACKUP"){
      if(args.length != 6){
        System.out.println("Wrong number of arguments for BACKUP protocol.");
        System.exit(1);
      }

    }else if(sub_protocol == "RESTORE"){
      if(args.length != 5){
        System.out.println("Wrong number of arguments for RESTORE protocol.");
        System.exit(1);
      }

    }else if(sub_protocol == "DELETE"){
      if(args.length != 5){
        System.out.println("Wrong number of arguments for DELETE protocol.");
        System.exit(1);
      }

    }else if(sub_protocol == "RECLAIM"){
      if(args.length != 5){
        System.out.println("Wrong number of arguments for RECLAIM protocol.");
        System.exit(1);
      }

    }else if(sub_protocol == "STATE"){
      if(args.length != 4){
        System.out.println("Wrong number of arguments for STATE protocol.");
        System.exit(1);
      }


    }else{
      System.out.println("Invalid sub_protocol!");
      System.exit(1);
    }

  }
}
