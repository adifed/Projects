package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;
import java.util.HashMap;
import java.util.Map;

public class ConnectionsImpl<T> implements Connections<T> {

    private HashMap<Integer,ConnectionHandler<T>> idANDclients; //map which holds id for each active client
    private int connectionId;

    public ConnectionsImpl(){
        this.idANDclients = new HashMap<>();
        this.connectionId = 1;
    }

    public void AddClient(ConnectionHandler<T> c){
        idANDclients.put(getConnectionId(),c);
        updateConnectionId();
    }

    public int getConnectionId() {
        return connectionId;
    }

    public void updateConnectionId() {
        connectionId++;
    }

    @Override
    public boolean send(int connectionId, Object msg) {
        synchronized (idANDclients) {
            if (idANDclients.containsKey(connectionId)) {
                idANDclients.get(connectionId).send((T) msg);
                return true;
            }
        }
        return false;
    }

    @Override
    public void broadcast(Object msg) {
        for (Map.Entry<Integer,ConnectionHandler<T>> c:idANDclients.entrySet()) {
            c.getValue().send((T)msg);
        }
    }

}