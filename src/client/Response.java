package client;

import java.io.UnsupportedEncodingException;

public class Response {
    private String message;
    private byte[] data;
    private State state;

    public Response(){}

    public Response(String message){
        this.message = message;
        this.data = null;
        this.state = null;
    }

    public Response(String message, byte[] data){
        this.message = message;
        this.data = data;
        this.state = null;
    }

    public Response(State state){
        this.message = null;
        this.data = null;
        this.state = state;
    }

    public String toString() {
        if (this.message == null){
            return this.state.toString();
        }
        else {
            if (this.data == null){
                return this.message;
            }
            else {
                String file = "";
                try {
                    file = new String(this.data, "ISO-8859-1");
                }
                catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
                return this.message + "\r\n\r\n" + file;
            }
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
