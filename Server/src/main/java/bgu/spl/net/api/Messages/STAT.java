package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class STAT extends Message {

    private enum State {
        GET_USERNAME;
    }

    private State state;

    private int username_off;
    private int curr_off;

    public String username=null;

    public STAT()
    {
        super();
        opcode = Opcode.STAT;
    }

    public STAT(byte[] bytes)
    {
        super(bytes);
        opcode = Opcode.STAT;
        state = State.GET_USERNAME;
        curr_off = 1;
        username_off = curr_off+1;
    }

    public String getUsername(){
        return username;
    }

    @Override
    public byte[] encode() {
      return null;
    }

    @Override
    public Message decodeNextByte(byte next) {
        curr_off+=1;
        if ( state== State.GET_USERNAME && next == 0 )
        {
            username = new String(bytes, username_off, curr_off- username_off, StandardCharsets.UTF_8);
            return this;
        }
        return null;
    }

}

