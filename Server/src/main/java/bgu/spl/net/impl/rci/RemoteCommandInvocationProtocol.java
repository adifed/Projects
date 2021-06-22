package bgu.spl.net.impl.rci;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;

import java.io.Serializable;

public class RemoteCommandInvocationProtocol<T> implements BidiMessagingProtocol<Serializable> {

    private T arg;
    private int connectionId;

    private  Connections<Serializable> connections;

    public RemoteCommandInvocationProtocol(T arg) {
        this.arg = arg;
    }

    @Override
    public void process(Serializable msg) {
        Serializable ret_msg =  ((Command) msg).execute(arg);
        connections.send(connectionId, ret_msg);
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }

    @Override
    public void start(int connectionId, Connections<Serializable> connections)
    {

        this.connections = connections;
        this.connectionId = connectionId;
    }
}
