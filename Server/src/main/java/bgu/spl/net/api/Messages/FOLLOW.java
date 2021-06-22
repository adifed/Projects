package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;

public class FOLLOW extends Message {

    private enum State {
        GET_FOLLOW, GET_NUMBER, GET_USERS;
    }

    private State state;

    private int curr_user=0;
    private int curr_user_start = 0;
    private  int curr_off;

    private short follow;
    private short num_users = 0;
    private ArrayList<String> users_name = null;


    public FOLLOW()
    {
        super();
        opcode = Opcode.FOLLOW;
    }

    public FOLLOW(byte[] bytes)
    {
        super(bytes);
        opcode = Opcode.FOLLOW;
        state = State.GET_FOLLOW;
        curr_off = 1;

    }

    public int isFollow(){
        return (int)follow;
    }

    public ArrayList<String> getUserNameList(){
        return users_name;
    }

    @Override
    public byte[] encode() {

        return null;
    }

    @Override
    public Message decodeNextByte(byte next) {
        curr_off += 1; //len
        if (state == State.GET_FOLLOW) { //wait for follow/unfollow
            if (curr_off == 2) {
                follow = next;
                state = State.GET_NUMBER;
            }
        } else if (state == State.GET_NUMBER) { //wait for number
            if (curr_off == 4) {
                num_users = bytesToShort(bytes, 3); //place in bytes
                state = State.GET_USERS;
                curr_user = 0;
                curr_user_start = curr_off+1;
                users_name = new ArrayList<String>();
            }
        } else if (state == State.GET_USERS) {
            if (next == 0) {
                String name = new String(bytes, curr_user_start, curr_off - curr_user_start, StandardCharsets.UTF_8);
                users_name.add(name);
                curr_user += 1;
                if (curr_user == num_users)
                    return this; //message

                curr_user_start = curr_off+1;
            }

        }
        return null;
    }
}
