package bgu.spl.net.api;

import java.util.HashMap;
import java.util.Map;

public abstract class Message {
     public Opcode opcode;
     public byte[] bytes;

     public Message(byte[] bytes)
     {
          this.bytes = bytes;
     }

     public Message() {}


     public abstract byte[] encode();
     public abstract Message decodeNextByte(byte next);

     public enum Opcode {
          NONE(0),
          REGISTER(1), LOGIN(2), LOGOUT(3),
          FOLLOW(4), POST(5), PM(6),
          USERLIST(7), STAT(8), NOTIFICATION(9),
          ACK(10), ERROR(11);

          private int value;
          private static Map map = new HashMap<>();

          private Opcode(int value) {
               this.value = value;
          }

          static {
               for (Opcode pageType : Opcode.values()) {
                    map.put(pageType.value, pageType);
               }
          }

          public static Opcode valueOf(int pageType) {
               return (Opcode) map.get(pageType);
          }

          public short getValue() {
               return (short)value;
          }
     }

     public static byte[] hexStringToByteArray(String s) {
          int len = s.length();
          byte[] data = new byte[len / 2];
          for (int i = 0; i < len; i += 2) {
               data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                       + Character.digit(s.charAt(i+1), 16));
          }
          return data;
     }

     public static short bytesToShort(byte[] byteArr, int off)
     {
          short result = (short)((byteArr[off] & 0xff) << 8);
          result += (short)(byteArr[off+1] & 0xff);
          return result;
     }

     public byte[] shortToBytes(short num)
     {
          byte[] bytesArr = new byte[2];
          bytesArr[0] = (byte)((num >> 8) & 0xFF);
          bytesArr[1] = (byte)(num & 0xFF);
          return bytesArr;
     }


}
