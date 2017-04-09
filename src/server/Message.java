package server;

import client.*;
import app.*;

public class Message {

  private Message() {}

  public String[] getMessage(){
    String file = ClientApplication.getFileName();
    String rep_degree = Integer.toString(ClientApplication.getRepDegree());
    String sub_protocol = ClientApplication.getSubProtocol();

    String[] msg = {file,rep_degree,sub_protocol};

    return msg;
  }
}
