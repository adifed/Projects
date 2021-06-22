package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NOTIFICATION extends Message {


    public String username;
    public String content;
    public byte notify_type;


    public NOTIFICATION(String p, String username, String content)
    {
        super();
        opcode = Opcode.NOTIFICATION;
        this. username = username;
        this.content = content;
        if(p.equals("Public")){
            notify_type=1;
        }
        else
            notify_type=0;
    }

    public byte[] encode(){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

        try {
            outputStream.write(shortToBytes((short)opcode.ordinal()));
            outputStream.write(notify_type);
            outputStream.write(username.getBytes());
            outputStream.write(0);
            outputStream.write(content.getBytes());
            outputStream.write(0);
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


    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

}
