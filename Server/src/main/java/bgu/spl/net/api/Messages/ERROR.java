package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ERROR extends Message {

    private int curr_off;

    public short err_opcode;


    public ERROR(int err_opcode) {
        opcode = Message.Opcode.ERROR;
        this.err_opcode = (short) err_opcode;

    }

    @Override
    public byte[] encode() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

        try {
            outputStream.write(shortToBytes((short)opcode.ordinal()));
            outputStream.write(shortToBytes(err_opcode));
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] msg = outputStream.toByteArray( );

        return msg;
    }



    @Override
    public Message decodeNextByte(byte next) {

        return null;
    }

}
