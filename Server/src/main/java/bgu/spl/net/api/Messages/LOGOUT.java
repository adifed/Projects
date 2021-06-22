package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LOGOUT extends Message {

    private int curr_off;

    public LOGOUT()
    {
        super();
        opcode = Message.Opcode.LOGOUT;
    }

    public LOGOUT(byte[] bytes)
    {
        super(bytes);
        opcode = Message.Opcode.LOGOUT;
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
