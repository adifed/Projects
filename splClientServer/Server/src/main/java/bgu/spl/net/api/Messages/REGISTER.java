package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class REGISTER extends Message {

    private enum State {
        GET_USERNAME, GET_PASWORD;
    }

    private State state;

    private int user_off;
    private int pass_off;
    private int curr_off;

    public String username=null;
    public String password=null;

    public REGISTER()
    {
        super();
        opcode = Message.Opcode.REGISTER;
    }


    public REGISTER(byte[] bytes)
    {
        super(bytes);
        opcode = Message.Opcode.REGISTER; //opcode number of register(enum)
        state = State.GET_USERNAME; //state
        curr_off = 1; //current place
        user_off = curr_off+1;
        pass_off = curr_off+1;

    }

    public String getPassword() {
        return password;
    }

    public String getUsername(){
        return username;
    }

    @Override
    public byte[] encode()  { //not important- just because the generics
        return null;
    }

    @Override
    public Message decodeNextByte(byte next) {
        curr_off+=1;
        if ( state== State.GET_USERNAME && next == 0 ) //if we havent found the username
        {
            //find
            username = new String(bytes, user_off, curr_off-user_off, StandardCharsets.UTF_8);
            pass_off = curr_off+1;
            state = State.GET_PASWORD;
        }
        else if (  state== State.GET_PASWORD && next == 0 ) //if we havent found the password
        {
            //find
            password = new String(bytes, pass_off, curr_off-pass_off, StandardCharsets.UTF_8);
            return this;
        }
        return null;
    }

}
