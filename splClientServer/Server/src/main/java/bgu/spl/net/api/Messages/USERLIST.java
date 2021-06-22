package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class USERLIST extends Message {

    private int curr_off;

    public USERLIST() {
        super();
        opcode = Opcode.USERLIST;
    }

    public USERLIST(byte[] bytes)
    {
        super(bytes);
        opcode = Opcode.USERLIST;
        curr_off = 1;
    }

    @Override
    public byte[] encode() {
      return null;
    }


    @Override
    public Message decodeNextByte(byte next) {
        return this;
    }

}

