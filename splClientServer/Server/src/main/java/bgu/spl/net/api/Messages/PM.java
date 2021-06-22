package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PM extends Message {

    private enum State {
        GET_USERNAME, GET_CONTENT;
    }

    private State state;

    private int user_off;
    private int cont_off;
    private int curr_off;

    public String username=null;
    public String content=null;

    public PM()
    {
        super();
        opcode = Opcode.PM;
    }

    public PM(byte[] bytes)
    {
        super(bytes);
        opcode = Opcode.PM;
        state = State.GET_USERNAME;
        curr_off = 1;
        user_off = curr_off+1;
        cont_off = curr_off+1;

    }

    public String getUserName(){
        return username;
    }

    public String getContent(){
        return content;
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
            username = new String(bytes, user_off, curr_off-user_off, StandardCharsets.UTF_8);
            cont_off = curr_off+1;
            state = State.GET_CONTENT;
        }
        else if (  state== State.GET_CONTENT && next == 0 )
        {
            content = new String(bytes, cont_off, curr_off-cont_off, StandardCharsets.UTF_8);
            return this;
        }
        return null;
    }

}


