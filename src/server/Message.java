package server;

import client.*;
import app.*;

public class Message {

  private Message() {}

  public String[] getMessage(){
    String file = ClientApplication.file;
    String rep_degree = Integer.toString(ClientApplication.rep_degree);
    String sub_protocol = ClientApplication.sub_protocol;

    String[] msg = {file,rep_degree,sub_protocol};

    return msg;
  }
}
