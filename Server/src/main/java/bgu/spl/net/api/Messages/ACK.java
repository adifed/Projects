package bgu.spl.net.api.Messages;


import bgu.spl.net.api.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class ACK extends Message {

    public short msg_opcode = 0;

    public short num_users = 0;
    public LinkedList<String> users_name = null;

    public short posts;
    public short following;
    public short followers;

    public ACK(int msg_opcode)
    {
        opcode = Opcode.ACK;
        this.msg_opcode =(short) msg_opcode;
    }

    public ACK(int msg_opcode, int num_users, LinkedList<String> users_name){
        opcode = Opcode.ACK;
        this.msg_opcode = (short)msg_opcode;
        this.num_users = (short)num_users;
        this.users_name = users_name;
    }

    public ACK (int msg_opcode, int posts, int followers, int following){
        opcode = Opcode.ACK;
        this.msg_opcode =(short) msg_opcode;
        this.posts = (short)posts;
        this.followers = (short)followers;
        this.following = (short) following;

    }

    public byte[] encode(){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

        try {
            outputStream.write(shortToBytes((short)opcode.ordinal()));
            outputStream.write(shortToBytes(msg_opcode));
            if ( msg_opcode == Opcode.FOLLOW.ordinal() || msg_opcode == Opcode.USERLIST.ordinal() )
            {
                outputStream.write(shortToBytes(num_users));
                for ( String name : users_name)
                {
                    outputStream.write(name.getBytes());
                    outputStream.write(0);
                }
            }
            else if ( msg_opcode == Opcode.STAT.ordinal() )
            {
                outputStream.write(shortToBytes(posts));
                outputStream.write(shortToBytes(followers));
                outputStream.write(shortToBytes(following));
            }
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
