package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.Database;
import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImp;
import bgu.spl.net.srv.Server;

public class ReactorMain {

    public static void main(String[] args) { // server in order to run the client!!
        Database db = new Database(); //one shared object

        Server.reactor(
                Integer.parseInt(args[1]),
                Integer.parseInt(args[0]), //port
                () -> new BidiMessagingProtocolImp(db), //protocol factory
                () -> new MessageEncoderDecoderImpl() {
               } //message encoder decoder factory
        ).serve();
    }
}
