package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.Database;
import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImp;
import bgu.spl.net.srv.Server;

public class TPCMain {

    public static void main(String[] args) { // server in order to run the client!!
        Database db = new Database(); //one shared object

// you can use any server...
        //
        Server.threadPerClient(
                Integer.parseInt(args[0]), //port
                () -> new BidiMessagingProtocolImp(db), //protocol factory
                () -> new MessageEncoderDecoderImpl() {
                } //message encoder decoder factory
        ).serve();

    }
}
