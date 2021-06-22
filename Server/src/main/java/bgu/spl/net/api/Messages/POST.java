package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class POST extends Message {

    private enum State {
        GET_CONTENT;
    }

    private State state;

    private int cont_off;
    private int curr_off;

    public String content;

    public POST()
    {
        super();
        opcode = Opcode.POST;
    }

    public POST(byte[] bytes)
    {
        super(bytes);
        opcode = Opcode.POST;
        state = State.GET_CONTENT;
        curr_off = 1;
        cont_off = curr_off+1;
    }

    @Override
    public byte[] encode() {
       return null;
    }

    public String getContent(){
        return this.content;
    }

    @Override
    public Message decodeNextByte(byte next) {
        curr_off+=1;
        if ( state== State.GET_CONTENT && next == 0 )
        {
            content = new String(bytes, cont_off, curr_off-cont_off, StandardCharsets.UTF_8);
            return this;
        }
        return null;
    }

}

