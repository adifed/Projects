package bgu.spl.net.api;

import bgu.spl.net.api.Messages.*;

import java.util.Arrays;


public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;

    private enum State { //the state of the message
        WAIT_OPCDOE, WAIT_BODY;
    }

    private State state;
    private Message message;

    public MessageEncoderDecoderImpl(){ //constructor
        state = State.WAIT_OPCDOE;
        message=null;
    }

    @Override
    public Message decodeNextByte(byte nextByte) {
        Message cmd = null;
        pushByte(nextByte); //push the byte to: bytes

        if (state == State.WAIT_OPCDOE) { //we haven't discovered opcode yet
            if (len >= 2) {  //we can find the opcode
                short s_opcode = bytesToShort(bytes, 0);  //find opcode
                if (s_opcode < 1 || s_opcode > 11)
                    s_opcode = 0;

                short opcode = s_opcode;
                state = State.WAIT_BODY; //now we found opcode so we change to state body

                switch (opcode) { //checks
                    case 1:
                        message = new REGISTER(bytes);
                        break;
                    case 2:
                        message = new LOGIN(bytes);
                        break;
                    case 3:
                        message = new LOGOUT(bytes);
                        cmd = message;
                        break;
                    case 4:
                        message = new FOLLOW(bytes);
                        break;
                    case 5:
                        message = new POST(bytes);
                        break;
                    case 6:
                        message = new PM(bytes);
                        break;
                    case 7:
                        message = new USERLIST(bytes);
                        cmd = message;
                        break;
                    case 8:
                        message = new STAT(bytes);
                        break;
                    default:
                        len = 0;
                        state = State.WAIT_OPCDOE;
                        break;
                }
            }
        } else {
            cmd = message.decodeNextByte(nextByte); //decode the byte in the message
        }

        // restart if found command
        if (cmd != null) {
            len = 0;
            state = State.WAIT_OPCDOE;
        }

        return cmd; //not a line yet
    }



    public static short bytesToShort(byte[] byteArr, int off) {
        short result = (short) ((byteArr[off] & 0xff) << 8);
        result += (short) (byteArr[off + 1] & 0xff);
        return result;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    @Override
    public byte[] encode(Message message) {
        byte[] arr = null;
        if (message instanceof ACK) {
            arr = message.encode();
        }

        if (message instanceof ERROR) {
            arr = message.encode();
        }

        if(message instanceof  NOTIFICATION){
            arr = message.encode();
        }

        return arr;
    }
}
